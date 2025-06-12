package com.authentication.authentication.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.authentication.DTOs.AuthenticationResponse;
import com.authentication.authentication.DTOs.CompleteRegistrationRequest;
import com.authentication.authentication.DTOs.LoginRequest;
import com.authentication.authentication.DTOs.LoginResponse;
import com.authentication.authentication.DTOs.RegisterRequest;
import com.authentication.authentication.DTOs.UserResponse;
import com.authentication.authentication.Services.AuthenticateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticateService authenticateService;

    @PostMapping("/startRegistration")
    public void startRegistration(
            @RequestBody RegisterRequest regesterRequest) {
        authenticateService.startRegistration(regesterRequest);
    }

    @PostMapping("/completeRegistration")
    public ResponseEntity<AuthenticationResponse> completeRegistration(
            @RequestBody CompleteRegistrationRequest request) {
        return ResponseEntity.ok(authenticateService.completeRegistration(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws AuthenticationException {
        return ResponseEntity.ok(authenticateService.login(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return authenticateService.getCurrentUser(authentication);
    }

}
