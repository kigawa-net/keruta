package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpEntrypoints
import net.kigawa.keruta.ktcp.server.authenticate.ServerAuthenticateEntrypoint

class KtcpServer(

) {

    fun getKtcpEntrypoints (connection: Connection)  = KtcpEntrypoints(
        ServerAuthenticateEntrypoint(connection)
    )

    fun startConnection(): Connection {
        return Connection(this)
    }
}
