package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id

class ZkUser(
    val userId: String, val id: Id, val path: ZkPath, val persister: ZkPersister,
) {

    companion object {
        val usersPath = ZkPath("user")
    }

    suspend fun createQueue(queueId: String): Res<Unit, KtcpErr> {
        val queuePath = ZkQueue.queuePath + queueId
        val res = persister.create(
            queuePath, "", listOf(ACL(ZooDefs.Perms.ALL, id))
        )
        if (res is Res.Err) return res
    }
}
