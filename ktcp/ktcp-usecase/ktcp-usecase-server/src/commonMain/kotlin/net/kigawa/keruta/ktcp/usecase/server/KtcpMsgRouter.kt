package net.kigawa.keruta.ktcp.usecase.server

import net.kigawa.keruta.ktcp.model.msg.server.ServerUnknownMsg
import net.kigawa.keruta.ktcp.model.server.KtcpServer

class KtcpMsgRouter(ktcpServer: KtcpServer) {
    fun route(msg: ServerUnknownMsg) {}
}
