package ee.test.loan;

import java.math.BigDecimal;

import ee.test.loan.model.LoanRequestDto;
import ee.test.loan.model.LoanResponseDto;
import ee.test.loan.service.LoanService;
import ee.test.loan.validator.ValidLoanAmount;
import ee.test.loan.validator.ValidLoanPeriod;
import ee.test.loan.validator.ValidPersonalCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/evaluate")
    public ResponseEntity<LoanResponseDto> evaluateLoan(@RequestParam @ValidPersonalCode String personalCode,
                                                        @RequestParam @ValidLoanAmount BigDecimal loanAmount,
                                                        @RequestParam @ValidLoanPeriod int loanPeriod) {
        return ResponseEntity.ok(loanService.evaluateLoan(new LoanRequestDto(personalCode, loanAmount, loanPeriod)));
    }
}
