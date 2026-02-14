package net.kigawa.keruta.ktse.persist

import com.auth0.jwt.JWT
import io.ktor.client.call.*
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.model.provider.idp_added.ClientProviderIdpAddedMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktse.auth.keruta.KerutaJsonProvider
import net.kigawa.keruta.ktse.auth.keruta.TokenExchangeResponse
import net.kigawa.keruta.ktse.http.HttpClient
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.net.Url

class ProviderCompleteHandler(
    val dbPersister: DbPersister,
    val kerutaJsonProvider: KerutaJsonProvider,
    val httpClient: HttpClient,
) {
    private val logger = getKogger()

    suspend fun handle(msg: ServerProviderCompleteMsg, ctx: ServerCtx): Res<Unit, KtcpErr> {
        val authed = ctx.session.authenticated()
            ?: return Res.Err(UnauthenticatedErr("", null))
        val authedSession = authed.persisterSession as? ExposedAuthedPersisterSession
            ?: return Res.Err(UnauthenticatedErr("invalid session type", null))

        val tokenResult = dbPersister.execTransaction { dsl ->
            dsl.providerAddToken.findAndDelete(msg.token)
        } ?: return Res.Err(VerifyFailErr("provider add token not found or expired", null))

        val data = when (tokenResult) {
            is Res.Err -> return tokenResult.convert()
            is Res.Ok -> tokenResult.value
        }

        val issuerUrl = Url.parse(data.issuer)
        val kerutaJson = when (val res = kerutaJsonProvider.get(issuerUrl)) {
            is Res.Err -> return res.convert()
            is Res.Ok -> res.value
        }

        logger.debug { "Exchanging code at ${kerutaJson.tokenEndpoint}" }
        val tokenResponse = try {
            val response = httpClient.postForm(
                kerutaJson.tokenEndpoint,
                mapOf(
                    "grant_type" to "authorization_code",
                    "code" to msg.code,
                    "redirect_uri" to msg.redirectUri,
                    "client_id" to data.audience,
                )
            )
            @Suppress("DEPRECATION")
            response.body<TokenExchangeResponse>()
        } catch (e: Exception) {
            return Res.Err(VerifyFailErr("code exchange failed", e))
        }

        val idToken = tokenResponse.idToken
            ?: return Res.Err(VerifyFailErr("no id_token in response", null))

        val subject = try {
            JWT.decode(idToken).subject
        } catch (e: Exception) {
            return Res.Err(VerifyFailErr("failed to decode id_token", e))
        }

        dbPersister.execTransaction { dsl ->
            dsl.insertProviderForUser(
                user = authedSession.user,
                providerIssuer = issuerUrl,
                providerAudience = data.audience,
                providerName = data.name,
                userSubject = subject,
            )
        }

        ctx.connection.send(
            ctx.serializer.serialize(ClientProviderIdpAddedMsg())
        )
        return Res.Ok(Unit)
    }
}
