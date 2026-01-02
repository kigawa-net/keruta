package net.kigawa.keruta.ktcl.web.module

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.concurrent.TimeUnit

object JwtModule {

    fun module(application: Application) = application.apply {
        val issuer = environment.config.property("ktor.security.jwt.issuer").getString()
        val audience = environment.config.property("ktor.security.jwt.audience").getString()
        val myRealm = environment.config.property("ktor.security.jwt.realm").getString()
        val jwkProvider = JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        install(Authentication) {
            jwt("auth-jwt") {
                realm = myRealm
                verifier(jwkProvider)
                validate { credential ->
                    if (credential.payload.audience.contains(audience)) {
                        JWTPrincipal(credential.payload)
                    } else null
                }
            }
        }
    }
}
