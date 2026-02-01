package net.kigawa.keruta.ktse.auth.jwks

import com.auth0.jwk.JwkProviderBuilder
import net.kigawa.kodel.api.net.Url

class JwksConfigProvider {
    fun getByUrl(jwksUrl: Url) {
        JwkProviderBuilder(jwksUrl.toJvmUrl()).build()
    }
}
