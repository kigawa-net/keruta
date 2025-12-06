package net.kigawa.keruta.sdk.client.model.client

import net.kigawa.keruta.sdk.client.model.packet.ServerPacket

interface ApiReader {
    suspend fun read(): ServerPacket?
}
