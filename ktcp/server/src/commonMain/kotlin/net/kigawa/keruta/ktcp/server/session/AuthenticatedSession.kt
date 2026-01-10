package net.kigawa.keruta.ktcp.server.session

import net.kigawa.keruta.ktcp.server.auth.Verified

class AuthenticatedSession(
    val session: KtcpSession,
    val verified: Verified,
)
