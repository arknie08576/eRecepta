package pl.ezdrowie.eRecepta.dto;

import pl.ezdrowie.eRecepta.model.Prescription;

import java.util.UUID;

public record PrescriptionResponse(UUID id, String medicationName, String dosage) {

    public static PrescriptionResponse from(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getMedicationName(),
                prescription.getDosage());
    }
}
