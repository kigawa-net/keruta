package net.kigawa.keruta.ktcp.client.authenticate

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendAuthRequestEntrypoint: ServerAuthRequestEntrypoint<ClientCtx> {
    override fun access(
        input: ServerAuthRequestArg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.msgSender.sendMsg(input)
        Res.Ok(Unit)
    }
}
