package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.auth.SendAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderAddEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderListEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueListEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueShowEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskListEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskMoveEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskShowEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskUpdateEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    @Suppress("unused")
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        SendAuthRequestEntrypoint(),
        SendTaskCreateEntrypoint(),
        SendTaskUpdateEntrypoint(),
        SendTaskMoveEntrypoint(),
        SendProviderListEntrypoint(),
        SendProviderAddEntrypoint(),
        SendProviderCompleteEntrypoint(),
        SendQueueCreateEntrypoint(),
        SendQueueListEntrypoint(),
        SendQueueShowEntrypoint(),
        SendTaskListEntrypoint(),
        SendTaskShowEntrypoint(),
    )

    @Suppress("unused")
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
