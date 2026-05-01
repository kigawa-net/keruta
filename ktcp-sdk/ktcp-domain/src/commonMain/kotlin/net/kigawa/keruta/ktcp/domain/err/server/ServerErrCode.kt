package net.kigawa.keruta.ktcp.domain.err.server

enum class ServerErrCode {
    INVALID_TYPE_DECODE_FRAME,
    DESERIALIZE_DECODE_FRAME,
    UNEXPECTED,
    RESPONSE_ERR,
    UNAUTHENTICATED,
    MULTIPLE_RECORD,
    NO_SINGLE_RECORD
}
