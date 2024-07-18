package ee.test.loan.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

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
            return createNegativeLoanResponseDto();
        }

        Optional<BigDecimal> maxLoanAmount = calculateMaxLoanAmount(personSegment.getCreditModifier(), loanRequest.loanPeriod());
        if (maxLoanAmount.isPresent()) {
            return createPositiveLoanResponseDtoBuilder(loanRequest)
                    .approvedLoanAmount(maxLoanAmount.get())
                    .approvedLoanPeriod(loanRequest.loanPeriod())
                    .build();
        }
        Optional<Integer> suitablePeriod = calculateSuitableLoanPeriod(personSegment.getCreditModifier(), loanRequest.loanAmount());
        if (suitablePeriod.isPresent()) {
            return createPositiveLoanResponseDtoBuilder(loanRequest)
                    .approvedLoanAmount(loanRequest.loanAmount())
                    .approvedLoanPeriod(suitablePeriod.get())
                    .build();
        }
        return createNegativeLoanResponseDto();
    }

    private Optional<Integer> calculateSuitableLoanPeriod(BigDecimal creditModifier, BigDecimal loanAmount) {
        BigDecimal creditScoreRatio = getCreditScoreRatio(creditModifier, loanAmount);
        int suitableLoanPeriod = BigDecimal.ONE.divide(creditScoreRatio, RoundingMode.UP).intValue();
        if (suitableLoanPeriod <= loanConfig.maxPeriod()) {
            return Optional.of(suitableLoanPeriod);
        }
        return Optional.empty();
    }

    private Optional<BigDecimal> calculateMaxLoanAmount(BigDecimal creditModifier, int loanPeriod) {
        BigDecimal low = loanConfig.minAmount();
        BigDecimal high = loanConfig.maxAmount();
        Optional<BigDecimal> maxApprovedAmount = Optional.empty();

        while (low.compareTo(high) <= 0) {
            BigDecimal mid = low.add(high).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
            BigDecimal creditScore = getCreditScore(creditModifier, mid, loanPeriod);

            if (creditScore.compareTo(BigDecimal.ONE) >= 0) {
                maxApprovedAmount = Optional.of(mid);
                low = mid.add(BigDecimal.ONE);
            } else if (creditScore.compareTo(BigDecimal.ONE) < 0) {
                high = mid.subtract(BigDecimal.ONE);
            }
        }

        BigDecimal maxAmount = loanConfig.maxAmount();
        return maxApprovedAmount.map(amount -> amount.compareTo(maxAmount) > 0 ? maxAmount : amount);
    }

    private static BigDecimal getCreditScore(BigDecimal creditModifier, BigDecimal loanAmount, int loanPeriod) {
        return getCreditScoreRatio(creditModifier, loanAmount).multiply(BigDecimal.valueOf(loanPeriod)).setScale(8, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal getCreditScoreRatio(BigDecimal creditModifier, BigDecimal loanAmount) {
        return creditModifier.divide(loanAmount, 12, RoundingMode.HALF_EVEN);
    }

    private static LoanResponseDto createNegativeLoanResponseDto() {
        return LoanResponseDto.builder()
                .decision(LoanDecision.NEGATIVE)
                .build();
    }

    private static LoanResponseDto.LoanResponseDtoBuilder createPositiveLoanResponseDtoBuilder(LoanRequestDto loanRequest) {
        return LoanResponseDto.builder()
                .decision(LoanDecision.POSITIVE)
                .requestedLoanAmount(loanRequest.loanAmount())
                .requestedLoanPeriod(loanRequest.loanPeriod());
    }
}
