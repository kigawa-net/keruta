package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.kodel.api.err.Res

interface VerifyTablesPersister {
    fun getVerifyTables(unverifiedTokens: UnverifiedAuthTokens): Res<PersistedVerifyTables, KtcpErr>?
    fun createVerifyTables(token: VerifiedAuthToken, providerName: String): Res<PersistedVerifyTables, KtcpErr>
}
