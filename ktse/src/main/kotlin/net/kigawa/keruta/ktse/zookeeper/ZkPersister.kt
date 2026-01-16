package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.err.BackendErr
import net.kigawa.kodel.api.err.Res
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.client.ZKClientConfig
import org.apache.zookeeper.data.ACL

class ZkPersister(
    ktseConfig: KtseConfig,
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

    suspend fun exists(path: ZkPath): Res<Boolean, KtcpErr> = try {
        Res.Ok(getZk().exists(path.str, false) != null)
    } catch (e: KeeperException) {
        Res.Err(BackendErr("", e))
    }

}
