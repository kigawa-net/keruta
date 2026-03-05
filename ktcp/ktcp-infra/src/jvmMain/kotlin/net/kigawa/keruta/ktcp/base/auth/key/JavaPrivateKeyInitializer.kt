package net.kigawa.keruta.ktcp.base.auth.key

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import java.security.PEMDecoder
import java.security.interfaces.RSAPrivateCrtKey

class JavaPrivateKeyInitializer {
    fun initialize(key: KerutaPrivateKey): RSAPrivateCrtKey = PEMDecoder.of()
        .decode(key.strKey, RSAPrivateCrtKey::class.java)
}
