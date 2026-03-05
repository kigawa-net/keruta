package net.kigawa.keruta.ktcp.infra.client

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import net.kigawa.keruta.ktcp.model.serialize.JsonString
import net.kigawa.keruta.ktcp.usecase.client.JwksJsonGenerator

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
abstract class NimbusdsJwksGeneratorBase: JwksJsonGenerator {

    override fun generate(
        privateKey: KerutaPrivateKey,
    ): JsonString {
        return platformGenerate(privateKey)
    }

    protected abstract fun platformGenerate(key: KerutaPrivateKey): JsonString
}
