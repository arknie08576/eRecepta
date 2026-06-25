package pl.ezdrowie.eRecepta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PatientRequest(
        @NotBlank(message = "PESEL is required")
        @Pattern(regexp = "\\d{11}", message = "PESEL must contain exactly 11 digits")
        String pesel,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {}
