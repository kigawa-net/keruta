package net.kigawa.keruta.ktcp.usecase.client

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey

interface JwksJsonGenerator {
    fun generate(privateKey: KerutaPrivateKey): Map<String?, Any?>
}
