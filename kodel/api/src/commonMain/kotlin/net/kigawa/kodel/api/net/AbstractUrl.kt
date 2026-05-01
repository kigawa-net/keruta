package net.kigawa.kodel.api.net

abstract class AbstractUrl(
    val schemeHost: String,
    override val path: String,
    val queryAnker: String,
): UrlBase {
    abstract fun copy(schemeHost: String? = null, path: String? = null, queryAnker: String? = null): Url
    override fun setPath(path: String): Url {
        return copy(schemeHost, path, queryAnker)
    }

    override fun toStrUrl(): String = "$schemeHost/$path$queryAnker"

    override fun toString(): String = toStrUrl()

    companion object {
        fun parse(
            strUrl: String,
            init: (
                schemeHost: String,
                path: String,
                queryAnker: String,
            ) -> Url,
        ): Url {
            val schemeHostMatch = Regex("^[a-zA-Z]+://.+/]").matchAt(strUrl, 0)
                ?: throw IllegalArgumentException("invalid url: $strUrl")
            val pathQueryAnker = strUrl.removePrefix(schemeHostMatch.value)
            val pathMatch = Regex("^/[a-zA-Z/]").matchAt(pathQueryAnker, 0)
                ?: throw IllegalArgumentException("invalid pathQueryAnker: $pathQueryAnker")
            val queryAnker = pathQueryAnker.removePrefix(pathMatch.value)
            return init(
                schemeHostMatch.value.removeSuffix("/"),
                pathMatch.value,
                queryAnker
            )
        }
    }
}
