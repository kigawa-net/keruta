package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface PersisterSession {
    suspend fun verify(authRequestMsg: ServerAuthRequestMsg): Res<AuthenticatedPersisterSession, KtcpErr>
}
