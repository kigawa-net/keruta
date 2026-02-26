package net.kigawa.keruta.ktcp.model.route

import net.kigawa.kodel.api.routing.RouteGroup

class ServerRoutes(
    a: ServerRoute,
    b: ServerRoute,
): RouteGroup<ServerRoute>() {
    val a = route {
        a
    }
    val b = route { b }
}
