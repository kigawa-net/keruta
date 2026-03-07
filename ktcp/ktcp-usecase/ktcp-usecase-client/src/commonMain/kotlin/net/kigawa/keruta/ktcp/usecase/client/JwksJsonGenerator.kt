package net.kigawa.keruta.ktcp.usecase.client

import net.kigawa.keruta.ktcp.model.auth.key.PemKey

interface JwksJsonGenerator {
    fun generate(privateKey: PemKey): Map<String?, Any?>
}
