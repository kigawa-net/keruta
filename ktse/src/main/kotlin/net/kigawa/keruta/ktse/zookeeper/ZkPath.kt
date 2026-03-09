package net.kigawa.keruta.ktse.zookeeper

data class ZkPath(
    val listPath: List<String>,
) {
    constructor(vararg path: String): this(
        path.flatMap { it.split("/") }
            .filter { it.isNotEmpty() }
    )

    init {
        listPath.forEach {
            require(it.isNotEmpty())
            require(!it.contains("/"))
        }
    }

    operator fun plus(path: ZkPath): ZkPath {
        return ZkPath(listPath + path.listPath)
    }
    operator fun plus(path: String): ZkPath {
        return ZkPath(listPath + path.split("/").filter { it.isNotEmpty() })
    }

    val str get() = listPath.joinToString("/", prefix = "/")
}
