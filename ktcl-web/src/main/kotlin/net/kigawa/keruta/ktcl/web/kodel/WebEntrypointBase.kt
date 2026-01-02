package net.kigawa.keruta.ktcl.web.kodel

interface WebEntrypointBase {
    val path: List<String>
    val strPath get() = path.joinToString(separator = "/", prefix = "/")
}
