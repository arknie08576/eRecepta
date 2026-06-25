package pl.ezdrowie.eRecepta.dto;

import jakarta.validation.constraints.NotBlank;
import pl.ezdrowie.eRecepta.validation.Pesel;

public record PatientRequest(
        @NotBlank(message = "PESEL is required")
        @Pesel
        String pesel,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {}
