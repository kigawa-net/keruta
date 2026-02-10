package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedArg
import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderCreatedEntrypoint : ClientProviderCreatedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderCreatedArg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}
