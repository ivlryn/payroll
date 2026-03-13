package org.aub.payzenapi.config;

import lombok.RequiredArgsConstructor;// Assuming this exists and is correctly implemented
import org.aub.payzenapi.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API (common for REST APIs)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - ALL authentication-related paths
                        .requestMatchers(
                                "/api/v1/auths/**", // This now covers login, register, verify, forgot, etc.
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v2/api-docs",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/webjars/**",
                                ("/api/v1/employees"),
                                "/swagger-ui.html"
                        ).permitAll()
                        // User management - ADMIN only
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        // Other API endpoints - ADMIN or USER
                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions for JWT
                )
                .authenticationProvider(authenticationProvider) // Set your custom authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before Spring's default

        return http.build();
    }
}
