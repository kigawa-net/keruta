package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.authenticate.ClientAuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.KtcpEntrypoints

class KtcpClient {
    val ktcpEntrypoints = KtcpEntrypoints(
        ClientAuthenticateEntrypoint()
    )
    fun startConnection(): ClientConnection {
        return ClientConnection()
    }
}
