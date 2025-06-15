package com.mndro.calista.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap request and response
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Log request
            String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
            logger.info("[REQUEST] {} {} | Headers: {} | Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getHeaderNames(),
                    requestBody);

            // Log response
            String responseBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
            logger.info("[RESPONSE] {} {} | Status: {} | Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    responseBody);

            // Copy response body back to actual response
            wrappedResponse.copyBodyToResponse();
        }
    }
}
