package net.kigawa.keruta.ktcp.server.session

import net.kigawa.keruta.ktcp.domain.auth.AuthToken
import net.kigawa.keruta.ktcp.domain.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.kodel.api.err.Res

class AuthenticatedSession(
    val persisterSession: AuthenticatedPersisterSession,
    val session: KtcpSession,
) {
    fun createProviderRegisterToken(): Res<AuthToken, KtcpErr> =
        session.server.jwtVerifier.createToken(
            JwtVerifyValues(
                persisterSession.verifyTables.userIdp.issuer,
                "provider_register",
                persisterSession.verifyTables.userIdp.subject
            )
        )
}
