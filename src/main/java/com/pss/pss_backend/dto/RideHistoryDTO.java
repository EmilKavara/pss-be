package com.pss.pss_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideHistoryDTO {
    private Long rideId;
    private LocalDateTime departureTime;
    private String destination;
    private String status;
    private String origin;
}
