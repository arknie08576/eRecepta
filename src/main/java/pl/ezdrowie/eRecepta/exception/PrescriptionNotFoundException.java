package pl.ezdrowie.eRecepta.exception;

import java.util.UUID;

public class PrescriptionNotFoundException extends RuntimeException {
    public PrescriptionNotFoundException(UUID id) {
        super("Prescription with id " + id + " not found");
    }
}
