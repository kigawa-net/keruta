package net.kigawa.keruta.ktcp.server.session

import net.kigawa.keruta.ktcp.server.auth.VerifiedToken

class AuthenticatedSession(
    val session: KtcpSession,
    val verifiedToken: VerifiedToken,
){
    val persisterSession = session.persisterSession.verify(this)
}
