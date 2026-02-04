package net.kigawa.keruta.ktcl.mobile.auth

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

/**
 * JS用のOidcAuthManagerスタブ実装
 * モバイル専用プロジェクトのため、JSターゲットでは使用しない
 */
actual class OidcAuthManager actual constructor(
    private val config: MobileConfig,
) {
    actual suspend fun login(): Result<String> {
        return Result.failure(NotImplementedError("JSターゲットではOIDC認証は使用できません"))
    }

    actual suspend fun logout() {
        // No-op
    }

    actual suspend fun refreshToken(): Result<String> {
        return Result.failure(NotImplementedError("JSターゲットではOIDC認証は使用できません"))
    }

    actual fun isAuthenticated(): Boolean {
        return false
    }
}