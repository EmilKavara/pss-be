package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.RequestDTO;
import com.pss.pss_backend.dto.RideDTO;
import com.pss.pss_backend.model.*;
import com.pss.pss_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RidePassengerService {

    @Autowired
    private RidePassengerRepository ridePassengerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    public void addPassengerToRide(RidePassenger ridePassenger) {
        ridePassenger.setStatus("PENDING");
        ridePassengerRepository.save(ridePassenger);
    }

    public List<RidePassenger> getPassengersForRide(Long rideId) {
        return ridePassengerRepository.findByRide_RideIdAndRoleAndStatus(rideId, "passenger", "APPROVED");
    }

    public RidePassenger getDriverForRide(Long rideId) {
        return ridePassengerRepository.findByRide_RideIdAndRole(rideId, "driver")
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Driver not found for this ride"));
    }

    public List<RidePassenger> getRidesForUser(Long userId) {
        return ridePassengerRepository.findByUser_UserId(userId);
    }

    public List<RequestDTO> getRequestsForDriver(Long driverId) {
        List<RidePassenger> requests = ridePassengerRepository.findByRide_Driver_UserIdAndRoleAndStatus(driverId, "passenger", "PENDING");

        return requests.stream()
                .map(request -> new RequestDTO(
                        request.getId(),
                        request.getRide().getRideId(),
                        request.getUser().getFullName(),
                        request.getUser().getEmail(),
                        request.getStatus()
                ))
                .collect(Collectors.toList());
    }


    public void handleRequest(Long requestId, boolean isAccepted) {
        RidePassenger request = ridePassengerRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String userEmail = request.getUser().getEmail();

        if (isAccepted) {
            request.setStatus("APPROVED");

            Ride ride = request.getRide();
            if (ride.getAvailableSeats() > 0) {
                ride.setAvailableSeats(ride.getAvailableSeats() - 1);
            } else {
                throw new RuntimeException("No available seats");
            }

            ridePassengerRepository.save(request);

            Reservation reservation = new Reservation();
            reservation.setRide(ride);
            reservation.setPassenger(request.getUser());
            reservation.setBookedSeats(1);
            reservation.setReservationStatus("CONFIRMED");
            reservationRepository.save(reservation);

            String subject = "Ride Request Approved";
            String body = buildApprovalEmailBody(request.getUser(), ride);
            emailService.sendConfirmationEmail(userEmail, subject, body);

        } else {
            request.setStatus("DENIED");

            Notification notification = new Notification();
            notification.setUser(request.getUser());
            notification.setMessage(String.format(
                    "Your request for the ride from %s to %s was denied.",
                    request.getRide().getOrigin(),
                    request.getRide().getDestination()
            ));
            notification.setStatus("UNREAD");
            notificationRepository.save(notification);

            ridePassengerRepository.save(request);
        }
    }

    private String buildApprovalEmailBody(User passenger, Ride ride) {
        return String.format(
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        "table { width: 100%%; border-collapse: collapse; margin-top: 20px; }" +
                        "th, td { padding: 10px; text-align: left; border: 1px solid #ddd; }" +
                        "th { background-color: #f4f4f4; font-weight: bold; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h2>Dear %s,</h2>" +
                        "<p>We are delighted to inform you that your request to join the ride has been approved!</p>" +
                        "<h3>Ride Details</h3>" +
                        "<table>" +
                        "<tr><th>Start Location</th><td>%s</td></tr>" +
                        "<tr><th>Destination</th><td>%s</td></tr>" +
                        "<tr><th>Departure Time</th><td>%s</td></tr>" +
                        "</table>" +
                        "<p>Please make sure to be on time at the departure location. Feel free to contact the driver for any additional details.</p>" +
                        "<p>We wish you a pleasant journey!</p>" +
                        "<p><strong>Best regards,</strong><br>The Ride Team</p>" +
                        "</body>" +
                        "</html>",
                passenger.getFullName(),
                ride.getOrigin(),
                ride.getDestination(),
                ride.getDepartureTime()
        );
    }

    public void sendRequest(Long rideId, Long passengerId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));
        User user = userRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + passengerId));

        RidePassenger request = new RidePassenger();
        request.setRide(ride);
        request.setUser(user);
        request.setRole("PASSENGER");
        request.setStatus("PENDING");

        ridePassengerRepository.save(request);
    }

    public List<RideDTO> getBookedRidesForUser(Long userId) {
        List<RidePassenger> ridePassengers = ridePassengerRepository.findByUser_UserIdAndStatusIn(userId, Arrays.asList("APPROVED", "PENDING"));

        return ridePassengers.stream()
                .map(ridePassenger -> {
                    RideDTO rideDTO = new RideDTO(ridePassenger.getRide());
                    rideDTO.setStatus(ridePassenger.getStatus());
                    rideDTO.setDriverName(ridePassenger.getRide().getDriver().getFullName());
                    return rideDTO;
                })
                .collect(Collectors.toList());
    }





}
