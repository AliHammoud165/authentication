package com.authentication.authentication.DTOs;

import com.authentication.authentication.Enums.AuthType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private AuthType login_type;
    private String identifier;
    private String password;
}
