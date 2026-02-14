package net.kigawa.keruta.ktcp.client.auth

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendAuthRequestEntrypoint: ServerAuthRequestEntrypoint<ClientCtx> {
    override fun access(
        input: ServerAuthRequestMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input))
        Res.Ok(Unit)
    }
}
