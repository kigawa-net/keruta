package net.kigawa.keruta.ktcp.model.route

import kotlinx.serialization.KSerializer
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

class ServerRoute<T: KtcpMsg>(
    val type: ServerMsgType,
    val serializer: KSerializer<T>,
): KtcpRoute
