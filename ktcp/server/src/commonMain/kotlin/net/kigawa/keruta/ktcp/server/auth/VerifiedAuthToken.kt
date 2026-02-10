package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.server.auth.jwt.VerifiedToken

class VerifiedAuthToken(val user: VerifiedToken, val provider: VerifiedToken)
