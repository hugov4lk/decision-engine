package ee.test.loan.model;

import java.math.BigDecimal;

public record LoanRequestDto(
        String personalCode,
        BigDecimal loanAmount,
        int loanPeriod
) {
}
