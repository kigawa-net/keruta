package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.TaskToCreate
import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession
import net.kigawa.kodel.api.err.Res
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id

class ZkAuthenticatedPersisterSession(
    val persister: ZkPersister,
    authenticatedSession: AuthenticatedSession,
): AuthenticatedPersisterSession {
    val id = Id("auth", authenticatedSession.verified.sub)
    override suspend fun createTask(
        task: TaskToCreate,
    ): Res<Unit, KtcpErr> {
        return persister.createTask(task, listOf(ACL(ZooDefs.Perms.ALL, id)))
    }
}
