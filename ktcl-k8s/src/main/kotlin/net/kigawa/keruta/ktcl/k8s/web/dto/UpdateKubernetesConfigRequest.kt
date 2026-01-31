package net.kigawa.keruta.ktcl.k8s.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateKubernetesConfigRequest(
    val namespace: String? = null,
    val useInCluster: Boolean? = null,
    val kubeconfigPath: String? = null,
    val jobTimeout: Long? = null
)
