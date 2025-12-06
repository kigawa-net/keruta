package net.kigawa.keruta.sdk.client.model.request

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import net.kigawa.keruta.sdk.client.model.Config
import net.kigawa.keruta.sdk.client.model.client.ApiSender
import net.kigawa.keruta.sdk.client.model.err.Res

class ApiRequestor(
    val sender: ApiSender,
    val responseNotifier: ApiResponseNotifier,
    val config: Config,
) {
    suspend fun send(request: ApiRequest): Res<ApiResponse, RequestErr> {
        val deff = CoroutineScope(currentCoroutineContext()).async {
            responseNotifier.receive(request.id, config.timeout)
        }
        sender.send(request)
        return when (val res = deff.await()) {
            is Res.Success -> Res.Success(res.value)
            is Res.Error -> Res.Error(RequestErr(request, res.error))
        }
    }
}
