package net.kigawa.keruta.ktcp.domain

import net.kigawa.keruta.ktcp.domain.serialize.JsonString

data class WebsocketPacket(
    val bodyText: JsonString,
)
