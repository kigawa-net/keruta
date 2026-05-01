package net.kigawa.keruta.ktse.zookeeper

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher

class ServerWatcher(
) : Watcher {
    private val job: CompletableJob = Job()
    override fun process(event: WatchedEvent) {
        if (event.state == Watcher.Event.KeeperState.SyncConnected) job.complete()
    }

    suspend fun waitConnect() = job.join()
}
