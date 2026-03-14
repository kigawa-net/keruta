package net.kigawa.keruta.ktcp.base.auth.key

import com.auth0.jwt.algorithms.Algorithm
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class Auth0AlgorithmInitializer {
    fun initPrivateKey(keyPair: KeyPair): Algorithm = when (val k = keyPair.private) {
        is RSAPrivateKey -> Algorithm.RSA256(
            keyPair.public as RSAPublicKey, k
        )

        else -> throw Exception("not supported")
    }
}
