package net.kigawa.keruta.ktcl.mobile.storage

/**
 * JS用のSecureStorageスタブ実装
 * モバイル専用プロジェクトのため、JSターゲットでは使用しない
 */
actual class SecureStorage {
    actual suspend fun saveUserToken(token: String) {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }

    actual suspend fun getUserToken(): String? {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }

    actual suspend fun saveServerToken(token: String) {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }

    actual suspend fun getServerToken(): String? {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }

    actual suspend fun saveOidcState(state: String) {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }

    actual suspend fun getOidcState(): String? {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }

    actual suspend fun clearTokens() {
        throw NotImplementedError("JSターゲットではSecureStorageは使用できません")
    }
}