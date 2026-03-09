package net.kigawa.keruta.ktcl.k8s

import io.ktor.server.application.*
import net.kigawa.keruta.ktcl.k8s.web.WebApplicationModule

@Suppress("unused")
object KtclK8sServer {
    val webApplicationModule = WebApplicationModule()
    fun Application.module() {
        webApplicationModule.configure(this)
    }
}
