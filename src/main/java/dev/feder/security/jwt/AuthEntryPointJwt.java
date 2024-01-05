package dev.feder.security.jwt;

import dev.feder.exceptions.NotAuthenticatedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    /**
     * This method is called when a user is not authenticated and attempts to access a secured resource.
     * It translates the AuthenticationException into an NotAuthenticatedException and lets the @{@link dev.feder.exceptions.ResponseEntityExceptionHandlerAdvice} handle it.
     * */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("AuthEntryPointJwt.commence");
        System.out.println("authException: " + authException);
        resolver.resolveException(request, response, null, new NotAuthenticatedException());
    }
}
