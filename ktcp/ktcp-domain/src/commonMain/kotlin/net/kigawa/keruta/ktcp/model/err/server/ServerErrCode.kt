package net.kigawa.keruta.ktcp.model.err.server

enum class ServerErrCode {
    INVALID_TYPE_DECODE_FRAME,
    DESERIALIZE_DECODE_FRAME,
    UNEXPECTED,
    RESPONSE_ERR,
    UNAUTHENTICATED,
    BACKEND,
    UNKNOWN_ISSUER,
    MULTIPLE_RECORD,
    NO_SINGLE_RECORD
}
