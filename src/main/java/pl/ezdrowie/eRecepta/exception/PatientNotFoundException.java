package pl.ezdrowie.eRecepta.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String pesel) {
        super("Patient with PESEL " + pesel + " not found");
    }
}
