package com.authentication.authentication.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authentication.authentication.DTOs.EmailValidation;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, UUID> {

    Optional<EmailValidation> findByEmail(String email);

}
