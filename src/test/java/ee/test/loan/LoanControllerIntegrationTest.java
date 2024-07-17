package ee.test.loan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ee.test.exception.ServiceErrorCode;
import ee.test.loan.model.LoanDecision;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerIntegrationTest {

    private static final String LOAN_EVALUATE_PATH = "/loan/evaluate";
    private static final String PERSONAL_CODE_VALID = "49002010998";
    private static final String PERSONAL_CODE_INVALID = "49002010997";
    private static final String LOAN_AMOUNT_VALID = "5000";
    private static final String MAX_LOAN_AMOUNT = "10000";
    private static final String LOAN_AMOUNT_INVALID = "1999";
    private static final String LOAN_PERIOD_VALID = "12";
    private static final String LOAN_PERIOD_INVALID = "11";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenEvaluateLoan_thenReturns200AndExpectedBody() throws Exception {
        mockMvc.perform(get(LOAN_EVALUATE_PATH)
                        .param("personalCode", PERSONAL_CODE_VALID)
                        .param("loanAmount", LOAN_AMOUNT_VALID)
                        .param("loanPeriod", LOAN_PERIOD_VALID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value(LoanDecision.POSITIVE.toString()))
                .andExpect(jsonPath("$.requestedLoanAmount").value(LOAN_AMOUNT_VALID))
                .andExpect(jsonPath("$.approvedLoanAmount").value(MAX_LOAN_AMOUNT))
                .andExpect(jsonPath("$.approvedLoanPeriod").value(LOAN_PERIOD_VALID))
                .andExpect(jsonPath("$.requestedLoanPeriod").value(LOAN_PERIOD_VALID));
    }

    @Test
    void whenEvaluateLoanWithInvalidPersonalCode_thenBadRequestAndExpectedErrorMessage() throws Exception {
        mockMvc.perform(get(LOAN_EVALUATE_PATH)
                        .param("personalCode", PERSONAL_CODE_INVALID)
                        .param("loanAmount", LOAN_AMOUNT_VALID)
                        .param("loanPeriod", LOAN_PERIOD_VALID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.exception").value("Invalid personal code"))
                .andExpect(jsonPath("$.errorCode").value(ServiceErrorCode.VALIDATION_FAILURE.toString()));
    }

    @Test
    void whenEvaluateLoanWithInvalidLoanAmount_thenBadRequestAndExpectedErrorMessage() throws Exception {
        mockMvc.perform(get(LOAN_EVALUATE_PATH)
                        .param("personalCode", PERSONAL_CODE_VALID)
                        .param("loanAmount", LOAN_AMOUNT_INVALID)
                        .param("loanPeriod", LOAN_PERIOD_VALID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.exception").value("Invalid loan amount"))
                .andExpect(jsonPath("$.errorCode").value(ServiceErrorCode.VALIDATION_FAILURE.toString()));
    }

    @Test
    void whenEvaluateLoanWithInvalidLoanPeriod_thenBadRequestAndExpectedErrorMessage() throws Exception {
        mockMvc.perform(get(LOAN_EVALUATE_PATH)
                        .param("personalCode", PERSONAL_CODE_VALID)
                        .param("loanAmount", LOAN_AMOUNT_VALID)
                        .param("loanPeriod", LOAN_PERIOD_INVALID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.exception").value("Invalid loan period"))
                .andExpect(jsonPath("$.errorCode").value(ServiceErrorCode.VALIDATION_FAILURE.toString()));
    }

    @Test
    void whenEvaluateLoanWithWrongParameterName_thenInternalServerError() throws Exception {
        mockMvc.perform(get(LOAN_EVALUATE_PATH)
                        .param("wrongName", PERSONAL_CODE_VALID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.exception").value("Something went wrong, please try again later"))
                .andExpect(jsonPath("$.errorCode").value(Matchers.nullValue()));
    }
}
