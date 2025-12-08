package net.kigawa.keruta.ktcp.model.authenticate

import net.kigawa.kodel.api.err.Res

interface UnverifiedAuthenticateToken {
    fun tryVerify(): Res<VerifiedAuthenticateToken, AuthenticateVerifyErr>
}
