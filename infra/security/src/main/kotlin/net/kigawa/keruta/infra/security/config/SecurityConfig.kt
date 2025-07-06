package net.kigawa.keruta.infra.security.config

import net.kigawa.keruta.infra.security.jwt.JwtAuthenticationFilter
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    // Auth endpoints
                    .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                    // Login page and OAuth2 endpoints
                    .requestMatchers("/login", "/oauth2/**").permitAll()
                    // Main application (now at /admin)
                    .requestMatchers("/admin/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    // User home page
                    .requestMatchers("/user-home").permitAll()
                    // Swagger UI
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                    // Admin panel (now at root)
                    .requestMatchers("/").authenticated()
                    // All other requests need authentication
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/")
                    .permitAll()
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
        configuration.allowCredentials = false
        configuration.maxAge = 3600

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

@Configuration
class KeycloakSecurityConfig {
    @Bean
    fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy {
        return RegisterSessionAuthenticationStrategy(SessionRegistryImpl())
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        val keycloakAuthenticationProvider = KeycloakAuthenticationProvider()
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper())
        auth.authenticationProvider(keycloakAuthenticationProvider)
    }
}
