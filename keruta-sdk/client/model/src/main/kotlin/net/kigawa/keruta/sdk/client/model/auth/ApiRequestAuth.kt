package net.kigawa.keruta.sdk.client.model.auth

import net.kigawa.keruta.sdk.client.model.request.ApiRequest
import java.util.UUID

class ApiRequestAuth: ApiRequest {
    override val id: UUID = UUID.randomUUID()
}
