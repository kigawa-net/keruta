package net.kigawa.keruta.ktcl.mobile.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class SecureStorage(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "keruta_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    actual suspend fun saveUserToken(token: String) {
        sharedPreferences.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    actual suspend fun getUserToken(): String? {
        return sharedPreferences.getString(KEY_USER_TOKEN, null)
    }

    actual suspend fun saveServerToken(token: String) {
        sharedPreferences.edit().putString(KEY_SERVER_TOKEN, token).apply()
    }

    actual suspend fun getServerToken(): String? {
        return sharedPreferences.getString(KEY_SERVER_TOKEN, null)
    }

    actual suspend fun saveOidcState(state: String) {
        sharedPreferences.edit().putString(KEY_OIDC_STATE, state).apply()
    }

    actual suspend fun getOidcState(): String? {
        return sharedPreferences.getString(KEY_OIDC_STATE, null)
    }

    actual suspend fun clearTokens() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_TOKEN)
            remove(KEY_SERVER_TOKEN)
            remove(KEY_OIDC_STATE)
        }.apply()
    }

    companion object {
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_SERVER_TOKEN = "server_token"
        private const val KEY_OIDC_STATE = "oidc_state"
    }
}
