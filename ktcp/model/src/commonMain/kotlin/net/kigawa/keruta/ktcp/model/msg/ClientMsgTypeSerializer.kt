package net.kigawa.keruta.ktcp.model.msg

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ClientMsgTypeSerializer: KSerializer<ClientMsgType> {
    override val descriptor: SerialDescriptor
        get() = String.serializer().descriptor

    override fun serialize(
        encoder: Encoder, value: ClientMsgType,
    ) {
        encoder.encodeString(value.str)
    }

    override fun deserialize(
        decoder: Decoder,
    ): ClientMsgType {
        return ClientMsgType.fromString(decoder.decodeString())
    }
}
