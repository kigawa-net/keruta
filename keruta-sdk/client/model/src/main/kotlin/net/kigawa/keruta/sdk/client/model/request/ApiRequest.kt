package net.kigawa.keruta.sdk.client.model.request

import net.kigawa.keruta.sdk.client.model.client.ApiSendData
import java.util.UUID

interface ApiRequest: ApiSendData {
    val id: UUID
}
