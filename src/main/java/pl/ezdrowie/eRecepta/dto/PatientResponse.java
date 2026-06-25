package pl.ezdrowie.eRecepta.dto;

import pl.ezdrowie.eRecepta.model.Patient;

public record PatientResponse(String pesel, String firstName, String lastName) {

    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getPesel(), patient.getFirstName(), patient.getLastName());
    }
}
