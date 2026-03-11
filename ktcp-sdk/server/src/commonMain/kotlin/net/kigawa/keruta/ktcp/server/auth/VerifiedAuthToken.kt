package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.domain.auth.jwt.VerifiedToken

class VerifiedAuthToken(val user: VerifiedToken, val provider: VerifiedToken)
