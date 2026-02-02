package net.kigawa.keruta.ktcl.mobile.auth

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

actual class OidcAuthManager actual constructor(
    private val config: MobileConfig,
) {
    actual suspend fun login(): Result<String> {
        return Result.failure(NotImplementedError("iOS OIDC認証は未実装です"))
    }

    actual suspend fun logout() {
    }

    actual suspend fun refreshToken(): Result<String> {
        return Result.failure(NotImplementedError("リフレッシュトークン未実装"))
    }

    actual fun isAuthenticated(): Boolean {
        return false
    }
}
