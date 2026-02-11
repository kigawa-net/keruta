package net.kigawa.keruta.ktcl.mobile.connection

import net.kigawa.keruta.ktcp.client.KtcpConnection

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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

    @Suppress("unused")
    suspend fun close() = connection.close()
}
