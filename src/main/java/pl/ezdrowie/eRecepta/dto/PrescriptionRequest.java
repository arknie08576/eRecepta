package pl.ezdrowie.eRecepta.dto;

import jakarta.validation.constraints.NotBlank;

public record PrescriptionRequest(
        @NotBlank(message = "Medication name is required")
        String medicationName,

        @NotBlank(message = "Dosage is required")
        String dosage
) {}
