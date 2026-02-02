package net.kigawa.keruta.ktcl.mobile.auth

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

expect class OidcAuthManager(config: MobileConfig) {
    suspend fun login(): Result<String>
    suspend fun logout()
    suspend fun refreshToken(): Result<String>
    fun isAuthenticated(): Boolean
}
