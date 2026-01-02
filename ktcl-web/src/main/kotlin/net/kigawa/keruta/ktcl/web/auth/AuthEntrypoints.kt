package net.kigawa.keruta.ktcl.web.auth

import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.Config
import net.kigawa.keruta.ktcl.web.kodel.WebEntrypoint
import net.kigawa.keruta.ktcl.web.kodel.WebEntrypointBase
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class AuthEntrypoints(
    config: Config,
    val parent: WebEntrypointBase,
): EntrypointGroupBase<Route, Unit, Unit>(), WebEntrypointBase {
    val keycloak = add(WebEntrypoint(this, KeycloakRoute(config))) { this(it) }
    val callback = add(WebEntrypoint(this, CallbackRoute())) { this(it) }
    override fun onSubEntrypointNotFound(input: Route) {
        throw IllegalStateException("Not found")
    }

    override val info: EntrypointInfo
        get() = EntrypointInfo("auth", listOf(), "Authentication entrypoints")
    override val path: List<String>
        get() = parent.path + listOf(info.name.raw)
}
