package net.kigawa.keruta.ktcl.k8s.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class KubernetesConfig(
    val namespace: String,
    val useInCluster: Boolean,
    val kubeconfigPath: String?,
    val jobTimeout: Long
)
