package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.TaskToCreate
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.kodel.api.err.Res
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.client.ZKClientConfig
import org.apache.zookeeper.data.ACL

class ZkPersister(
    ktseConfig: KtseConfig,
) {
    private val watcher = ServerWatcher()
    private val zkConfig = ZKClientConfig()
    private val zk = ZooKeeper(ktseConfig.zkHost, 5000, watcher, zkConfig)
    suspend fun getZk(): ZooKeeper {
        watcher.waitConnect()
        return zk
    }

    suspend fun createTask(
        taskToCreate: TaskToCreate, acls: List<ACL>,
    ): Res<Unit, KtcpErr> {
        getZk().create("", ByteArray(0), acls, CreateMode.PERSISTENT)
        TODO()
    }
}
