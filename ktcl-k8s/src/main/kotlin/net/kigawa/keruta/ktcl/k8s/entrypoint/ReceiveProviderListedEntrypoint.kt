package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderListedEntrypoint: ClientProviderListedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderListedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}
