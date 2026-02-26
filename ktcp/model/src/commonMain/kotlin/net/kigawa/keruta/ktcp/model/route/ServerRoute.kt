package net.kigawa.keruta.ktcp.model.route

import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

interface ServerRoute: KtcpRoute {
    val type: ServerMsgType
}
