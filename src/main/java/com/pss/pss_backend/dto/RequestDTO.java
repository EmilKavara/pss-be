package com.pss.pss_backend.dto;

public class RequestDTO {
    private Long requestId;
    private Long rideId;
    private String passengerName;
    private String passengerEmail;
    private String status;

    public RequestDTO(Long requestId, Long rideId, String passengerName, String passengerEmail, String status) {
        this.requestId = requestId;
        this.rideId = rideId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.status = status;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
