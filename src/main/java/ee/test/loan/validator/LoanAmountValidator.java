package ee.test.loan.validator;

import java.math.BigDecimal;

import ee.test.loan.config.LoanConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoanAmountValidator implements ConstraintValidator<ValidLoanAmount, BigDecimal> {

    private final LoanConfig loanConfig;

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value.compareTo(loanConfig.minAmount()) >= 0 && value.compareTo(loanConfig.maxAmount()) <= 0;
    }
}
