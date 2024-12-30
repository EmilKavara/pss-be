package com.pss.pss_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideStatusDTO {

        private Long id;
        private String origin;
        private String destination;
        private LocalDateTime departureTime;
        private String status;
        private Integer availableSeats;
        private String driverName;

        public RideStatusDTO(Long id, String origin, String destination, LocalDateTime departureTime, String status, Integer availableSeats, String driverName) {
            this.id = id;
            this.origin = origin;
            this.destination = destination;
            this.departureTime = departureTime;
            this.status = status;
            this.availableSeats = availableSeats;
            this.driverName = driverName;
        }

}