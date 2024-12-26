package com.pss.pss_backend.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {

    private String username;

    private String password;

    private String role;

    private String fullName;

    private String email;

}
