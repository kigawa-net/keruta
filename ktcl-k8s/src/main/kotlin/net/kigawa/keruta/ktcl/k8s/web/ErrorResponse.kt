package net.kigawa.keruta.ktcl.k8s.web

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String,
)