package net.kigawa.keruta.ktcl.mobile.storage

/**
 * JVM用のSecureStorageスタブ実装
 * モバイル専用プロジェクトのため、JVMターゲットでは使用しない
 */
actual class SecureStorage {
    actual suspend fun saveUserToken(token: String) {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }

    actual suspend fun getUserToken(): String? {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }

    actual suspend fun saveServerToken(token: String) {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }

    actual suspend fun getServerToken(): String? {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }

    actual suspend fun saveOidcState(state: String) {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }

    actual suspend fun getOidcState(): String? {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }

    actual suspend fun clearTokens() {
        throw NotImplementedError("JVMターゲットではSecureStorageは使用できません")
    }
}