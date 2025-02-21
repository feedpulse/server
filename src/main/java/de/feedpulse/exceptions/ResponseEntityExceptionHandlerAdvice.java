package de.feedpulse.exceptions;

import de.feedpulse.dto.response.ExceptionResponse;
import de.feedpulse.exceptions.common.InvalidRequestBodyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResponseEntityExceptionHandlerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ResponseEntityExceptionHandlerAdvice.class);

    /**
     * This is an exception handler for all subclasses of 'BaseException' (which is a subclass of 'RuntimeException').
     * If any of these exceptions are thrown within the application, this handler catches them and returns a {@link ExceptionResponse} object.
     *
     * @param ex the exception that got thrown in the application.
     */
    @ExceptionHandler({BaseException.class})
    protected ResponseEntity<ExceptionResponse> handleCustomExceptions(BaseException ex) {
        log.error("BaseException: ", ex);
        return new ResponseEntity<>(ExceptionResponse.fromException(ex), HttpStatus.valueOf(ex.getStatus()));
    }

    /**
     * Handles AccessDeniedException which is thrown when a user tries to access a resource that they are not authorized to access.
     * This exception is thrown by the Spring Security framework, because of the '@PreAuthorize' annotation on the controller methods.
     */
    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: ", ex);
        BaseException e = new de.feedpulse.exceptions.security.AccessDeniedException();
        return new ResponseEntity<>(ExceptionResponse.fromException(e), HttpStatus.valueOf(e.getStatus()));
    }

    /**
     * Handles the HttpMessageNotReadableException which is thrown when the request body is not in the expected format or is missing.
     * This exception is thrown by the Spring framework, because of the '@RequestBody' annotation on the controller methods.
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        BaseException e = new InvalidRequestBodyException();
        log.error("HttpMessageNotReadableException: ", ex);
        return new ResponseEntity<>(ExceptionResponse.fromException(e), HttpStatus.valueOf(e.getStatus()));
    }


    /**
     * This is the global exception handler for all exceptions that are not subclasses of 'BaseException'.
     * It catches all exceptions that are not caught by any other exception handler in the application.
     * It returns a generic 'ExceptionResponse' object with a generic message and the HTTP status code 500.
     *
     * @param ex the exception that got thrown in the application.
     * @return a 'ExceptionResponse' object with the appropriate HTTP status code and a message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        log.error("Exception: ", ex);
        BaseException e = new BaseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "Internal Server Error", "Internal Server Error");
        return new ResponseEntity<>(ExceptionResponse.fromException(e), HttpStatus.valueOf(e.getStatus()));
    }

}
