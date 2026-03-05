package net.kigawa.keruta.ktcp.model.route

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.kodel.api.routing.RouteGroup

class ServerRoutes: RouteGroup<ServerRoute<out ServerMsg>>() {
    val authRequest = route {
        ServerRoute(ServerMsgType.AUTH_REQUEST, ServerAuthRequestMsg.serializer())
    }
}
