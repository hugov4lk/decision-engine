package ee.test.loan.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalCodeValidatorTest {

    private PersonalCodeValidator personalCodeValidator;

    @BeforeEach
    void setUp() {
        personalCodeValidator = new PersonalCodeValidator();
    }

    @Test
    void givenValidPersonalCode_whenIsValid_thenReturnsTrue() {
        assertTrue(personalCodeValidator.isValid("49002010976", null));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"1234567890", "1234567890a", "49002010964"})
    void givenInvalidPersonalCode_whenIsValid_thenReturnsFalse(String personalCode) {
        assertFalse(personalCodeValidator.isValid(personalCode, null));
    }
}
