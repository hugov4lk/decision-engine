package ee.test.loan.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import ee.test.loan.config.LoanConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanAmountValidatorTest {

    @Mock
    private LoanConfig loanConfig;

    @InjectMocks
    private LoanAmountValidator loanAmountValidator;

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void givenAmountBetweenMinMaxAmount_whenIsValid_thenReturnsTrue(int amount) {
        when(loanConfig.minAmount()).thenReturn(BigDecimal.ONE);
        when(loanConfig.maxAmount()).thenReturn(BigDecimal.TWO);

        assertTrue(loanAmountValidator.isValid(BigDecimal.valueOf(amount), null));

        verify(loanConfig).minAmount();
        verify(loanConfig).maxAmount();
        verifyNoMoreInteractions(loanConfig);
    }

    @Test
    void givenAmountSmallerThanMinAmount_whenIsValid_thenReturnsFalse() {
        when(loanConfig.minAmount()).thenReturn(BigDecimal.ONE);

        assertFalse(loanAmountValidator.isValid(BigDecimal.ZERO, null));

        verify(loanConfig).minAmount();
        verifyNoMoreInteractions(loanConfig);
    }

    @Test
    void givenAmountBiggerThanMinAmount_whenIsValid_thenReturnsFalse() {
        when(loanConfig.minAmount()).thenReturn(BigDecimal.ONE);
        when(loanConfig.maxAmount()).thenReturn(BigDecimal.TWO);

        assertFalse(loanAmountValidator.isValid(BigDecimal.valueOf(3), null));

        verify(loanConfig).minAmount();
        verify(loanConfig).maxAmount();
        verifyNoMoreInteractions(loanConfig);
    }
}
