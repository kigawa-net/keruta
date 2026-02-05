package net.kigawa.keruta.ktse.websocket

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.ktcp.model.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.session.KtcpSession
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.ReceiveUnknownArg
import net.kigawa.keruta.ktse.WebsocketConnection
import net.kigawa.keruta.ktse.auth.jwks.JwksConfigProvider
import net.kigawa.keruta.ktse.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktse.auth.oidc.OidcConfigProvider
import net.kigawa.keruta.ktse.err.SendGenericErrArg
import net.kigawa.keruta.ktse.persist.KtsePersisterSession
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.keruta.ktse.zookeeper.ZkPersister
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.convertErr
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.Duration.Companion.seconds

class KtorWebsocketModule(application: Application) {
    val ktseConfig = KtseConfig(application.environment)
    val httpClient = net.kigawa.keruta.ktse.http.HttpClient()
    val jwtVerifier = Auth0JwtVerifier()
    val serializer = JsonKerutaSerializer()
    val logger = getKogger()
    val ktcpServer = KtcpServer()
    val zkPersister = ZkPersister(ktseConfig)
    val dbPersister = DbPersister(ktseConfig)
    val jwksConfigProvider = JwksConfigProvider()
    val oidcConfigProvider = OidcConfigProvider(httpClient)

    init {
        application.install(WebSockets.Plugin) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }

    fun websocketModule(routing: Route) = routing.webSocket("/ws/ktcp") {
        logger.debug("WebSocket connection established")
        KtcpSession.startSession(
            WebsocketConnection(this@webSocket),
            KtsePersisterSession(dbPersister, jwtVerifier, jwksConfigProvider, oidcConfigProvider)
        ) { session ->
            consumeWs(session)
        }
    }

    private suspend fun DefaultWebSocketServerSession.consumeWs(session: KtcpSession) {
        logger.debug("WebSocket session started")
        incoming.consumeEach { frame ->
            logger.debug("received frame: $frame, $frame")
            session.updateTimeout()
            receiveAndHandle(frame, session)
        }
    }

    private suspend fun receiveAndHandle(frame: Frame, session: KtcpSession) = when (
        val res = receive(
            frame, ServerCtx(session, serializer, ktcpServer)
        )
    ) {
        is Res.Err<*, KtcpErr> -> {
            logger.error("Failed to receive message", res.err)
            ktcpServer.clientEntrypoints.genericError.access(
                SendGenericErrArg(res.err),
                ServerCtx(session, serializer, ktcpServer)
            )
        }

        is Res.Ok<*, *> -> {
        }
    }

    private suspend fun receive(frame: Frame, ctx: ServerCtx): Res<Unit, KtcpErr> = when (
        val res = ReceiveUnknownArg.fromFrame(frame, ctx)
    ) {
        is Res.Err<*, KtcpErr> -> {
            logger.error("Failed to decode frame", res.err)
            ctx.session.recordErr()
            res.x()
        }

        is Res.Ok<ReceiveUnknownArg, *> -> {
            ktcpServer.ktcpServerEntrypoints.access(res.value, ctx)?.execute()
                ?.convertErr { ResponseErr("", it) } ?: Res.Err(
                EntrypointNotFoundErr("entrypoint not found:", null)
            )
        }
    }
}
