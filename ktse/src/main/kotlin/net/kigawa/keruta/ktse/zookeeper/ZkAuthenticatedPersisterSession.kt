package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.TaskToCreate
import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession
import net.kigawa.kodel.api.err.Res
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.data.ACL

class ZkAuthenticatedPersisterSession(
    val persister: ZkPersister,
    val authenticatedSession: AuthenticatedSession,
): AuthenticatedPersisterSession {
    override suspend fun createTask(
        task: TaskToCreate,
    ): Res<Unit, KtcpErr> {
        val zkUser = when (val res = persister.getUser(authenticatedSession.verified.user.sub)) {
            is Res.Err -> return res.convertType()
            is Res.Ok -> res.value
        }

        when (val res = zkUser.createQueue(task.queueId)) {
            is Res.Err -> return res
            is Res.Ok -> {}
        }

        val taskPath = ZkQueue.queuePath + task.queueId + "task" + task.name
        val taskData = persister.serializer.serialize(task)
        return persister.create(
            taskPath,
            taskData,
            listOf(ACL(ZooDefs.Perms.ALL, zkUser.id))
        )
    }
}
