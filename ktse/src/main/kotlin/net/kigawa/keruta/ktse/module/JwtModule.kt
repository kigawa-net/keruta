package net.kigawa.keruta.ktse.module

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.concurrent.TimeUnit

object JwtModule {

    fun module(application: Application) = application.apply {
        val issuer = environment.config.property("ktor.security.keycloak.issuer").getString()
        val audience = environment.config.property("ktor.security.keycloak.audience").getString()
        val myRealm = environment.config.property("ktor.security.keycloak.realm").getString()
        val jwkProvider = JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        install(Authentication) {
            jwt("keycloak") {
                realm = myRealm
                verifier(jwkProvider, issuer) {
                    acceptLeeway(3)
                }
                validate { credential ->
                    val audClaim = credential.payload.audience
                    val clientId = credential.payload.getClaim("azp").asString()

                    if (audClaim.contains(audience) && clientId != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }
    }
}
