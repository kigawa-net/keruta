package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession

interface PersisterSession {
    fun verify(verifiedSession: AuthenticatedSession): AuthenticatedPersisterSession
}
