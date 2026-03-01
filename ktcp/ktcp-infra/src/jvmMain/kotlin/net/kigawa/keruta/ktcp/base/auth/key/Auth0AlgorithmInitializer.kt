package net.kigawa.keruta.ktcp.base.auth.key

import com.auth0.jwt.algorithms.Algorithm
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateKey

class Auth0AlgorithmInitializer {
    fun initPrivateKey(key: PrivateKey): Algorithm = when (key) {
        is RSAPrivateKey -> Algorithm.RSA256(null, key)
        else -> throw Exception("not supported")
    }
}
