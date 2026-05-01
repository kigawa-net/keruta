package net.kigawa.keruta.ktcp.domain.route

import kotlinx.serialization.KSerializer
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

class ServerRoute<T: ServerMsg>(
    override val type: ServerMsgType,
    override val serializer: KSerializer<T>,
): KtcpRoute<T>
