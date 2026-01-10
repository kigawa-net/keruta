package net.kigawa.keruta.ktcp.server.auth.success

import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class AuthSuccessSendEntrypoint: AuthSuccessEntrypoint<ServerCtx> {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.server.auth.success.AuthSuccessSendEntrypoint")
    override fun access(
        input: ClientAuthSuccessArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input.authSuccessMsg)
                    .also { logger.debug("send auth success message: $it") }
            )
            Res.Ok(Unit)
        }
    }
}
