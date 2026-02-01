package net.kigawa.kodel.api.net

interface UrlBase {
    val path: String
    fun setPath(path: String): Url

    fun plusPath(path: String): Url {
        val base = path.removeSuffix("/")
        val path = path.trimStart('/')
        return setPath("$base/$path")
    }
}
