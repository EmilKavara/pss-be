package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.RideDTO;
import com.pss.pss_backend.dto.RideStatusDTO;
import com.pss.pss_backend.exception.InvalidRideException;
import com.pss.pss_backend.exception.RideNotFoundException;
import com.pss.pss_backend.exception.UserNotFoundException;
import com.pss.pss_backend.model.Notification;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.RidePassenger;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.repository.RidePassengerRepository;
import com.pss.pss_backend.repository.RideRepository;
import com.pss.pss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RidePassengerRepository ridePassengerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    public void createRide(RideDTO rideDTO, String username) {
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateDepartureTime(rideDTO.getDepartureTime());

        Ride ride = new Ride();
        ride.setOrigin(rideDTO.getStartLocation());
        ride.setDestination(rideDTO.getDestination());
        ride.setDepartureTime(rideDTO.getDepartureTime());
        ride.setAvailableSeats(rideDTO.getAvailableSeats());
        ride.setDriver(driver);

        Ride savedRide = rideRepository.save(ride);

        RidePassenger ridePassenger = new RidePassenger();
        ridePassenger.setRide(savedRide);
        ridePassenger.setUser(driver);
        ridePassenger.setRole("driver");
        ridePassenger.setStatus("APPROVED");
        ridePassengerRepository.save(ridePassenger);

        sendRideConfirmationEmail(savedRide);
    }

    private void validateDepartureTime(LocalDateTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new InvalidRideException("Departure time cannot be in the past.");
        }
        if (departureTime.isAfter(LocalDateTime.now().plusMonths(6))) {
            throw new InvalidRideException("Departure time cannot be more than 6 months in advance.");
        }
    }

    private void sendRideConfirmationEmail(Ride ride) {
        String emailSubject = "Ride Confirmation";
        String emailBody = "<html>"
                + "<head>"
                + "<style>"
                + "table { width: 100%; border-collapse: collapse; }"
                + "th, td { padding: 8px; text-align: left; border: 1px solid #ddd; }"
                + "th { background-color: #f4f4f4; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<h2>Ride Confirmation</h2>"
                + "<p>Dear " + ride.getDriver().getFullName() + ",</p>"
                + "<p>Thank you for creating a ride! Your ride details are as follows:</p>"
                + "<table>"
                + "<tr><th>Start Location</th><td>" + ride.getOrigin() + "</td></tr>"
                + "<tr><th>Destination</th><td>" + ride.getDestination() + "</td></tr>"
                + "<tr><th>Departure Time</th><td>" + ride.getDepartureTime() + "</td></tr>"
                + "<tr><th>Available Seats</th><td>" + ride.getAvailableSeats() + "</td></tr>"
                + "</table>"
                + "<p>If you need to make any changes or cancellations, please contact us.</p>"
                + "<p>Best regards,<br>Your Ride Team</p>"
                + "</body>"
                + "</html>";

        emailService.sendConfirmationEmail(ride.getDriver().getEmail(), emailSubject, emailBody);
    }

    public Optional<Ride> getRideById(Long rideId) {
        return rideRepository.findById(rideId);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public void deleteRide(Long rideId) {
        ridePassengerRepository.deleteByRide_RideId(rideId);
        rideRepository.deleteById(rideId);
    }

    public List<RideStatusDTO> getRidesByStatusAndUser(String status, String username) {
        List<Ride> rides;

        if (status.equalsIgnoreCase("all")) {
            rides = rideRepository.findByDriver_Username(username);
        } else {
            rides = rideRepository.findByStatusAndDriver_Username(status, username);
        }

        return rides.stream()
                .map(ride -> new RideStatusDTO(
                        ride.getRideId(),
                        ride.getOrigin(),
                        ride.getDestination(),
                        ride.getDepartureTime(),
                        ride.getStatus(),
                        ride.getAvailableSeats(),
                        ride.getDriver().getFullName()
                ))
                .collect(Collectors.toList());
    }


    public void cancelRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));
        ride.setStatus("CANCELLED");
        rideRepository.save(ride);

        notificationService.notifyPassengers(ride, "The ride has been cancelled.");
    }

    public void reportDelay(Long rideId, LocalDateTime newDepartureTime) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));
        ride.setDepartureTime(newDepartureTime);
        rideRepository.save(ride);

        notificationService.notifyPassengers(ride, "The ride is delayed. New departure time: " + newDepartureTime);
    }

    public void passengerCancelRide(Long rideId, Long passengerId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        RidePassenger ridePassenger = ridePassengerRepository
                .findByRide_RideIdAndUser_UserIdAndRole(rideId, passengerId, "passenger")
                .orElseThrow(() -> new RuntimeException("Passenger not found for this ride"));

        if (!ridePassenger.getStatus().equals("APPROVED")) {
            throw new RuntimeException("Passenger cannot cancel this ride. Status: " + ridePassenger.getStatus());
        }

        ridePassengerRepository.delete(ridePassenger);

        ride.setAvailableSeats(ride.getAvailableSeats() + 1);
        rideRepository.save(ride);

        String notificationMessage = String.format(
                "Passenger %s has canceled their participation in the ride from %s to %s.",
                ridePassenger.getUser().getFullName(), ride.getOrigin(), ride.getDestination()
        );
        Notification notification = new Notification();
        notification.setUser(ride.getDriver());
        notification.setMessage(notificationMessage);
        notification.setStatus("UNREAD");
        notificationService.saveNotification(notification);
    }

    public List<RideStatusDTO> getFilteredRides(String status, String destination, int minSeats, String sortBy) {
        // Fetch rides based on status
        List<Ride> rides = rideRepository.findByStatus(status);

        // Filter by destination if provided
        if (destination != null && !destination.isEmpty()) {
            rides = rides.stream()
                    .filter(ride -> ride.getDestination().equalsIgnoreCase(destination))
                    .collect(Collectors.toList());
        }

        // Filter by minimum available seats
        rides = rides.stream()
                .filter(ride -> ride.getAvailableSeats() >= minSeats)
                .collect(Collectors.toList());

        // Sort based on the sortBy parameter
        if (sortBy != null) {
            switch (sortBy) {
                case "availableSeats":
                    rides.sort(Comparator.comparingInt(Ride::getAvailableSeats).reversed());
                    break;
                case "departureTime":
                    rides.sort(Comparator.comparing(Ride::getDepartureTime));
                    break;
                default:
                    break; // No sorting if sortBy is empty or invalid
            }
        }

        // Convert to DTOs
        return rides.stream()
                .map(ride -> new RideStatusDTO(
                        ride.getRideId(),
                        ride.getOrigin(),
                        ride.getDestination(),
                        ride.getDepartureTime(),
                        ride.getStatus(),
                        ride.getAvailableSeats(),
                        ride.getDriver().getFullName()
                ))
                .collect(Collectors.toList());
    }


}

