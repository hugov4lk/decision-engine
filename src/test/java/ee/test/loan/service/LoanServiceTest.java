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
    void givenDebtSegment_whenEvaluateLoan_thenReturnsNegativeDecision() {
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.DEBT);

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);
        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getRequestedLoanAmount,
                        LoanResponseDto::getApprovedLoanAmount,
                        LoanResponseDto::getRequestedLoanPeriod,
                        LoanResponseDto::getApprovedLoanPeriod
                ).containsExactly(
                        LoanDecision.NEGATIVE,
                        null,
                        null,
                        null,
                        null
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verifyNoMoreInteractions(segmentService);
        verifyNoInteractions(loanConfig);
    }

    @Test
    void givenSegment3_whenEvaluateLoan_thenReturnsPositiveDecisionWithBiggerApprovedLoanAmount() {
        BigDecimal newBiggerApprovedAmount = BigDecimal.valueOf(10000);
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.SEGMENT_3);
        when(loanConfig.minAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(loanConfig.maxAmount()).thenReturn(BigDecimal.valueOf(10000));

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);
        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getRequestedLoanAmount,
                        LoanResponseDto::getApprovedLoanAmount,
                        LoanResponseDto::getRequestedLoanPeriod,
                        LoanResponseDto::getApprovedLoanPeriod
                ).containsExactly(
                        LoanDecision.POSITIVE,
                        loanRequest.loanAmount(),
                        newBiggerApprovedAmount,
                        loanRequest.loanPeriod(),
                        loanRequest.loanPeriod()
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verify(loanConfig).minAmount();
        verify(loanConfig, times(2)).maxAmount();
        verifyNoMoreInteractions(segmentService, loanConfig);
    }

    @Test
    void givenSegment2_whenEvaluateLoan_thenReturnsPositiveDecisionWithSmallerApprovedLoanAmount() {
        BigDecimal newSmallerApprovedAmount = BigDecimal.valueOf(3599);
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.SEGMENT_2);
        when(loanConfig.minAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(loanConfig.maxAmount()).thenReturn(BigDecimal.valueOf(10000));

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);
        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getRequestedLoanAmount,
                        LoanResponseDto::getApprovedLoanAmount,
                        LoanResponseDto::getRequestedLoanPeriod,
                        LoanResponseDto::getApprovedLoanPeriod
                ).containsExactly(
                        LoanDecision.POSITIVE,
                        loanRequest.loanAmount(),
                        newSmallerApprovedAmount,
                        loanRequest.loanPeriod(),
                        loanRequest.loanPeriod()
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verify(loanConfig).minAmount();
        verify(loanConfig, times(2)).maxAmount();
        verifyNoMoreInteractions(segmentService, loanConfig);
    }

    @Test
    void givenSegment1_whenEvaluateLoan_thenReturnsPositiveDecisionAndApprovedPeriodLessThanRequested() {
        int newApprovedPeriod = 50;
        LoanRequestDto loanRequest = mockLoanRequestDto();
        when(segmentService.getSegmentByPersonalCode(loanRequest.personalCode())).thenReturn(Segment.SEGMENT_1);
        when(loanConfig.minAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(loanConfig.maxAmount()).thenReturn(BigDecimal.valueOf(10000));
        when(loanConfig.maxPeriod()).thenReturn(60);

        LoanResponseDto response = loanService.evaluateLoan(loanRequest);
        assertThat(response)
                .extracting(
                        LoanResponseDto::getDecision,
                        LoanResponseDto::getRequestedLoanAmount,
                        LoanResponseDto::getApprovedLoanAmount,
                        LoanResponseDto::getRequestedLoanPeriod,
                        LoanResponseDto::getApprovedLoanPeriod
                ).containsExactly(
                        LoanDecision.POSITIVE,
                        loanRequest.loanAmount(),
                        loanRequest.loanAmount(),
                        loanRequest.loanPeriod(),
                        newApprovedPeriod
                );

        verify(segmentService).getSegmentByPersonalCode(loanRequest.personalCode());
        verifyNoMoreInteractions(segmentService, loanConfig);
    }

    private static LoanRequestDto mockLoanRequestDto() {
        return new LoanRequestDto("49002010976", BigDecimal.valueOf(5000), 12);
    }
}
