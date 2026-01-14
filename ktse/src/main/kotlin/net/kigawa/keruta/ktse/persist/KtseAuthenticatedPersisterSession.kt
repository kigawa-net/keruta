package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.TaskToCreate
import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession
import net.kigawa.keruta.ktse.zookeeper.ZkPersister
import net.kigawa.kodel.api.err.Res

class KtseAuthenticatedPersisterSession(
    val persister: ZkPersister,
    val authenticatedSession: AuthenticatedSession,
): AuthenticatedPersisterSession {
    override suspend fun createTask(
        task: TaskToCreate,
    ): Res<Unit, KtcpErr> {
        TODO()
    }
}
