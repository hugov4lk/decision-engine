package ee.test.loan.validator;

import ee.test.loan.config.LoanConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoanPeriodValidator implements ConstraintValidator<ValidLoanPeriod, Integer> {

    private final LoanConfig loanConfig;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value >= loanConfig.minPeriod() && value <= loanConfig.maxPeriod();
    }
}
