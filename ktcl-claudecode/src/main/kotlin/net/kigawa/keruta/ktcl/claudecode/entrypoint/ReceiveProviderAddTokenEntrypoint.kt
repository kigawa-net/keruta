package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.add_token.ClientProviderAddTokenEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderAddTokenEntrypoint : ClientProviderAddTokenEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderAddTokenMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}
