package net.kigawa.keruta.sdk.client.model.packet

import net.kigawa.keruta.sdk.client.model.err.Res
import net.kigawa.keruta.sdk.client.model.err.TypeChangeErr
import net.kigawa.keruta.sdk.client.model.event.ApiEvent
import net.kigawa.keruta.sdk.client.model.request.ApiResponse

interface ServerPacket {
    val packetType: ServerPacketType
    fun asEvent(): Res<ApiEvent, TypeChangeErr>
    fun asResponse(): Res<ApiResponse, TypeChangeErr>
}
