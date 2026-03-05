package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.serialize.JsonString

data class WebsocketPacket(
    val bodyText: JsonString,
)
