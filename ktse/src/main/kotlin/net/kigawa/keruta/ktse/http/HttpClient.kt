package net.kigawa.keruta.ktse.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

class HttpClient {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun get(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = client.get(urlString, block)
}
