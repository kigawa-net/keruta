package net.kigawa.keruta.sdk.common.model

interface AuthRequest: Request {
    val token: String
}
