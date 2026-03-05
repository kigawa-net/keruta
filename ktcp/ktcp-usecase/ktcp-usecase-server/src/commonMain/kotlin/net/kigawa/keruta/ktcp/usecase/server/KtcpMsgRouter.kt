package net.kigawa.keruta.ktcp.usecase.server

import net.kigawa.keruta.ktcp.model.WebsocketPacket
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.route.KtcpRoute
import net.kigawa.kodel.api.routing.RouteGroup

class KtcpMsgRouter<out T: KtcpMsg>(
    private val route: RouteGroup<out KtcpRoute<out T>>,
) {
    fun route(packet: WebsocketPacket) {
//route.routes.first { it.type }
    }
}
