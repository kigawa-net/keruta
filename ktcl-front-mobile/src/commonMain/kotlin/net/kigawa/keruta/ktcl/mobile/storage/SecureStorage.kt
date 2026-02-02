package net.kigawa.keruta.ktcl.mobile.storage

expect class SecureStorage() {
    suspend fun saveUserToken(token: String)
    suspend fun getUserToken(): String?
    suspend fun saveServerToken(token: String)
    suspend fun getServerToken(): String?
    suspend fun saveOidcState(state: String)
    suspend fun getOidcState(): String?
    suspend fun clearTokens()
}
