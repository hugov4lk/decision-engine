package ee.test.loan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import ee.test.exception.ServiceErrorCode;
import ee.test.exception.ServiceException;
import ee.test.loan.model.Segment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SegmentServiceTest {

    private SegmentService segmentService;

    @BeforeEach
    void setUp() {
        segmentService = new SegmentService();
    }

    @ParameterizedTest
    @MethodSource("personSegmentData")
    void givenValidPersonalCode_whenGetSegmentByPersonalCode_thenReturnsExpectedSegment(String personalCode, Segment expectedSegment) {
        assertEquals(expectedSegment, segmentService.getSegmentByPersonalCode(personalCode));
    }

    @Test
    void givenUnknownPersonalCode_whenGetSegmentByPersonalCode_thenThrowsException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> segmentService.getSegmentByPersonalCode("unknown"));
        assertEquals("Unknown personal code", exception.getMessage());
        assertEquals(ServiceErrorCode.UNKNOWN_PERSONAL_CODE, exception.getErrorCode());
    }

    private static Stream<Arguments> personSegmentData() {
        return Stream.of(
                Arguments.of("49002010965", Segment.DEBT),
                Arguments.of("49002010976", Segment.SEGMENT_1),
                Arguments.of("49002010987", Segment.SEGMENT_2),
                Arguments.of("49002010998", Segment.SEGMENT_3)
        );
    }
}
