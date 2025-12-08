package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

class ServerAuthenticateEntrypoint: AuthenticateEntrypoint {
    override fun access(
        input: AuthenticateMsg,
    ): KtcpRes? {
        TODO("Not yet implemented")
    }
}
