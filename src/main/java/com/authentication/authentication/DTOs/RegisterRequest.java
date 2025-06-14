package com.authentication.authentication.DTOs;

import com.authentication.authentication.Enums.AuthType;
import com.authentication.authentication.Enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private AuthType register_type;
    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String password;
    private String phone_number;
    private Role role;
}
