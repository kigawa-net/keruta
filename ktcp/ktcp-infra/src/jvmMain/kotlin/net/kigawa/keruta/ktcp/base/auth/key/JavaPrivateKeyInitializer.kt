package net.kigawa.keruta.ktcp.base.auth.key

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PEMDecoder
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.RSAPublicKeySpec

class JavaPrivateKeyInitializer {
    fun initialize(key: KerutaPrivateKey): KeyPair {
        val privateKey = PEMDecoder.of()
            .decode(key.strKey, RSAPrivateCrtKey::class.java)

        val kf = KeyFactory.getInstance("RSA")
        val pubSpec = RSAPublicKeySpec(privateKey.modulus, privateKey.publicExponent)
        val pubKey = kf.generatePublic(pubSpec)
        val keyPair = KeyPair(pubKey, privateKey)
        return keyPair
    }
}
