package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.err.BackendErr
import net.kigawa.keruta.ktse.zookeeper.ZkUser.Companion.usersPath
import net.kigawa.kodel.api.err.Res
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.client.ZKClientConfig
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id

class ZkPersister(
    ktseConfig: KtseConfig,
    val serializer: KerutaSerializer,
) {
    private val watcher = ServerWatcher()
    private val zkConfig = ZKClientConfig()
    private val zk = ZooKeeper(ktseConfig.zkHost, 5000, watcher, zkConfig)


    private suspend fun getZk(): ZooKeeper {
        watcher.waitConnect()
        return zk
    }

    suspend fun create(path: ZkPath, data: String, acls: List<ACL>): Res<Unit, KtcpErr> = try {
        getZk().create(path.str, data.toByteArray(), acls, CreateMode.PERSISTENT)
        Res.Ok(Unit)
    } catch (e: KeeperException) {
        Res.Err(BackendErr("", e))
    }

    suspend fun createIfNotExists(
        path: ZkPath, data: String, acls: List<ACL>,
    ): Res<Unit, KtcpErr> = when (val res = exists(path)) {
        is Res.Err -> res.convertType()
        is Res.Ok -> create(path, data, acls)
    }

    suspend fun exists(path: ZkPath): Res<Boolean, KtcpErr> = try {
        Res.Ok(getZk().exists(path.str, false) != null)
    } catch (e: KeeperException) {
        Res.Err(BackendErr("", e))
    }

    suspend fun getUser(userId: String): Res<ZkUser, KtcpErr> {
        val id = Id("auth", userId)
        val path = usersPath + userId
        return when (val res =
            createIfNotExists(
                path, "", listOf(ACL(ZooDefs.Perms.ALL, id))
            )
        ) {
            is Res.Err -> res.convertType()
            is Res.Ok -> Res.Ok(ZkUser(userId, id, path, this))
        }
    }
}
