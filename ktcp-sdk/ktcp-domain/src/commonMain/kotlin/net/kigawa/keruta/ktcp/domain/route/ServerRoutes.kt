package net.kigawa.keruta.ktcp.domain.route

import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.kodel.api.routing.RouteGroup

class ServerRoutes: RouteGroup<ServerRoute<out ServerMsg>>() {
    val authRequest = route {
        ServerRoute(ServerMsgType.AUTH_REQUEST, ServerAuthRequestMsg.serializer())
    }
}
