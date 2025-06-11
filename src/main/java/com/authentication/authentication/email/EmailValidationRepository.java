package com.authentication.authentication.email;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, UUID> {

    Optional<EmailValidation> findByEmail(String email);

}
