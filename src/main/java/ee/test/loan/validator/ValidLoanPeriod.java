package ee.test.loan.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = LoanPeriodValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoanPeriod {

    String message() default "Invalid loan period";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}