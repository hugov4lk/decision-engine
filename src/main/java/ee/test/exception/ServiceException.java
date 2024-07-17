package ee.test.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ServiceErrorCode errorCode;

    public ServiceException(String message, ServiceErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
