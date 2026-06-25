package pl.ezdrowie.eRecepta.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PeselValidatorTest {

    private final PeselValidator validator = new PeselValidator();

    private boolean isValid(String pesel) {
        return validator.isValid(pesel, null);
    }

    @Test
    void acceptsValidPesel() {
        assertThat(isValid("90010100009")).isTrue(); // 1990-01-01
        assertThat(isValid("44051401359")).isTrue(); // 1944-05-14
    }

    @Test
    void acceptsNull() {
        // presence is enforced by @NotBlank, not by this validator
        assertThat(isValid(null)).isTrue();
    }

    @Test
    void rejectsWrongChecksum() {
        assertThat(isValid("90010100000")).isFalse();
        assertThat(isValid("12345678901")).isFalse();
    }

    @Test
    void rejectsInvalidDate() {
        assertThat(isValid("90130100004")).isFalse(); // month 13, checksum is correct
    }

    @Test
    void rejectsWrongLengthOrNonDigits() {
        assertThat(isValid("")).isFalse();
        assertThat(isValid("123")).isFalse();
        assertThat(isValid("9001010000")).isFalse();    // 10 digits
        assertThat(isValid("900101000099")).isFalse();  // 12 digits
        assertThat(isValid("9001010000A")).isFalse();   // non-digit
    }
}
