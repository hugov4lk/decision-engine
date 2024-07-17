package ee.test.loan.service;

import ee.test.exception.ServiceErrorCode;
import ee.test.exception.ServiceException;
import ee.test.loan.model.Segment;
import org.springframework.stereotype.Service;

@Service
public class SegmentService {

    public Segment getSegmentByPersonalCode(String personalCode) {
        return switch (personalCode) {
            case "49002010965" -> Segment.DEBT;
            case "49002010976" -> Segment.SEGMENT_1;
            case "49002010987" -> Segment.SEGMENT_2;
            case "49002010998" -> Segment.SEGMENT_3;
            default -> throw new ServiceException("Unknown personal code", ServiceErrorCode.UNKNOWN_PERSONAL_CODE);
        };
    }
}
