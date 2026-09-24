package net.kigawa.keruta.ktse.kicp

/**
 * 登録トークン検証リクエスト
 */
data class VerifyRegisterRequest(
    val registerId: String,
    val registerToken: String
)
