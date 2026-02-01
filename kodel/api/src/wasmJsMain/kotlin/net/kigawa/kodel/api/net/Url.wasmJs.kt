package net.kigawa.kodel.api.net

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class Url(
    val url: String,
): UrlBase {
    actual override val path: String
        get() = TODO("Not yet implemented")

    actual override fun setPath(path: String): Url {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun parse(strUrl: String): Url {
            val match = Regex("^[a-zA-Z]+://.+/]").matchAt(strUrl, 0)
                ?: throw IllegalArgumentException("invalid url")
            return Url(strUrl)
        }
    }
}
