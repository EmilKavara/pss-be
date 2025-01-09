package com.pss.pss_backend.dto;

public class RideGraphData {
    private String timePeriod;
    private long rideCount;

    public RideGraphData(String timePeriod, long rideCount) {
        this.timePeriod = timePeriod;
        this.rideCount = rideCount;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public long getRideCount() {
        return rideCount;
    }

    public void setRideCount(long rideCount) {
        this.rideCount = rideCount;
    }
}

