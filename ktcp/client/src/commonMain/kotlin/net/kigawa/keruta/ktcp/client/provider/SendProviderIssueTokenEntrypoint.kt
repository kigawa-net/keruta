package net.kigawa.keruta.ktcp.client.provider

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderIssueTokenEntrypoint: ServerProviderIssueTokenEntrypoint<ClientCtx> {
    override fun access(
        input: ServerProviderIssueTokenMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer.serialize(input))
            Res.Ok(Unit)
        }
    }
}
