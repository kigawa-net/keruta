package net.kigawa.kodel.api.net

import org.w3c.dom.url.URL

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class Url(
    val url: URL,
): UrlBase {
    actual companion object {
        actual fun parse(strUrl: String): Url {
            return Url(URL(strUrl))
        }
    }

    actual override fun setPath(path: String): Url {
        return Url(URL(url.toString()).apply { pathname = path })
    }

    actual override val path: String
        get() = url.pathname

    actual override fun toStrUrl(): String = url.toString()

    actual override fun toString(): String = toStrUrl()
}
