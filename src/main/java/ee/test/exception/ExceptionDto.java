package ee.test.exception;

public record ExceptionDto(
        String message,
        String exception,
        ServiceErrorCode errorCode
) {
}
