package net.kigawa.keruta.ktcp.domain.provider.idp_added

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientProviderIdpAddedEntrypoint<C> : Entrypoint<ClientProviderIdpAddedMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ClientMsgType.PROVIDER_IDP_ADDED.str, emptyList(), "プロバイダーIdP追加完了処理"
        )
}
