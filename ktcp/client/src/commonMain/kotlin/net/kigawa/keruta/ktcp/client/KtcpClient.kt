package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.authenticate.ClientAuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints

class KtcpClient {
    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ClientAuthenticateEntrypoint()
    )
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
