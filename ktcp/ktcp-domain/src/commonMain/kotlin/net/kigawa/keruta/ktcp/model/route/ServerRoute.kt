package net.kigawa.keruta.ktcp.model.route

import kotlinx.serialization.KSerializer
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

class ServerRoute<T: ServerMsg>(
    override val type: ServerMsgType,
    override val serializer: KSerializer<T>,
): KtcpRoute<T>
