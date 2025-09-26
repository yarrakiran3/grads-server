
// JwtAuthenticationFilter.java
package com.grads.security;

import com.grads.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Origin: " + request.getHeader("Origin"));

        // Skip JWT validation for OPTIONS requests
        if ("option".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Skipping OPTIONS request in JWT filter");
            filterChain.doFilter(request, response);
            return;
        }
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        System.out.println("Started JWT filter");


            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = jwtUtil.getTokenFromRequest(authorizationHeader);
                System.out.println("Checking user details");

                try {
                    email = jwtUtil.extractUsername(jwt);
                } catch (SignatureException e) {

                    System.out.println(" Signature doesn't match");
                    //
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature");
                    return;
                } catch (ExpiredJwtException e) {

                    System.out.println(" Token Expired");

                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                    return;
                } catch (JwtException e) {

                    System.out.println("Invalid Token ");
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                } catch (Exception e){
                    logger.error("Cannot get JWT Token");
                    throw  e;
                }
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(email);

                System.out.println("User loaded");

                System.out.println("Authorities: " + userDetails.getAuthorities());

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);




    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
    }
}