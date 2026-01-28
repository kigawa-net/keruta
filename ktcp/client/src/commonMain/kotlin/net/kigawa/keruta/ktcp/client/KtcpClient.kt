package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.auth.SendAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.client.provider.SendProviderListEntrypoint
import net.kigawa.keruta.ktcp.client.task.SendTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    @Suppress("unused")
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        SendAuthRequestEntrypoint(),
        SendTaskCreateEntrypoint(),
        SendProviderListEntrypoint(),
    )

    @Suppress("unused")
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
