package ee.test.loan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import ee.test.loan.config.LoanConfig;
import ee.test.loan.model.LoanDecision;
import ee.test.loan.model.LoanRequestDto;
import ee.test.loan.model.LoanResponseDto;
import ee.test.loan.model.Segment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private SegmentService segmentService;

    @Mock
    private LoanConfig loanConfig;

    @InjectMocks
    private LoanService loanService;

    @Test
    void givenDebtSegment_whenEvaluateLoan_thenNegativeDecision() {
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.DEBT);

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);
        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getApprovedAmount,
                        LoanResponseDto::getApprovedPeriod,
                        LoanResponseDto::getRequestedPeriod
                ).containsExactly(
                        LoanDecision.NEGATIVE,
                        null,
                        null,
                        null
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verifyNoMoreInteractions(segmentService);
        verifyNoInteractions(loanConfig);
    }

    @Test
    void givenSegment3_whenEvaluateLoan_thenPositiveDecision() {
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.SEGMENT_3);

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);
        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getApprovedAmount,
                        LoanResponseDto::getApprovedPeriod,
                        LoanResponseDto::getRequestedPeriod
                ).containsExactly(
                        LoanDecision.POSITIVE,
                        loanRequest.loanAmount(),
                        loanRequest.loanPeriod(),
                        loanRequest.loanPeriod()
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verifyNoMoreInteractions(segmentService);
        verifyNoInteractions(loanConfig);
    }

    @Test
    void givenSegment1AndMaxPeriod60_whenEvaluateLoan_thenPositiveDecisionAndApprovedPeriodLessThanRequested() {
        int newApprovedPeriod = 50;
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.SEGMENT_1);
        when(loanConfig.maxPeriod()).thenReturn(60);

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);

        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getApprovedAmount,
                        LoanResponseDto::getApprovedPeriod,
                        LoanResponseDto::getRequestedPeriod
                ).containsExactly(
                        LoanDecision.POSITIVE,
                        loanRequest.loanAmount(),
                        newApprovedPeriod,
                        loanRequest.loanPeriod()
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verify(loanConfig).maxPeriod();
        verifyNoMoreInteractions(segmentService, loanConfig);
    }

    private static LoanRequestDto mockLoanRequestDto() {
        return new LoanRequestDto("49002010976", BigDecimal.valueOf(5000), 12);
    }
}
