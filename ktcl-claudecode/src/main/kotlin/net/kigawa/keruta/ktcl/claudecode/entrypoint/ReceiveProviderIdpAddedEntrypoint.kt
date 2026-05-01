package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.idp_added.ClientProviderIdpAddedEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.idp_added.ClientProviderIdpAddedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderIdpAddedEntrypoint : ClientProviderIdpAddedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderIdpAddedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}
