package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.RideDTO;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.repository.RideRepository;
import com.pss.pss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    public void createRide(RideDTO rideDTO, String username) {
        // Get the user (driver) from the username
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate the departure time
        if (rideDTO.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Departure time cannot be in the past.");
        }

        LocalDateTime maxAllowedTime = LocalDateTime.now().plusMonths(6);
        if (rideDTO.getDepartureTime().isAfter(maxAllowedTime)) {
            throw new IllegalArgumentException("Departure time cannot be more than 6 months in advance.");
        }

        // Create the ride and set the driver
        Ride ride = new Ride();
        ride.setOrigin(rideDTO.getStartLocation());
        ride.setDestination(rideDTO.getDestination());
        ride.setDepartureTime(rideDTO.getDepartureTime());
        ride.setAvailableSeats(rideDTO.getAvailableSeats());
        ride.setDriver(driver); // Set the driver based on the authenticated user
        rideRepository.save(ride);
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
        rideRepository.deleteById(rideId);
    }
}
