package net.kigawa.keruta.infra.security.service

import jakarta.annotation.PostConstruct
import net.kigawa.keruta.infra.security.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service for user management.
 */
@Service
class UserService(
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {

    @Value("\${auth.admin.username:admin}")
    private lateinit var adminUsername: String

    @Value("\${auth.admin.password:password}")
    private lateinit var adminPassword: String

    private val users = mutableMapOf<String, User>()

    /**
     * Initializes the user service with a default admin user.
     */
    @PostConstruct
    fun init() {
        if (users.isEmpty()) {
            val encodedPassword = passwordEncoder.encode(adminPassword)
            val adminUser = User.create(adminUsername, encodedPassword, listOf("ADMIN"))
            users[adminUsername] = adminUser
        }
    }

    /**
     * Loads a user by username.
     *
     * @param username The username
     * @return The user details
     * @throws UsernameNotFoundException If the user is not found
     */
    override fun loadUserByUsername(username: String): UserDetails {
        init()
        return users[username] ?: throw UsernameNotFoundException("User not found: $username")
    }

    /**
     * Validates a user's credentials.
     *
     * @param username The username
     * @param password The password
     * @return true if the credentials are valid, false otherwise
     */
    fun validateCredentials(username: String, password: String): Boolean {
        val user = try {
            loadUserByUsername(username)
        } catch (e: UsernameNotFoundException) {
            return false
        }

        return passwordEncoder.matches(password, user.password)
    }
}
