package com.pss.pss_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private List<RideHistoryDTO> rideHistory;
}
