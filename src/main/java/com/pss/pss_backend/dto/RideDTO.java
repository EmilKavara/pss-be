package com.pss.pss_backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RideDTO {
    @NotBlank
    private String startLocation;

    @NotBlank
    private String destination;

    @NotNull
    @Future
    private LocalDateTime departureTime;

    @NotNull
    private Integer availableSeats;
}
