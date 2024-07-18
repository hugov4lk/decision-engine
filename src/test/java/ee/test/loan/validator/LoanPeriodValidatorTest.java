package ee.test.loan.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import ee.test.loan.config.LoanConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanPeriodValidatorTest {

    @Mock
    private LoanConfig loanConfig;

    @InjectMocks
    private LoanPeriodValidator loanPeriodValidator;

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void givenPeriodBetweenMinMaxPeriod_whenIsValid_thenReturnsTrue(int period) {
        when(loanConfig.minPeriod()).thenReturn(1);
        when(loanConfig.maxPeriod()).thenReturn(2);

        assertTrue(loanPeriodValidator.isValid(period, null));

        var inOrder = inOrder(loanConfig);
        inOrder.verify(loanConfig).minPeriod();
        inOrder.verify(loanConfig).maxPeriod();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void givenPeriodSmallerThanMinPeriod_whenIsValid_thenReturnsFalse() {
        when(loanConfig.minPeriod()).thenReturn(1);

        assertFalse(loanPeriodValidator.isValid(0, null));

        var inOrder = inOrder(loanConfig);
        inOrder.verify(loanConfig).minPeriod();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void givenPeriodBiggerThanMinPeriod_whenIsValid_thenReturnsFalse() {
        when(loanConfig.minPeriod()).thenReturn(1);
        when(loanConfig.maxPeriod()).thenReturn(2);

        assertFalse(loanPeriodValidator.isValid(3, null));

        var inOrder = inOrder(loanConfig);
        inOrder.verify(loanConfig).minPeriod();
        inOrder.verify(loanConfig).maxPeriod();
        inOrder.verifyNoMoreInteractions();
    }
}
