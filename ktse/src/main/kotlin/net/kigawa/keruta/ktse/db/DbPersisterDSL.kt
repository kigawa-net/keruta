package net.kigawa.keruta.ktse.db

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.kodel.api.err.Res

class DbPersisterDSL {
    fun getUserIdpOrNull(subject: String, issuer: String): Res<PersistedUserIdp, KtcpErr>? {
        TODO("Not yet implemented")
    }

    fun getUser(value: VerifiedToken): Res<PersistedUser, KtcpErr> {
        TODO("Not yet implemented")
    }

    fun createUserAndIdp(idp: IdpConfig, verifiedToken: VerifiedToken): Res<PersistedUser, KtcpErr> {
        TODO("Not yet implemented")
    }

    fun getProviderOrNull(issuer: String, id: Long): Res<PersistedProvider, KtcpErr>? {
        TODO("Not yet implemented")
    }

    fun createProvider(idp: IdpConfig, value: VerifiedToken): Res<PersistedProvider, KtcpErr> {
        TODO("Not yet implemented")
    }

}
