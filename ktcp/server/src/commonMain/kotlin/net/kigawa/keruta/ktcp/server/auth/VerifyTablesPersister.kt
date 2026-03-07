package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.KtclAudience
import net.kigawa.keruta.ktcp.model.UserIssuer
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifiedToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url

interface VerifyTablesPersister {
    fun getVerifyTables(unverifiedTokens: UnverifiedAuthTokens): Res<PersistedVerifyTables, KtcpErr>?
    fun createVerifyTables(token: VerifiedAuthToken, providerName: String): Res<PersistedVerifyTables, KtcpErr>
    fun getUserTables(
        issuer: Url, subject: String,
    ): Res<Pair<PersistedUser, PersistedUserIdp>, KtcpErr>

    fun saveProviderForUser(
        user: PersistedUser, providerToken: VerifiedToken, ktclAudience: KtclAudience,
        userAudience: String,
        providerName: String, userIssuer: UserIssuer,
    ): Res<PersistedProvider, KtcpErr>
}
