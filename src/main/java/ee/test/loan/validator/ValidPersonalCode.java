package ee.test.loan.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
@Constraint(validatedBy = PersonalCodeValidator.class)
public @interface ValidPersonalCode {

    String message() default "Invalid personal code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}