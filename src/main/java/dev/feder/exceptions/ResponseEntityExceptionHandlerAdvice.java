package dev.feder.exceptions;

import dev.feder.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ResponseEntityExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    /**
     * This is an exception handler for all subclasses of 'BaseException' (which is a subclass of 'RuntimeException').
     * If any of these exceptions are thrown within the application, this handler catches them and immediately re-throws them.
     * The purpose of re-throwing the exception here is to exclude these custom exceptions from being handled
     * by the global exception handler {@see ExceptionHandlerAdvice#handleException(Exception)}.
     * This allows the Spring framework to use the '@ResponseStatus' annotation present on these exception classes
     * to set the appropriate HTTP status code in the reply.
     *
     * @param ex      the exception that got thrown in the application.
     * @throws RuntimeException re-throws the input exception so the '@ResponseStatus' annotation on it can be used.
     */
    @ExceptionHandler({BaseException.class})
    protected void handleCustomExceptions(RuntimeException ex) {
        throw ex; //
    }

    /**
     * This is the global exception handler for all exceptions that are not subclasses of 'BaseException'.
     * It catches all exceptions that are not caught by any other exception handler in the application.
     * It returns a 'ProblemDetail' object with the appropriate HTTP status code and a message.
     *
     * @param e the exception that got thrown in the application.
     * @return a 'ProblemDetail' object with the appropriate HTTP status code and a message.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        e.printStackTrace();
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

}
