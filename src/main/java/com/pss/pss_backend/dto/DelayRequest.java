package com.pss.pss_backend.dto;

import java.time.LocalDateTime;

public class DelayRequest {

    private LocalDateTime newDepartureTime;

    public LocalDateTime getNewDepartureTime() {
        return newDepartureTime;
    }

    public void setNewDepartureTime(LocalDateTime newDepartureTime) {
        this.newDepartureTime = newDepartureTime;
    }
}

