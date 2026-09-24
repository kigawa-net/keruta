package net.kigawa.keruta.kise.route

import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.kise.KiseConfig
import net.kigawa.keruta.kise.jwt.JwtIssuerImpl
import java.math.BigInteger
import java.util.Base64

class JwksRoute(
    private val config: KiseConfig,
    private val jwtIssuerImpl: JwtIssuerImpl?,
) {
    fun configure(route: Route) {
        route.get("/.well-known/jwks.json") {
            val keys = if (jwtIssuerImpl != null) {
                val pub = jwtIssuerImpl.getPublicKey()
                listOf(
                    mapOf(
                        "kty" to "RSA",
                        "use" to "sig",
                        "alg" to "RS256",
                        "kid" to "kise-key-1",
                        "n" to pub.modulus.toBase64UrlUnsigned(),
                        "e" to pub.publicExponent.toBase64UrlUnsigned(),
                    ),
                )
            } else {
                emptyList<Map<String, Any>>()
            }
            call.respond(mapOf("keys" to keys))
        }

        route.get("/.well-known/openid-configuration") {
            // jwks_uri はリクエストを受けたサーバーの URL を使用する
            val req = call.request.local
            val port = if ((req.scheme == "https" && req.serverPort == 443) ||
                (req.scheme == "http" && req.serverPort == 80)
            ) {
                ""
            } else {
                ":${req.serverPort}"
            }
            val serverBase = "${req.scheme}://${req.serverHost}$port"
            call.respond(
                mapOf(
                    "issuer" to config.issuer,
                    "jwks_uri" to "$serverBase/.well-known/jwks.json",
                    "response_types_supported" to listOf("code"),
                    "subject_types_supported" to listOf("public"),
                    "id_token_signing_alg_values_supported" to listOf("RS256"),
                ),
            )
        }
    }

    private fun BigInteger.toBase64UrlUnsigned(): String {
        val bytes = this.toByteArray()
        val unsigned = if (bytes.isNotEmpty() && bytes[0] == 0.toByte()) bytes.drop(1).toByteArray() else bytes
        return Base64.getUrlEncoder().withoutPadding().encodeToString(unsigned)
    }
}
