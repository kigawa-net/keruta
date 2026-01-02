package net.kigawa.keruta.ktcl.web

import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.auth.AuthEntrypoints
import net.kigawa.keruta.ktcl.web.kodel.WebEntrypoint
import net.kigawa.keruta.ktcl.web.kodel.WebEntrypointBase
import net.kigawa.keruta.ktcl.web.login.LoginPage
import net.kigawa.kodel.api.entrypoint.UnnamedEntrypointGroupBase

class WebEntrypoints(
    config: Config,
): UnnamedEntrypointGroupBase<Route, Unit, Unit>(), WebEntrypointBase {
    val top = add(WebEntrypoint(this, TopPage())) { this(it) }
    val login = add(WebEntrypoint(this, LoginPage(this))) {
        this(it)
    }
    val auth = add(AuthEntrypoints(config, this)) { this(it) }
    override val path: List<String>
        get() = emptyList()
}
