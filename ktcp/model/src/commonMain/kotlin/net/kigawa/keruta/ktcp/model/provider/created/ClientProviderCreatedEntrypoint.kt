package net.kigawa.keruta.ktcp.model.provider.created

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientProviderCreatedEntrypoint<C>:
    Entrypoint<ClientProviderCreatedArg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ClientMsgType.PROVIDER_CREATED.str,
            emptyList(), ""
        )
}
