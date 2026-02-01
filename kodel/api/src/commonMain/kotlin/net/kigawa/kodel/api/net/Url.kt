package net.kigawa.kodel.api.net

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Url: UrlBase {
    override val path: String
    override fun setPath(path: String): Url

    companion object {
        fun parse(strUrl: String): Url
    }
}
