package com.grads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;


//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        // Allow credentials
//        config.setAllowCredentials(true);
//
//        // Allow specific origins (add your frontend URLs)
//        config.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000",
//                "https://grads.kirany.space"
//
//        ));
//
//        // Allow specific headers
//        config.setAllowedHeaders(Arrays.asList(
//                "Origin",
//                "Content-Type",
//                "Accept",
//                "Authorization",
//                "Access-Control-Allow-Origin",
//                "Access-Control-Allow-Headers",
//                "Access-Control-Allow-Methods",
//                "X-Requested-With"
//        ));
//
//        // Allow specific methods
//        config.setAllowedMethods(Arrays.asList(
//                "GET",
//                "POST",
//                "PUT",
//                "DELETE",
//                "OPTIONS",
//                "PATCH"
//        ));
//
//        // Set max age for preflight requests
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsFilter(source);
//    }
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://grads.kirany.space"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Add all the headers that browsers commonly send
//        configuration.setAllowedHeaders(Arrays.asList(
//                "Authorization",
//                "Content-Type",
//                "X-Requested-With",
//                "Accept",
//                "Accept-Encoding",
//                "Accept-Language",
//                "Origin",
//                "Referer",
//                "User-Agent",
//                "Access-Control-Request-Method",
//                "Access-Control-Request-Headers"
//        ));

        // Or use this simpler approach to allow all headers
         configuration.addAllowedHeader("*");

        configuration.setAllowCredentials(true);

        // Add max age for preflight caching
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}