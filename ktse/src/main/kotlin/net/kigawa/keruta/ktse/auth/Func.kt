package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.interfaces.DecodedJWT

val DecodedJWT.str: String get() = "DecodedJWT(iss: $issuer)"
