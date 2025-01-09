package com.pss.pss_backend.dto;

import com.pss.pss_backend.model.Ride;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RideDTO {
    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    @Future
    private LocalDateTime departureTime;

    @NotNull
    private Integer availableSeats;

    private String status;

    private Long id;

    private String driverName;

    public RideDTO() {}

    public RideDTO(Ride ride) {
        this.id = ride.getRideId();
        this.origin = ride.getOrigin();
        this.destination = ride.getDestination();
        this.departureTime = ride.getDepartureTime();
        this.availableSeats = ride.getAvailableSeats();
        this.status = ride.getStatus();
    }
}
