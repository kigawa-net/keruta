package net.kigawa.keruta.sdk.client.model.action

import net.kigawa.keruta.sdk.client.model.err.ErrNotifier
import net.kigawa.keruta.sdk.client.model.packet.ServerPacket
import net.kigawa.keruta.sdk.client.model.packet.ServerPacketType
import net.kigawa.keruta.sdk.client.model.request.ApiResponseNotifier

interface ApiPacketReceiveAction {
    val receiveEventAction: ApiReceiveEventAction
    val responseNotifier: ApiResponseNotifier
    val errNotifier: ErrNotifier
    suspend fun receive(packet: ServerPacket) {
        when (packet.packetType) {
            ServerPacketType.EVENT -> errNotifier.handleToNull { packet.asEvent() }
                ?.let { receiveEventAction.receive(it) }

            ServerPacketType.RESPONSE -> errNotifier.handleToNull { packet.asResponse() }
                ?.let { responseNotifier.notify(it) }
        }
    }
}
