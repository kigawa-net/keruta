package net.kigawa.keruta.ktse.persist.accessor

import net.kigawa.keruta.ktcp.model.KtclAudience
import net.kigawa.keruta.ktcp.model.UserIssuer
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifiedToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.UnverifiedAuthTokens
import net.kigawa.keruta.ktcp.server.auth.VerifiedAuthToken
import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url

class ExposedVerifyTablesPersister(
    val dbPersister: DbPersister,
): VerifyTablesPersister {
    override fun createVerifyTables(
        token: VerifiedAuthToken,
        providerName: String,
    ): Res<PersistedVerifyTables, KtcpErr> = dbPersister.execTransaction {
        it.insertVerifyTables(
            token.user.issuer, token.user.audience, token.user.subject,
            token.provider.issuer, token.provider.audience, providerName
        )
    }

    override fun getUserTables(
        issuer: Url, subject: String,
    ): Res<Pair<PersistedUser, PersistedUserIdp>, KtcpErr> = dbPersister.execTransaction {
        it.findUserTables(issuer, subject)
    }

    override fun saveProviderForUser(
        user: PersistedUser, providerToken: VerifiedToken, ktclAudience: KtclAudience,
        userAudience: String,
        providerName: String, userIssuer: UserIssuer,
    ): Res<PersistedProvider, KtcpErr> = dbPersister.execTransaction {
        it.insertProviderForUser(
            user, providerToken.issuer, ktclAudience, providerName,
            providerToken.subject, userIssuer
        )
    }


    override fun getVerifyTables(
        unverifiedTokens: UnverifiedAuthTokens,
    ): Res<PersistedVerifyTables, KtcpErr>? = dbPersister.execTransaction {
        it.findVerifyTables(
            unverifiedTokens.userToken.issuer, unverifiedTokens.userToken.subject,
            unverifiedTokens.providerToken.issuer
        )
    }
}
