package net.kigawa.keruta.ktcl.mobile.storage

import platform.Foundation.NSUserDefaults

actual class SecureStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual suspend fun saveUserToken(token: String) {
        userDefaults.setObject(token, KEY_USER_TOKEN)
    }

    actual suspend fun getUserToken(): String? {
        return userDefaults.stringForKey(KEY_USER_TOKEN)
    }

    actual suspend fun saveServerToken(token: String) {
        userDefaults.setObject(token, KEY_SERVER_TOKEN)
    }

    actual suspend fun getServerToken(): String? {
        return userDefaults.stringForKey(KEY_SERVER_TOKEN)
    }

    actual suspend fun saveOidcState(state: String) {
        userDefaults.setObject(state, KEY_OIDC_STATE)
    }

    actual suspend fun getOidcState(): String? {
        return userDefaults.stringForKey(KEY_OIDC_STATE)
    }

    actual suspend fun clearTokens() {
        userDefaults.removeObjectForKey(KEY_USER_TOKEN)
        userDefaults.removeObjectForKey(KEY_SERVER_TOKEN)
        userDefaults.removeObjectForKey(KEY_OIDC_STATE)
    }

    companion object {
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_SERVER_TOKEN = "server_token"
        private const val KEY_OIDC_STATE = "oidc_state"
    }
}
