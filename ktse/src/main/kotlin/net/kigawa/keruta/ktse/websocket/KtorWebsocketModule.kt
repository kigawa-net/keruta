package net.kigawa.keruta.ktse.websocket

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.ktcp.base.auth.jwks.JwksProvider
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.base.auth.key.Auth0AlgorithmInitializer
import net.kigawa.keruta.ktcp.base.auth.key.JavaPrivateKeyInitializer
import net.kigawa.keruta.ktcp.base.auth.oidc.OidcConfigProvider
import net.kigawa.keruta.ktcp.base.http.HttpClient
import net.kigawa.keruta.ktcp.model.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.session.KtcpSession
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.keruta.ktse.KerutaTaskServer
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.ReceiveUnknownArg
import net.kigawa.keruta.ktse.WebsocketConnection
import net.kigawa.keruta.ktse.auth.Auth0AuthTokenDecoder
import net.kigawa.keruta.ktse.auth.KtseJwtVerifier
import net.kigawa.keruta.ktse.persist.ExposedPersisterSession
import net.kigawa.keruta.ktse.persist.ProviderDeleteHandler
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.keruta.ktse.websocket.entrypoint.ReceiveProviderCompleteEntrypoint
import net.kigawa.keruta.ktse.websocket.entrypoint.ReceiveProviderDeleteEntrypoint
import net.kigawa.keruta.ktse.websocket.entrypoint.ReceiveProviderIssueTokenEntrypoint
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.convertErr
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.Duration.Companion.seconds

class KtorWebsocketModule(application: Application, val server: KerutaTaskServer) {
    val ktseConfig = KtseConfig(application.environment)
    val httpClient = HttpClient()
    val serializer = JsonKerutaSerializer()
    val logger = getKogger()
    val dbPersister = DbPersister(ktseConfig)
    val jwksProvider = JwksProvider()
    val oidcConfigProvider = OidcConfigProvider(httpClient)
    private val javaPrivateKeyInitializer = JavaPrivateKeyInitializer()
    private val auth0AlgorithmInitializer = Auth0AlgorithmInitializer()
    val auth0JwtVerifier = Auth0JwtVerifier(
        oidcConfigProvider, jwksProvider, auth0AlgorithmInitializer, javaPrivateKeyInitializer
    )
    val jwtVerifier = KtseJwtVerifier(
        auth0JwtVerifier = auth0JwtVerifier,
        jwtSecret = ktseConfig.jwtSecret,
        auth0AlgorithmInitializer = auth0AlgorithmInitializer,
        javaPrivateKeyInitializer = javaPrivateKeyInitializer,
    )
    val authTokenDecoder = Auth0AuthTokenDecoder(auth0JwtVerifier)
    val providerDeleteHandler = ProviderDeleteHandler()
    val ktcpServer = KtcpServer(
        ReceiveProviderIssueTokenEntrypoint(),
        ReceiveProviderCompleteEntrypoint(),
        ReceiveProviderDeleteEntrypoint(providerDeleteHandler),
        jwtVerifier,serializer
    )

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
            WebsocketConnection(this@webSocket, ktcpServer),
            ExposedPersisterSession(dbPersister), authTokenDecoder, dbPersister.verifyTablesPersister,
            ktseConfig.defaultUserIdp, ktseConfig.defaultProviderIdp,
            ktseConfig.jwtSecret
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
                GenericErrMsg(errorCode = res.err.code, errorMessage = res.err.message ?: "empty message"),
                ServerCtx(session, serializer, ktcpServer)
            )
        }

        is Res.Ok<*, *> -> {
        }
    }

    private suspend fun receive(frame: Frame, ctx: ServerCtx): Res<Unit, KtcpErr> {
        val res = ReceiveUnknownArg.fromFrame(frame, ctx)
        if (res is Res.Err) {
            logger.error("Failed to decode frame", res.err)
            ctx.session.recordErr()
            return res.convert()
        }
        val arg = (res as Res.Ok).value

        return ktcpServer.ktcpServerEntrypoints.access(arg, ctx)?.execute()
            ?.convertErr { ResponseErr("", it) } ?: Res.Err(
            EntrypointNotFoundErr("entrypoint not found:", null)
        )
    }
}
