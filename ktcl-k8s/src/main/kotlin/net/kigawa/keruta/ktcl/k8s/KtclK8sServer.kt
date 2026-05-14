package net.kigawa.keruta.ktcl.k8s

import io.ktor.server.application.*
import net.kigawa.keruta.ktcl.k8s.web.WebApplicationModule

@Suppress("unused")
class KtclK8sServer {
    private val webApplicationModule = WebApplicationModule()
    fun Application.module() {
        webApplicationModule.configure(this)
    }
}
