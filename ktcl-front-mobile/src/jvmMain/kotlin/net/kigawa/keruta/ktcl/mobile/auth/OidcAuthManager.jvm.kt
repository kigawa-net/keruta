package net.kigawa.keruta.ktcl.mobile.auth

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

/**
 * JVM用のOidcAuthManagerスタブ実装
 * モバイル専用プロジェクトのため、JVMターゲットでは使用しない
 */
actual class OidcAuthManager actual constructor(
    private val config: MobileConfig,
) {
    actual suspend fun login(): Result<String> {
        return Result.failure(NotImplementedError("JVMターゲットではOIDC認証は使用できません"))
    }

    actual suspend fun logout() {
        // No-op
    }

    actual suspend fun refreshToken(): Result<String> {
        return Result.failure(NotImplementedError("JVMターゲットではOIDC認証は使用できません"))
    }

    actual suspend fun exchangeCodeForToken(code: String): Result<String> {
        return Result.failure(NotImplementedError("JVMターゲットではOIDC認証は使用できません"))
    }

    actual fun isAuthenticated(): Boolean {
        return false
    }

    actual fun getAccessToken(): String? {
        return null
    }
}