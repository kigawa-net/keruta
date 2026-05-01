package net.kigawa.keruta.ktcp.infra.client

import net.kigawa.keruta.ktcp.domain.auth.key.PemKey
import net.kigawa.keruta.ktcp.usecase.client.JwksJsonGenerator

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
abstract class NimbusdsJwksGeneratorBase: JwksJsonGenerator {

    override fun generate(
        privateKey: PemKey,
    ): Map<String?, Any?> {
        return platformGenerate(privateKey)
    }

    protected abstract fun platformGenerate(key: PemKey): Map<String?, Any?>
}
