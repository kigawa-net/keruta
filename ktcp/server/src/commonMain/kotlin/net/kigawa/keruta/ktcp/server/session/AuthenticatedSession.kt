package net.kigawa.keruta.ktcp.server.session

import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession

class AuthenticatedSession(
    val persisterSession: AuthenticatedPersisterSession,
)
