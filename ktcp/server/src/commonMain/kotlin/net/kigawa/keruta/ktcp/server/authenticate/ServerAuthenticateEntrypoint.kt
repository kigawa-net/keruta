package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.EmptyKtcpRes
import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

import net.kigawa.keruta.ktcp.model.err.KtcpErrRes
import net.kigawa.keruta.ktcp.server.Connection
import net.kigawa.kodel.api.err.Res
import kotlin.time.ExperimentalTime

class ServerAuthenticateEntrypoint(
    val connection: Connection
): AuthenticateEntrypoint {
    @OptIn(ExperimentalTime::class)
    override fun access(
        input: AuthenticateMsg,
    ): KtcpRes? {
        return when (val res = input.token.tryVerify()){
            is Res.Err -> KtcpErrRes(
                code = res.err.code,
                message = res.err.message ?: "Unknown error",
                retryable = false,
            )
            is Res.Ok -> {
                connection.authenticated()
                EmptyKtcpRes
            }
        }
    }

}
