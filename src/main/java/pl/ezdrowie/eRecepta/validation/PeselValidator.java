package pl.ezdrowie.eRecepta.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.LocalDate;

public class PeselValidator implements ConstraintValidator<Pesel, String> {

    private static final int[] WEIGHTS = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

    @Override
    public boolean isValid(String pesel, ConstraintValidatorContext context) {
        if (pesel == null) {
            return true; // presence is enforced by @NotBlank
        }
        return pesel.matches("\\d{11}")
                && hasValidChecksum(pesel)
                && hasValidBirthDate(pesel);
    }

    private boolean hasValidChecksum(String pesel) {
        int sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            sum += WEIGHTS[i] * digitAt(pesel, i);
        }
        int control = (10 - (sum % 10)) % 10;
        return control == digitAt(pesel, 10);
    }

    private boolean hasValidBirthDate(String pesel) {
        int year = digitAt(pesel, 0) * 10 + digitAt(pesel, 1);
        int month = digitAt(pesel, 2) * 10 + digitAt(pesel, 3);
        int day = digitAt(pesel, 4) * 10 + digitAt(pesel, 5);

        int century;
        if (month >= 1 && month <= 12) {
            century = 1900;
        } else if (month >= 21 && month <= 32) {
            century = 2000;
            month -= 20;
        } else if (month >= 41 && month <= 52) {
            century = 2100;
            month -= 40;
        } else if (month >= 61 && month <= 72) {
            century = 2200;
            month -= 60;
        } else if (month >= 81 && month <= 92) {
            century = 1800;
            month -= 80;
        } else {
            return false;
        }

        try {
            LocalDate.of(century + year, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    private int digitAt(String pesel, int index) {
        return pesel.charAt(index) - '0';
    }
}
