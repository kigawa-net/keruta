package net.kigawa.keruta.ktcl.web

import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.login.LoginPage
import net.kigawa.kodel.api.entrypoint.UnnamedEntrypointGroupBase

class WebEntrypoints: UnnamedEntrypointGroupBase<Routing, Unit, Unit>() {
    val top = add(RouteEntrypoint(TopPage())) { this(it) }
    val login = add(RouteEntrypoint(LoginPage())) { this(it) }
}
