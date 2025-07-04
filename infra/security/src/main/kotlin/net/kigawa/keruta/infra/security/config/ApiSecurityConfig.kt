package net.kigawa.keruta.infra.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

/**
 * Security configuration for API endpoints.
 * This configuration has a higher order than the default one,
 * so it will be applied first for API endpoints.
 */
@Configuration
@EnableWebSecurity
class ApiSecurityConfig(private val objectMapper: ObjectMapper) {

    @Bean
    @Order(1) // Higher priority than the default SecurityFilterChain
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        // Create a RestAuthenticationEntryPoint for API endpoints
        val restAuthenticationEntryPoint = RestAuthenticationEntryPoint(objectMapper)

        http
            .securityMatcher("/api/**") // Only apply this configuration to API endpoints
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Health endpoint
                    .requestMatchers("/api/health").permitAll()
                    // Auth endpoints
                    .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                    // Swagger UI and API docs
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                    // API root endpoint
                    .requestMatchers("/api", "/api/").permitAll()
                    // All other API requests need authentication
                    .anyRequest().authenticated()
            }
            .exceptionHandling { exceptionHandling ->
                // Use RestAuthenticationEntryPoint for API endpoints
                exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint)
            }

        return http.build()
    }
}
