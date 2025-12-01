package valens.example.task_flow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;

/**
 * Filter to log HTTP requests and responses with correlation IDs.
 * Essential for API gateway analytics and debugging.
 */
@Slf4j
@Component
@Order(2) // Execute after CorrelationIdFilter
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        // Skip logging for actuator and static resources
        String path = request.getRequestURI();
        if (shouldSkipLogging(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Instant startTime = Instant.now();
        String correlationId = request.getHeader("X-Correlation-ID");
        
        // Wrap request and response to enable body reading
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = Duration.between(startTime, Instant.now()).toMillis();
            logRequestResponse(wrappedRequest, wrappedResponse, correlationId, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private boolean shouldSkipLogging(String path) {
        return path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/favicon.ico");
    }

    private void logRequestResponse(ContentCachingRequestWrapper request, 
                                   ContentCachingResponseWrapper response,
                                   String correlationId, long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        int statusCode = response.getStatus();
        
        // Log request
        log.info("HTTP Request: method={}, uri={}, query={}, correlationId={}, status={}, duration={}ms",
            method, uri, queryString != null ? queryString : "", correlationId, statusCode, duration);
        
        // Log request body for POST/PUT/PATCH (only in debug mode to avoid sensitive data)
        if (log.isDebugEnabled() && isRequestBodyMethod(method)) {
            String requestBody = getRequestBody(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                log.debug("Request body: {}", sanitizeRequestBody(requestBody));
            }
        }
        
        // Log response body for errors (only in debug mode)
        if (log.isDebugEnabled() && statusCode >= 400) {
            String responseBody = getResponseBody(response);
            if (responseBody != null && !responseBody.isEmpty()) {
                log.debug("Response body: {}", responseBody);
            }
        }
    }

    private boolean isRequestBodyMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            try {
                return new String(content, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            try {
                return new String(content, response.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    private String sanitizeRequestBody(String body) {
        // Remove sensitive fields from logging
        return body.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                   .replaceAll("\"accessSecret\"\\s*:\\s*\"[^\"]*\"", "\"accessSecret\":\"***\"")
                   .replaceAll("\"refreshSecret\"\\s*:\\s*\"[^\"]*\"", "\"refreshSecret\":\"***\"");
    }
}


