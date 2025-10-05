package net.kigawa.keruta.sdk.client.model.request

import net.kigawa.keruta.sdk.client.model.err.Res
import net.kigawa.keruta.sdk.client.model.err.TypeChangeErr
import java.util.*

interface ApiResponse {
    val id: UUID
    fun asAuthResponse(): Res<ApiAuthResponse, TypeChangeErr>
}
