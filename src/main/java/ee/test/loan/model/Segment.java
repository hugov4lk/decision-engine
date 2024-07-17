package ee.test.loan.model;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public enum Segment {
    DEBT(null),
    SEGMENT_1(BigDecimal.valueOf(100)),
    SEGMENT_2(BigDecimal.valueOf(300)),
    SEGMENT_3(BigDecimal.valueOf(1000));

    private final BigDecimal creditModifier;

    Segment(BigDecimal creditModifier) {
        this.creditModifier = creditModifier;
    }
}