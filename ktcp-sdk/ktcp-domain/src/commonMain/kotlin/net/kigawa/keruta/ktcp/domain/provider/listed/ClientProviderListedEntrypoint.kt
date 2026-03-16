package net.kigawa.keruta.ktcp.domain.provider.listed

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientProviderListedEntrypoint<C>:
    Entrypoint<ClientProviderListedMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("provider_list", emptyList(), "")
}
