package net.kigawa.keruta.ktcp.infra.client

import net.kigawa.keruta.ktcp.model.auth.key.PemKey

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class NimbusdsJwksGenerator: NimbusdsJwksGeneratorBase {
    override fun platformGenerate(key: PemKey): Map<String?, Any?>
}
