package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedArg
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.model.task.moved.ClientTaskMovedEntrypoint
import net.kigawa.keruta.ktcp.model.task.moved.ClientTaskMovedMsg
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedEntrypoint
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderListedEntrypoint : ClientProviderListedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientProviderListedArg,
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
