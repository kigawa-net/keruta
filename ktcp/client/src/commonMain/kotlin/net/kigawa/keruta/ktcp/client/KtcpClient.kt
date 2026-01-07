package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.authenticate.ClientAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ClientAuthRequestEntrypoint()
    )
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
