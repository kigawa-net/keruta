package net.kigawa.keruta.ktcl.k8s

import net.kigawa.keruta.ktcl.k8s.config.KerutaConfig

class KerutaEndpoints(
    val kerutaConfig: KerutaConfig,
) {
    val ownIssuer by kerutaConfig::ownIssuer
    val callback get() = ownIssuer.plusPath("/login/callback")
}
