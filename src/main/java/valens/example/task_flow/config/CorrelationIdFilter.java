package valens.example.task_flow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add correlation IDs to all HTTP requests and responses.
 * This helps with request tracing and is essential for API gateway integration.
 */
@Slf4j
@Component
@Order(1) // Execute early in the filter chain
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract or generate correlation ID
            String correlationId = extractOrGenerateCorrelationId(request);
            
            // Add to MDC for logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            
            // Add to response header
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Continue filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        // Check if correlation ID is already present in request header
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (!StringUtils.hasText(correlationId)) {
            // Generate new correlation ID
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated new correlation ID: {}", correlationId);
        } else {
            log.debug("Using existing correlation ID from request: {}", correlationId);
        }
        
        return correlationId;
    }
}


