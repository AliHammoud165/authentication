package com.authentication.authentication.email;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailValidation {

    @Id
    private String email;

    private Integer code;

    private LocalDateTime code_generated_at;

    private Integer validation_attempts;

    private Integer code_generated_count;

    private LocalDate code_generation_window_start;
}
