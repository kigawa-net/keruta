package net.kigawa.keruta.ktcl.k8s

import io.ktor.server.application.*
import net.kigawa.keruta.ktcl.k8s.web.WebApplicationModule

@Suppress("unused")
object KtclK8sServer {
    fun Application.module() {
        WebApplicationModule(this).configure()
    }
}
