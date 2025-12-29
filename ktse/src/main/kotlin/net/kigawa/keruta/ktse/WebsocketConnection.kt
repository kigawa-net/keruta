package net.kigawa.keruta.ktse

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.server.KtcpConnection
import net.kigawa.keruta.ktcp.model.serialize.MsgSerializer

class WebsocketConnection(
    val session: DefaultWebSocketServerSession,
): KtcpConnection {
    override suspend fun send(serializer: MsgSerializer, msg: @Serializable Any) {
        session.send(serializer.serialize(msg))
    }
}
