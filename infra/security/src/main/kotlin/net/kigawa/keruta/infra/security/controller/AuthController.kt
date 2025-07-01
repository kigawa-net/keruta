package net.kigawa.keruta.infra.security.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.infra.security.dto.LoginRequest
import net.kigawa.keruta.infra.security.dto.RefreshTokenRequest
import net.kigawa.keruta.infra.security.dto.TokenResponse
import net.kigawa.keruta.infra.security.jwt.JwtTokenProvider
import net.kigawa.keruta.infra.security.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication API")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    /**
     * Login endpoint.
     *
     * @param loginRequest The login request
     * @return The token response
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT tokens")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        val isValid = userService.validateCredentials(loginRequest.username, loginRequest.password)

        if (!isValid) {
            return ResponseEntity.badRequest().build()
        }

        val userDetails = userService.loadUserByUsername(loginRequest.username)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        val accessToken = jwtTokenProvider.createToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken(authentication)

        return ResponseEntity.ok(TokenResponse(accessToken, refreshToken))
    }

    /**
     * Refresh token endpoint.
     *
     * @param refreshTokenRequest The refresh token request
     * @return The token response
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refreshes JWT tokens using a refresh token")
    fun refresh(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        val refreshToken = refreshTokenRequest.refreshToken

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().build()
        }

        val authentication = jwtTokenProvider.getAuthentication(refreshToken)

        val accessToken = jwtTokenProvider.createToken(authentication)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(authentication)

        return ResponseEntity.ok(TokenResponse(accessToken, newRefreshToken))
    }
}
