package ee.test.loan.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = LoanAmountValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoanAmount {

    String message() default "Invalid loan amount";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
