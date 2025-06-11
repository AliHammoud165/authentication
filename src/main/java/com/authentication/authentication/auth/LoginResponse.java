package com.authentication.authentication.auth;

import com.authentication.authentication.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String first_name;
    private String last_name;
    private String email;
    private String phone_number;
    private Role role;
}
