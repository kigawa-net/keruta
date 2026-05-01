package net.kigawa.keruta.ktcl.mobile.connection

import kotlinx.coroutines.flow.SharedFlow
import net.kigawa.keruta.ktcp.client.KtcpConnection

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class MobileWebSocketConnection {
    suspend fun send(msg: String)
    suspend fun close()
    val messages: SharedFlow<String>
}

class MobileKtcpConnection(
    private val connection: MobileWebSocketConnection,
) : KtcpConnection {
    override suspend fun send(msg: String) = connection.send(msg)

    val messages: SharedFlow<String> = connection.messages

    @Suppress("unused")
    suspend fun close() = connection.close()
}
