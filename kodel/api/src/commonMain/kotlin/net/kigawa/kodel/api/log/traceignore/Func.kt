package net.kigawa.kodel.api.log.traceignore

import net.kigawa.kodel.api.log.Kogger


fun Kogger.debug(msg: String) = fine(msg)
fun Kogger.debug(msg: () -> String) = fine(msg)


fun Kogger.warn(msg: String) = warning(msg)
fun Kogger.warn(msg: () -> String) = warning(msg)


fun Kogger.error(msg: String, e: Throwable? = null) {
    severe(msg)
    e?.printStackTrace()
}

fun Kogger.error(msg: () -> String, e: Throwable? = null) {
    severe(msg)
    e?.printStackTrace()
}

expect fun Kogger.fine(msg: () -> String)
expect fun Kogger.warning(msg: () -> String)
expect fun Kogger.severe(msg: () -> String)
