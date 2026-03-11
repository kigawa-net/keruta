package net.kigawa.keruta.ktcp.usecase.client

import net.kigawa.keruta.ktcp.domain.auth.key.PemKey

interface JwksJsonGenerator {
    fun generate(privateKey: PemKey): Map<String?, Any?>
}
