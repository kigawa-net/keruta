package net.kigawa.keruta.ktcp.usecase.client

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import net.kigawa.keruta.ktcp.model.serialize.JsonString

interface JwksJsonGenerator {
    fun generate(privateKey: KerutaPrivateKey): JsonString
}
