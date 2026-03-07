package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.auth.SendAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderDeleteEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderIssueTokenEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderListEntrypoint
import net.kigawa.keruta.ktcp.client.queue.*
import net.kigawa.keruta.ktcp.client.task.*
import net.kigawa.keruta.ktcp.domain.KtcpServerEntrypoints

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
        SendQueueUpdateEntrypoint(),
        SendQueueDeleteEntrypoint(),
        SendTaskListEntrypoint(),
        SendTaskShowEntrypoint(),
    )

    @Suppress("unused")
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
