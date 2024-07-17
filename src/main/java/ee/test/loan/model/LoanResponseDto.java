package ee.test.loan.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanResponseDto {

    private LoanDecision decision;
    private BigDecimal approvedAmount;
    private Integer approvedPeriod;
    private Integer requestedPeriod;
}
