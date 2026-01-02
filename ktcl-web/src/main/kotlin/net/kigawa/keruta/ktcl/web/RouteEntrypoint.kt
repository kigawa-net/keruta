package net.kigawa.keruta.ktcl.web

import io.ktor.server.routing.*
import net.kigawa.kodel.api.entrypoint.EntrypointNode
import net.kigawa.kodel.api.entrypoint.FlattedEntrypoint

class RouteEntrypoint(
    val webRoute: WebRoute,
): EntrypointNode<Routing, Unit, Unit> {


    override suspend fun access(input: Routing, ctx: Unit) {
        webRoute.apply {
            input.route()
        }
    }

    override fun flat(): List<FlattedEntrypoint<Routing, Unit, Unit>> {
        return listOf(
            FlattedEntrypoint(
                webRoute.info?.let { listOf(it) } ?: emptyList(),
            this
        ))
    }
}
