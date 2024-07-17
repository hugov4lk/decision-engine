package ee.test.exception;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ExceptionDto handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        var errorList = ex.getAllValidationResults()
                .stream()
                .map(ParameterValidationResult::getResolvableErrors)
                .flatMap(List::stream)
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("error: {}", HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        return new ExceptionDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorList, ServiceErrorCode.VALIDATION_FAILURE);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public ExceptionDto handleServiceException(ServiceException ex) {
        log.error("error: {}", HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        return new ExceptionDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), ex.getErrorCode());
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ExceptionDto handleException(Exception ex) {
        log.error("error: {}", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex);
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Something went wrong, please try again later", null);
    }
}
