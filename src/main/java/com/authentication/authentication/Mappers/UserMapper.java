package com.authentication.authentication.Mappers;

import com.authentication.authentication.DTOs.LoginResponse;
import com.authentication.authentication.DTOs.UserResponse;
import com.authentication.authentication.Entities.User;

public class UserMapper {

    public static LoginResponse toLoginResponse(User user, String token) {
        return LoginResponse.builder()
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone_number(user.getPhonenumber())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setPhone_number(user.getPhonenumber());
        response.setFull_name(user.getFirst_name() + " " + user.getLast_name());
        response.setRole(user.getRole().name());
        response.setUsername(user.getUsername());
        return response;
    }
}
