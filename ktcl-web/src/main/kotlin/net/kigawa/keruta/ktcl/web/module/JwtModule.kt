package net.kigawa.keruta.ktcl.web.module

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.concurrent.TimeUnit

import net.kigawa.keruta.ktcl.web.Config

object JwtModule {

    fun module(application: Application, config: Config) = application.apply {
        val jwkProvider = JwkProviderBuilder(config.issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        install(Authentication) {
            jwt("auth-jwt") {
                realm = config.realm
                verifier(jwkProvider)
                validate { credential ->
                    if (credential.payload.audience.contains(config.audience)) {
                        JWTPrincipal(credential.payload)
                    } else null
                }
            }
        }
    }
}
