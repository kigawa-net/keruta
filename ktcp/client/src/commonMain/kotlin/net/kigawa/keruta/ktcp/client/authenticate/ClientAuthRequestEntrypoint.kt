package net.kigawa.keruta.ktcp.client.authenticate

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ClientAuthRequestEntrypoint: AuthRequestEntrypoint<ClientCtx> {
    override fun access(
        input: AuthRequestArg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.msgSender.sendMsg(input)
        Res.Ok(Unit)
    }
}
