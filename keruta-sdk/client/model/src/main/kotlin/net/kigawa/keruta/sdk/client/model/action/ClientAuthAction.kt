package net.kigawa.keruta.sdk.client.model.action

import net.kigawa.keruta.sdk.client.model.auth.ApiRequestAuth
import net.kigawa.keruta.sdk.client.model.auth.AuthErr
import net.kigawa.keruta.sdk.client.model.err.Res
import net.kigawa.keruta.sdk.client.model.request.ApiRequestor
import net.kigawa.keruta.sdk.client.model.request.ApiResponse

interface ClientAuthAction {
    val apiRequestor: ApiRequestor
    suspend fun authenticate(): Res<ApiResponse, AuthErr> {
        return when (val res = apiRequestor.send(ApiRequestAuth())) {
            is Res.Success -> Res.Success(res.value)
            is Res.Error -> Res.Error(AuthErr(res.error))
        }

    }
}
