package de.feedpulse.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.feedpulse.exceptions.security.TooManyRequestsException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//@Component
public class ThrottleRequestFilter extends OncePerRequestFilter {
    private int MAX_REQUESTS_PER_MINUTE = 50; //or whatever you want it to be

    private LoadingCache<String, Integer> requestCountsPerIpAddress;

    public ThrottleRequestFilter(){
        super();
        requestCountsPerIpAddress = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(key -> 0);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIpAddress = getClientIP(request);
        if(isMaximumRequestsPerSecondExceeded(clientIpAddress)){
            // Filters are processed before the dispatcher servlet, so the @RestControllerAdvice will not handle the exception
            // So we handle it here and directly write the response
            TooManyRequestsException ex = new TooManyRequestsException();
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(ex.toJson());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isMaximumRequestsPerSecondExceeded(String clientIpAddress){
        Integer requests = 0;
        requests = requestCountsPerIpAddress.get(clientIpAddress);
        if(requests != null){
            if(requests > MAX_REQUESTS_PER_MINUTE) {
                requestCountsPerIpAddress.asMap().remove(clientIpAddress);
                requestCountsPerIpAddress.put(clientIpAddress, requests);
                return true;
            }

        } else {
            requests = 0;
        }
        requests++;
        requestCountsPerIpAddress.put(clientIpAddress, requests);
        return false;
    }

    public String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    public void destroy() {

    }


}
