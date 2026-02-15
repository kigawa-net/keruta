package net.kigawa.keruta.ktcp.base.auth

import com.auth0.jwt.interfaces.DecodedJWT

val DecodedJWT.str: String get() = "DecodedJWT(iss: $issuer)"
