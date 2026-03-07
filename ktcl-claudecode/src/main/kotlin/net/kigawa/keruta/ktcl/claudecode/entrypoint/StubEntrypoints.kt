package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.deleted.ClientProviderDeletedEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.deleted.ClientProviderDeletedMsg
import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.domain.queue.created.ClientQueueCreatedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.created.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcp.domain.queue.listed.ClientQueueListedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.domain.task.moved.ClientTaskMovedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.moved.ClientTaskMovedMsg
import net.kigawa.keruta.ktcp.domain.task.updated.ClientTaskUpdatedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.updated.ClientTaskUpdatedMsg
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

class ReceiveProviderListedEntrypoint : ClientProviderListedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderListedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

class ReceiveQueueCreatedEntrypoint : ClientQueueCreatedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientQueueCreatedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

class ReceiveQueueListedEntrypoint : ClientQueueListedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientQueueListedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

class ReceiveQueueShowedEntrypoint : ClientQueueShowedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientQueueShowedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

class ReceiveTaskUpdatedEntrypoint : ClientTaskUpdatedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientTaskUpdatedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

class ReceiveTaskMovedEntrypoint : ClientTaskMovedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientTaskMovedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}
