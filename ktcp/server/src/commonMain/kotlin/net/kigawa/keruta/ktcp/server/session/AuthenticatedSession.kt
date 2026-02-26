package net.kigawa.keruta.ktcp.server.session

import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.kodel.api.err.Res

class AuthenticatedSession(
    val persisterSession: AuthenticatedPersisterSession,
    val session: KtcpSession,
) {
    fun createProviderRegisterToken(): Res<AuthToken, KtcpErr> =
        session.server.jwtVerifier.createToken()
}
