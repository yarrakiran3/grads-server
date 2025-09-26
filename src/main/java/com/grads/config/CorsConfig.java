package com.grads.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource())
        );
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}

