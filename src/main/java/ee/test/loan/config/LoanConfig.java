package ee.test.loan.config;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "decision-engine.loan")
public record LoanConfig(
        @NotNull BigDecimal minAmount,
        @NotNull BigDecimal maxAmount,
        @NotNull Integer minPeriod,
        @NotNull Integer maxPeriod
) {
}