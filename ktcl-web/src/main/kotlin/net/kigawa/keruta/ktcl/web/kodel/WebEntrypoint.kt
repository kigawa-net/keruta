package net.kigawa.keruta.ktcl.web.kodel

import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointNode
import net.kigawa.kodel.api.entrypoint.FlattedEntrypoint

class WebEntrypoint(
    val parent: WebEntrypointBase,
    val webRoute: WebRoute,
): EntrypointNode<Route, Unit, Unit>, WebEntrypointBase {


    override fun access(input: Route, ctx: Unit) {
        webRoute.apply {
            input.route()
        }
    }

    override fun flat(): List<FlattedEntrypoint<Route, Unit, Unit>> {
        return listOf(
            FlattedEntrypoint(
                webRoute.info?.let { listOf(it) } ?: emptyList(),
                this
            ))
    }

    override val path: List<String>
        get() = webRoute.info?.let { parent.path + it.name.raw } ?: parent.path
}
