package pl.ezdrowie.eRecepta.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates a Polish PESEL number: 11 digits, a correct control digit
 * and a valid encoded birth date. Null is considered valid so that
 * presence is enforced separately with {@code @NotBlank}.
 */
@Documented
@Constraint(validatedBy = PeselValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Pesel {

    String message() default "Invalid PESEL number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
