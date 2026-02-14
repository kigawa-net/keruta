package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.idp_added.ClientProviderIdpAddedEntrypoint
import net.kigawa.keruta.ktcp.model.provider.idp_added.ClientProviderIdpAddedMsg
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
