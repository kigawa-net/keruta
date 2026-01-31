package net.kigawa.keruta.ktcl.k8s

import io.ktor.server.application.*
import net.kigawa.keruta.ktcl.k8s.web.WebApplicationModule

object KtclK8sServer {
    fun Application.module() {
        WebApplicationModule().configure(this)
    }
}
