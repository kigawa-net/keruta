package net.kigawa.keruta.ktse.persist.accessor

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.UnverifiedAuthTokens
import net.kigawa.keruta.ktcp.server.auth.VerifiedAuthToken
import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

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

    override fun getVerifyTables(
        unverifiedTokens: UnverifiedAuthTokens,
    ): Res<PersistedVerifyTables, KtcpErr>? = dbPersister.execTransaction {
        it.findVerifyTables(
            unverifiedTokens.userToken.issuer, unverifiedTokens.userToken.subject,
            unverifiedTokens.providerToken.issuer
        )
    }
}
