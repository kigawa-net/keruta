package net.kigawa.kodel.api.net

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class Url(
    schemeHost: String,
    path: String,
    queryAnker: String,
): AbstractUrl(
    schemeHost, path, queryAnker
), UrlBase {
    override fun copy(
        schemeHost: String?, path: String?, queryAnker: String?,
    ): Url {
        return Url(
            schemeHost ?: this.schemeHost,
            path ?: this.path,
            queryAnker ?: this.queryAnker
        )
    }

    actual companion object {
        actual fun parse(strUrl: String): Url = parse(strUrl, ::Url)
    }
}
