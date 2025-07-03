/**
 * Provider for JWT token generation and validation.
 */
package net.kigawa.keruta.infra.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val userDetailsService: UserDetailsService,
) {
    @Value("\${jwt.secret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
    private lateinit var secretString: String

    @Value("\${jwt.expiration:86400000}")
    private var validityInMilliseconds: Long = 0 // 24h by default

    @Value("\${jwt.refresh-expiration:604800000}")
    private var refreshValidityInMilliseconds: Long = 0 // 7 days by default

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretString.toByteArray())
    }

    /**
     * Creates a JWT token for the given authentication.
     *
     * @param authentication The authentication object
     * @return The JWT token
     */
    fun createToken(authentication: Authentication): String {
        val username = authentication.name
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * Creates a refresh token for the given authentication.
     *
     * @param authentication The authentication object
     * @return The refresh token
     */
    fun createRefreshToken(authentication: Authentication): String {
        val username = authentication.name
        val now = Date()
        val validity = Date(now.time + refreshValidityInMilliseconds)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(validity)
            .claim("refresh", true)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * Gets the authentication from a JWT token.
     *
     * @param token The JWT token
     * @return The authentication object
     */
    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val username = claims.subject
        val userDetails = userDetailsService.loadUserByUsername(username)

        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token
     * @return true if the token is valid, false otherwise
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Gets the claims from a JWT token.
     *
     * @param token The JWT token
     * @return The claims
     */
    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    /**
     * Creates a JWT token for API access.
     * This method doesn't require an Authentication object and is useful for system-generated tokens.
     *
     * @param subject The subject of the token (usually a username or identifier)
     * @return The JWT token
     */
    fun createApiToken(subject: String): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(validity)
            .claim("type", "api")
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }
}
