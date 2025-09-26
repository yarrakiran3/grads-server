
// JwtAuthenticationEntryPoint.java
package com.grads.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        System.out.println("=== AuthenticationEntryPoint Debug ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Exception: " + authException.getMessage());

        // Don't handle OPTIONS requests in the entry point
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Skipping OPTIONS in AuthenticationEntryPoint");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                "{\"success\": false, \"message\": \"Unauthorized: " + authException.getMessage() + "\"}"
        );
    }

}
