package net.kigawa.keruta.ktcl.mobile.connection

import net.kigawa.keruta.ktcp.model.KtcpConnection

expect class MobileWebSocketConnection {
    suspend fun send(msg: String)
    suspend fun receive(): String?
    suspend fun close()
}

class MobileKtcpConnection(
    private val connection: MobileWebSocketConnection,
) : KtcpConnection {
    override suspend fun send(msg: String) = connection.send(msg)

    suspend fun receive(): String? = connection.receive()

    suspend fun close() = connection.close()
}
