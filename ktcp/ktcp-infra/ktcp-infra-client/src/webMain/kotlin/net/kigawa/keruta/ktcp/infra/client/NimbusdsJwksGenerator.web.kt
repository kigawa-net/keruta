package net.kigawa.keruta.ktcp.infra.client

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import net.kigawa.keruta.ktcp.model.serialize.JsonString

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class NimbusdsJwksGenerator: NimbusdsJwksGeneratorBase() {

    actual override fun platformGenerate(key: KerutaPrivateKey): JsonString {
        TODO("Not yet implemented")
    }
}
