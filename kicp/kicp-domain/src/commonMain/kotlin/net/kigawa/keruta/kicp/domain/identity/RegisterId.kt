package net.kigawa.keruta.kicp.domain.identity

import kotlinx.serialization.Serializable

/** idServerB が idServerA へ送る、登録クライアントの識別子 */
@Serializable
data class RegisterId(val value: String)