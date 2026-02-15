package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.deleted.ClientProviderDeletedEntrypoint
import net.kigawa.keruta.ktcp.model.provider.deleted.ClientProviderDeletedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderDeletedEntrypoint : ClientProviderDeletedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderDeletedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}
