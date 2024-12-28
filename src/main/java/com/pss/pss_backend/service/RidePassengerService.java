package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.RequestDTO;
import com.pss.pss_backend.model.Notification;
import com.pss.pss_backend.model.Reservation;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.RidePassenger;
import com.pss.pss_backend.repository.NotificationRepository;
import com.pss.pss_backend.repository.ReservationRepository;
import com.pss.pss_backend.repository.RidePassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RidePassengerService {

    @Autowired
    private RidePassengerRepository ridePassengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    public void addPassengerToRide(RidePassenger ridePassenger) {
        // When adding a passenger, set status to "pending"
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
            // Change the status of the RidePassenger to "approved"
            request.setStatus("APPROVED");

            // Handle available seats decrement logic
            Ride ride = request.getRide();
            if (ride.getAvailableSeats() > 0) {
                ride.setAvailableSeats(ride.getAvailableSeats() - 1);
            } else {
                throw new RuntimeException("No available seats");
            }

            // Save the RidePassenger with updated status
            ridePassengerRepository.save(request);

            // Create a Reservation for the approved passenger
            Reservation reservation = new Reservation();
            reservation.setRide(ride);
            reservation.setPassenger(request.getUser()); // The user who requested the ride
            reservation.setBookedSeats(1); // Assuming 1 seat is booked for the passenger
            reservation.setReservationStatus("CONFIRMED"); // Set reservation status to "Confirmed"
            reservationRepository.save(reservation); // Save the reservation

            // Send approval email
            String subject = "Ride Request Approved";
            String body = String.format(
                    "Dear %s,\n\nYour request to join the ride from %s to %s has been approved. "
                            + "The ride departs at %s. Please contact the driver for further details.\n\nBest regards,\nRide Team",
                    request.getUser().getFullName(), ride.getOrigin(), ride.getDestination(), ride.getDepartureTime()
            );
            emailService.sendConfirmationEmail(userEmail, subject, body);

        } else {
            // Change the status of the RidePassenger to "denied"
            request.setStatus("DENIED");

            // Create a denial notification for the user
            Notification notification = new Notification();
            notification.setUser(request.getUser());
            notification.setMessage(String.format(
                    "Your request for the ride from %s to %s was denied.",
                    request.getRide().getOrigin(),
                    request.getRide().getDestination()
            ));
            notification.setStatus("UNREAD"); // Set the notification status as "unread"
            notificationRepository.save(notification); // Save the notification

            // Save the RidePassenger with updated status
            ridePassengerRepository.save(request);
        }
    }



}
