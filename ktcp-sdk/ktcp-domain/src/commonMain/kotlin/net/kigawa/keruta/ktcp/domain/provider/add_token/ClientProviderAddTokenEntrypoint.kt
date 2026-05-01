package net.kigawa.keruta.ktcp.domain.provider.add_token

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientProviderAddTokenEntrypoint<C> : Entrypoint<ClientProviderAddTokenMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ClientMsgType.PROVIDER_ADD_TOKEN_ISSUED.str, emptyList(), "プロバイダー追加トークン処理"
        )
}
