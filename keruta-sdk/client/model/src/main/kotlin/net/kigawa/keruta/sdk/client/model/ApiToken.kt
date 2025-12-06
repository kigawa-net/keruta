package net.kigawa.keruta.sdk.client.model

class ApiToken(
    val strToken: String
) {
    init {
        require(strToken.isNotEmpty()) {"token is empty"}
    }
}
