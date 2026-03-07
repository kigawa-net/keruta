package net.kigawa.keruta.ktcp.usecase.server

import net.kigawa.keruta.ktcp.domain.WebsocketPacket
import net.kigawa.keruta.ktcp.domain.msg.KtcpMsg
import net.kigawa.keruta.ktcp.domain.route.KtcpRoute
import net.kigawa.kodel.api.routing.RouteGroup

class KtcpMsgRouter<out T: KtcpMsg>(
    private val route: RouteGroup<out KtcpRoute<out T>>,
) {
    fun route(packet: WebsocketPacket) {
//route.routes.first { it.type }
    }
}
