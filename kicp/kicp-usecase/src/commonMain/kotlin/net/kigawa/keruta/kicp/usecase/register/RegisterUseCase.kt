package net.kigawa.keruta.kicp.usecase.register

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.identity.IdentityId
import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.repo.JwksRepository
import net.kigawa.keruta.kicp.domain.repo.JwtVerifier
import net.kigawa.keruta.kicp.domain.repo.PeerServerClient
import net.kigawa.kodel.api.err.Res

interface RegisterUseCase {
    suspend fun register(input: RegisterInput): Res<IdentityId, KicpErr>
}

/** idServerB 側で動作する登録ユースケース */
class RegisterUseCaseImpl(
    private val jwksRepository: JwksRepository,
    private val jwtVerifier: JwtVerifier,
    private val peerServerClient: PeerServerClient,
) : RegisterUseCase {
    override suspend fun register(input: RegisterInput): Res<IdentityId, KicpErr> {
        val providerJwks = when (val r = jwksRepository.get(input.providerJwksUrl)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> r.value
        }
        when (val r = jwtVerifier.verify(input.providerToken.value, providerJwks)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> Unit
        }

        val oidcJwks = when (val r = jwksRepository.get(input.oidcJwksUrl)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> r.value
        }
        val oidcClaims = when (val r = jwtVerifier.verify(input.oidcToken.value, oidcJwks)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> r.value
        }

        val registerId = RegisterId("${oidcClaims.issuer}:${oidcClaims.subject}")
        when (val r = peerServerClient.verifyRegister(registerId, input.registerToken)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> Unit
        }

        return Res.Ok(IdentityId("${oidcClaims.issuer}:${oidcClaims.subject}"))
    }
}
