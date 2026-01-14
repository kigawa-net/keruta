package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.TaskToCreate
import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession
import net.kigawa.kodel.api.err.Res

class ZkAuthenticatedPersisterSession(
    val persister: ZkPersister,
    authenticatedSession: AuthenticatedSession,
    val zkUser: ZkUser
): AuthenticatedPersisterSession {
    override suspend fun createTask(
        task: TaskToCreate,
    ): Res<Unit, KtcpErr> {
        zkUser
    }
}
