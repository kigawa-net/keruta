package net.kigawa.keruta.kicp.usecase.login

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.identity.IdentityId
import net.kigawa.keruta.kicp.domain.repo.JwksRepository
import net.kigawa.keruta.kicp.domain.repo.JwtVerifier
import net.kigawa.kodel.api.err.Res

interface LoginUseCase {
    suspend fun login(input: LoginInput): Res<IdentityId, KicpErr>
}

class LoginUseCaseImpl(
    private val jwksRepository: JwksRepository,
    private val jwtVerifier: JwtVerifier,
) : LoginUseCase {
    override suspend fun login(input: LoginInput): Res<IdentityId, KicpErr> {
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

        return Res.Ok(IdentityId("${oidcClaims.issuer}:${oidcClaims.subject}"))
    }
}
