package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.auth.SendAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderDeleteEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderIssueTokenEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderListEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueListEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueShowEntrypoint
import net.kigawa.keruta.ktcp.client.task.*
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    @Suppress("unused")
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        SendAuthRequestEntrypoint(),
        SendTaskCreateEntrypoint(),
        SendTaskUpdateEntrypoint(),
        SendTaskMoveEntrypoint(),
        SendProviderListEntrypoint(),
        SendProviderIssueTokenEntrypoint(),
        SendProviderCompleteEntrypoint(),
        SendProviderDeleteEntrypoint(),
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
