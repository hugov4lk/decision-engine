package ee.test.loan.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanResponseDto {

    private LoanDecision decision;
    private BigDecimal approvedLoanAmount;
    private BigDecimal requestedLoanAmount;
    private Integer approvedLoanPeriod;
    private Integer requestedLoanPeriod;
}
