package ee.test.loan.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ee.test.loan.config.LoanConfig;
import ee.test.loan.model.LoanDecision;
import ee.test.loan.model.LoanRequestDto;
import ee.test.loan.model.LoanResponseDto;
import ee.test.loan.model.Segment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final SegmentService segmentService;
    private final LoanConfig loanConfig;

    public LoanResponseDto evaluateLoan(LoanRequestDto loanRequest) {
        Segment personSegment = segmentService.getSegmentByPersonalCode(loanRequest.personalCode());
        if (Segment.DEBT.equals(personSegment)) {
            return LoanResponseDto.builder().decision(LoanDecision.NEGATIVE).build();
        }

        BigDecimal creditScoreRatio = personSegment.getCreditModifier().divide(loanRequest.loanAmount(), 4, RoundingMode.HALF_EVEN);
        BigDecimal creditScore = creditScoreRatio.multiply(BigDecimal.valueOf(loanRequest.loanPeriod()));
        if (creditScore.compareTo(BigDecimal.ONE) >= 0) {
            return LoanResponseDto.builder()
                    .decision(LoanDecision.POSITIVE)
                    .approvedAmount(loanRequest.loanAmount())
                    .requestedPeriod(loanRequest.loanPeriod())
                    .approvedPeriod(loanRequest.loanPeriod())
                    .build();
        }
        return findSuitableLoanPeriod(creditScoreRatio, loanRequest);
    }

    private LoanResponseDto findSuitableLoanPeriod(BigDecimal creditScoreRatio, LoanRequestDto loanRequest) {
        int suitableLoanPeriod = BigDecimal.ONE.divide(creditScoreRatio, RoundingMode.UP).intValue();
        if (suitableLoanPeriod <= loanConfig.maxPeriod()) {
            return LoanResponseDto.builder()
                    .decision(LoanDecision.POSITIVE)
                    .approvedAmount(loanRequest.loanAmount())
                    .requestedPeriod(loanRequest.loanPeriod())
                    .approvedPeriod(suitableLoanPeriod)
                    .build();
        }
        return LoanResponseDto.builder().decision(LoanDecision.NEGATIVE).build();
    }
}
