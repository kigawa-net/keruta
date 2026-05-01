package net.kigawa.keruta.ktcl.k8s.serialize

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

class SerializeModule {
    fun configure(application: Application) {
        application.install(ContentNegotiation) {
            json()
        }
    }
}
