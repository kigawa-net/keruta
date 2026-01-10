package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.authenticate.SendAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        SendAuthRequestEntrypoint()
    )
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
