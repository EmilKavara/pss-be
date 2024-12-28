package com.pss.pss_backend.dto;

import java.time.LocalDateTime;

public class RideStatusDTO {

        private Long id;
        private String origin;
        private String destination;
        private LocalDateTime departureTime;
        private String status;
        private Integer availableSeats;

        // Constructor
        public RideStatusDTO(Long id, String origin, String destination, LocalDateTime departureTime, String status, Integer availableSeats) {
            this.id = id;
            this.origin = origin;
            this.destination = destination;
            this.departureTime = departureTime;
            this.status = status;
            this.availableSeats = availableSeats;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(LocalDateTime departureTime) {
            this.departureTime = departureTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getAvailableSeats() {
            return availableSeats;
        }

        public void setAvailableSeats(Integer availableSeats) {
            this.availableSeats = availableSeats;
        }

}
