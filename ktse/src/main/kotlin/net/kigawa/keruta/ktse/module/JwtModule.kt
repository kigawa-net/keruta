package net.kigawa.keruta.ktse.module

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.concurrent.TimeUnit

object JwtModule {

    fun module(application: Application) = application.apply {
        val issuer = environment.config.property("jwt.issuer").getString()
        val audience = environment.config.property("jwt.audience").getString()
        val myRealm = environment.config.property("jwt.realm").getString()
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
                    val clientId = credential.payload.getClaim("azp").asString()
                    if (clientId != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }
    }
}
