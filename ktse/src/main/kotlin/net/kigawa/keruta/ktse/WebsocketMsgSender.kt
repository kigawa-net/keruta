package net.kigawa.keruta.ktse

import io.ktor.server.websocket.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.server.MsgSerializer

class WebsocketMsgSender(
    val session: DefaultWebSocketServerSession,
    val serializer: MsgSerializer
): MsgSender {
    override suspend fun send(msg: @Serializable Any) {
        session.send(serializer.serialize(msg))
    }
}
