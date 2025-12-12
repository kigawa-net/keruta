package net.kigawa.keruta.ktcp.client.authenticate

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.EmptyKtcpRes
import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

class ClientAuthenticateEntrypoint: AuthenticateEntrypoint<ClientCtx> {
    override fun access(
        input: AuthenticateMsg, ctx: ClientCtx,
    ): KtcpRes {
        ctx.msgSender.sendMsg(input)
        return EmptyKtcpRes
    }
}
