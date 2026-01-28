package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.auth.SendAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderListEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.client.queue.SendQueueListEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    @Suppress("unused")
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        SendAuthRequestEntrypoint(),
        SendTaskCreateEntrypoint(),
        SendProviderListEntrypoint(),
        SendQueueCreateEntrypoint(),
        SendQueueListEntrypoint(),
    )

    @Suppress("unused")
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
