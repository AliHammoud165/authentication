package com.authentication.authentication.Services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authentication.authentication.DTOs.AuthenticationResponse;
import com.authentication.authentication.DTOs.CompleteRegistrationRequest;
import com.authentication.authentication.DTOs.EmailRequset;
import com.authentication.authentication.DTOs.EmailValidation;
import com.authentication.authentication.DTOs.LoginRequest;
import com.authentication.authentication.DTOs.LoginResponse;
import com.authentication.authentication.DTOs.RegisterRequest;
import com.authentication.authentication.DTOs.UserResponse;
import com.authentication.authentication.Entities.User;
import com.authentication.authentication.Mappers.UserMapper;
import com.authentication.authentication.Repositories.EmailValidationRepository;
import com.authentication.authentication.Repositories.UserRepository;
import com.authentication.authentication.config.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticateService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailGeneratingService emailGeneratingService;
    private final EmailService emailService;
    private final EmailValidationRepository emailValidationRepository;

    public void startRegistration(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (userRepository.findByphonenumber(registerRequest.getPhone_number()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        int verificationCode = emailGeneratingService.generateCode();
        System.out.println(verificationCode);
        emailGeneratingService.saveCodeForEmail(registerRequest.getEmail(), verificationCode);

        String emailText = emailGeneratingService.generateEmailTextAndSaveCode(registerRequest.getEmail(),
                verificationCode);
        EmailRequset emailRequset = new EmailRequset(registerRequest.getEmail(), "Verify your email", emailText);
        EmailValidation emailValidation = emailValidationRepository.findByEmail(registerRequest.getEmail())
                .orElse(null);

        if (emailValidation != null) {
            boolean windowExpired = emailValidation.getCode_generation_window_start() == null ||
                    ChronoUnit.MINUTES.between(emailValidation.getCode_generation_window_start().atStartOfDay(),
                            LocalDateTime.now()) > 1440;

            if (windowExpired) {
                emailValidation.setCode_generated_count(1);
                emailValidation.setCode_generation_window_start(LocalDate.now());
            } else {
                if (emailValidation.getCode_generated_count() >= 10) {
                    throw new IllegalArgumentException(
                            "You have reached the maximum number of code requests in 24 hours.");
                }
                emailValidation.setCode_generated_count(emailValidation.getCode_generated_count() + 1);
            }

            emailValidation.setCode(verificationCode);
            emailValidation.setCode_generated_at(LocalDateTime.now());
            emailValidation.setValidation_attempts(0);
            emailValidationRepository.save(emailValidation);

        } else {
            emailValidation = EmailValidation.builder()
                    .email(registerRequest.getEmail())
                    .code(verificationCode)
                    .code_generated_at(LocalDateTime.now())
                    .validation_attempts(0)
                    .code_generated_count(1)
                    .code_generation_window_start(LocalDate.now())
                    .build();

            emailValidationRepository.save(emailValidation);
        }

        emailService.sendSimpleEmail(emailRequset);

    }

    public AuthenticationResponse completeRegistration(CompleteRegistrationRequest registerRequest) {
        EmailValidation emailValidation = emailValidationRepository.findByEmail(registerRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        if (emailValidation.getCode_generated_at() == null ||
                Duration.between(emailValidation.getCode_generated_at(), LocalDateTime.now()).toMinutes() > 10) {
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (emailValidation.getValidation_attempts() >= 4) {
            throw new IllegalArgumentException("Too many invalid attempts. Try again later.");
        }

        if (!Objects.equals(emailValidation.getCode(), registerRequest.getCode())) {
            emailValidation.setValidation_attempts(emailValidation.getValidation_attempts() + 1);
            emailValidationRepository.save(emailValidation);
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (userRepository.findByphonenumber(registerRequest.getPhone_number()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        User user = User.builder()
                .first_name(registerRequest.getFirst_name())
                .last_name(registerRequest.getLast_name())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phonenumber(registerRequest.getPhone_number())
                .role(registerRequest.getRole())
                .build();

        userRepository.save(user);
        emailValidationRepository.delete(emailValidation);

        var jwtToken = jwtService.generateToken(null, user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(Map.of(), user);

        LoginResponse response = UserMapper.toLoginResponse(user, token);
        return response;
    }

    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) authentication.getPrincipal();

        UserResponse response = UserMapper.toUserResponse(user);
        return ResponseEntity.ok(response);

    }
}
