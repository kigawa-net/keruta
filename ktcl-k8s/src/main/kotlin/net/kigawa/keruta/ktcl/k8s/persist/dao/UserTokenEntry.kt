package net.kigawa.keruta.ktcl.k8s.persist.dao

data class UserTokenEntry(
    val userSubject: String,
    val userIssuer: String,
    val userAudience: String,
    val refreshToken: String,
)
