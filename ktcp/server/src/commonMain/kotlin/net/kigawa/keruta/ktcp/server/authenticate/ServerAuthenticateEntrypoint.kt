package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.EmptyKtcpRes
import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.server.ServerCtx

class ServerAuthenticateEntrypoint: AuthenticateEntrypoint<ServerCtx> {
    override fun access(
        input: AuthenticateMsg, ctx: ServerCtx,
    ): KtcpRes? {
        // TODO: トークンの検証ロジックを実装
        ctx.connection.authenticated()
        return EmptyKtcpRes
    }

}
