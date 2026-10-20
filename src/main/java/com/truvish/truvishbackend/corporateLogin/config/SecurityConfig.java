package com.truvish.truvishbackend.corporateLogin.config;

import com.truvish.truvishbackend.corporateLogin.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        // =========================================================
        // CORS CONFIGURATION
        // =========================================================

        CorsConfigurationSource corsConfigurationSource =
                request -> {

                    CorsConfiguration configuration =
                            new CorsConfiguration();


                    // =================================================
                    // ALLOWED ORIGINS
                    // =================================================

                    configuration.setAllowedOriginPatterns(
                            List.of(

                                    // ---------------------------------
                                    // PRODUCTION
                                    // ---------------------------------

                                    "https://truvish.com",
                                    "https://www.truvish.com",

                                    // ---------------------------------
                                    // RAILWAY
                                    // ---------------------------------

                                    "https://*.up.railway.app",

                                    // ---------------------------------
                                    // NETLIFY
                                    // ---------------------------------

                                    "https://*.netlify.app",

                                    // ---------------------------------
                                    // CLOUDFLARE TUNNEL
                                    // ---------------------------------

                                    "https://*.trycloudflare.com",

                                    // ---------------------------------
                                    // REDEEM FRONTEND
                                    // ---------------------------------

                                    "https://trivish-redeem.com",

                                    // ---------------------------------
                                    // CLIENT REQUEST
                                    // ---------------------------------

                                    "https://client-request-production.up.railway.app",

                                    // ---------------------------------
                                    // LOCAL DEVELOPMENT
                                    // ---------------------------------

                                    "http://localhost:3000",
                                    "http://localhost:5173",
                                    "http://localhost:5174",
                                    "http://localhost:5175",
                                    "http://localhost:5176",
                                    "http://localhost:5177"
                            )
                    );


                    // =================================================
                    // ALLOWED METHODS
                    // =================================================

                    configuration.setAllowedMethods(
                            List.of(
                                    "GET",
                                    "POST",
                                    "PUT",
                                    "DELETE",
                                    "PATCH",
                                    "OPTIONS"
                            )
                    );


                    // =================================================
                    // ALLOWED HEADERS
                    // =================================================

                    configuration.setAllowedHeaders(
                            List.of("*")
                    );


                    // =================================================
                    // EXPOSED HEADERS
                    // =================================================

                    configuration.setExposedHeaders(
                            List.of(
                                    "Authorization",
                                    "Content-Type"
                            )
                    );


                    // =================================================
                    // CREDENTIALS
                    // =================================================

                    configuration.setAllowCredentials(true);


                    // =================================================
                    // CORS CACHE
                    // =================================================

                    configuration.setMaxAge(3600L);


                    return configuration;
                };


        // =========================================================
        // HTTP SECURITY
        // =========================================================

        http


                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf ->
                        csrf.disable()
                )


                // =====================================================
                // CORS
                // =====================================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource
                        )
                )


                // =====================================================
                // SESSION MANAGEMENT
                // =====================================================
                //
                // JWT application hai.
                // Isliye server-side session use nahi karna.
                //
                // =====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth


                        // =================================================
                        // OPTIONS / CORS PREFLIGHT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // =================================================
                        // ACTUATOR
                        // =================================================

                        .requestMatchers(
                                "/actuator/**"
                        ).permitAll()


                        // =================================================
                        // DATABASE METRICS
                        // =================================================

                        .requestMatchers(
                                "/api/metrics/database/**"
                        ).permitAll()


                        // =================================================
                        // ERROR / FAVICON
                        // =================================================

                        .requestMatchers(
                                "/error",
                                "/favicon.ico"
                        ).permitAll()


                        // =================================================
                        // CORPORATE LOGIN
                        // =================================================

                        .requestMatchers(
                                "/api/corporate/login/**"
                        ).permitAll()


                        // =================================================
                        // CORPORATE DASHBOARD
                        // =================================================

                        .requestMatchers(
                                "/api/corporate/dashboard/**"
                        ).permitAll()


                        // =================================================
                        // CODE REPORT
                        // =================================================

                        .requestMatchers(
                                "/api/corporate/code-report/**"
                        ).permitAll()


                        // =================================================
                        // CLIENT
                        // =================================================

                        .requestMatchers(
                                "/api/client",
                                "/api/client/**",
                                "/api/clients",
                                "/api/clients/**"
                        ).permitAll()


                        // =================================================
                        // CLIENT REDEMPTION HISTORY
                        // =================================================

                        .requestMatchers(
                                "/api/redemption-history/**"
                        ).permitAll()


                        // =================================================
                        // TRUVISH
                        // =================================================

                        .requestMatchers(
                                "/api/truvish/**"
                        ).permitAll()


                        // =================================================
                        // THEMES
                        // =================================================

                        .requestMatchers(
                                "/api/themes/**"
                        ).permitAll()


                        // =================================================
                        // ADMIN
                        // =================================================

                        .requestMatchers(
                                "/api/admin/**"
                        ).permitAll()


                        // =================================================
                        // CLIENT BRAND
                        // =================================================

                        .requestMatchers(
                                "/api/client-choose-brand/**"
                        ).permitAll()


                        // =================================================
                        // INVENTORY
                        // =================================================

                        .requestMatchers(
                                "/api/inventory/**"
                        ).permitAll()


                        // =================================================
                        // VOUCHER INVENTORY
                        // =================================================

                        .requestMatchers(
                                "/api/voucher-inventory/**"
                        ).permitAll()


                        // =================================================
                        // REDEEM
                        // =================================================

                        .requestMatchers(
                                "/api/redeem/**"
                        ).permitAll()


                        // =================================================
                        // WALLET
                        // =================================================

                        .requestMatchers(
                                "/api/wallet/**"
                        ).permitAll()


                        // =================================================
                        // TRUCARD
                        // =================================================

                        .requestMatchers(
                                "/api/trucard/**"
                        ).permitAll()


                        // =================================================
                        // TRUCARD CODES
                        // =================================================

                        .requestMatchers(
                                "/api/trucard-codes/**"
                        ).permitAll()


                        // =================================================
                        // TRU BLANK CODE
                        // =================================================

                        .requestMatchers(
                                "/api/admin/tru-blank-code/**"
                        ).permitAll()


                        // =================================================
                        // UPLOADS
                        // =================================================

                        .requestMatchers(
                                "/uploads/**"
                        ).permitAll()


                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                )


                // =====================================================
                // AUTHENTICATION PROVIDER
                // =====================================================

                .authenticationProvider(
                        authenticationProvider
                )


                // =====================================================
                // JWT FILTER
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )


                // =====================================================
                // BASIC AUTH DISABLED
                // =====================================================

                .httpBasic(
                        httpBasic ->
                                httpBasic.disable()
                )


                // =====================================================
                // FORM LOGIN DISABLED
                // =====================================================

                .formLogin(
                        form ->
                                form.disable()
                );


        // =========================================================
        // BUILD
        // =========================================================

        return http.build();
    }
}