package net.kigawa.keruta.ktcl.web

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)
