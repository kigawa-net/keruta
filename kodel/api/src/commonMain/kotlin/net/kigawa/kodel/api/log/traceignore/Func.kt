package net.kigawa.kodel.api.log.traceignore

import net.kigawa.kodel.api.log.Kogger
import net.kigawa.kodel.api.log.fine
import net.kigawa.kodel.api.log.severe
import net.kigawa.kodel.api.log.warning


fun Kogger.debug(msg: String) = fine(msg)
fun Kogger.debug(msg: () -> String) = fine(msg)


fun Kogger.warn(msg: String) =warning(msg)
fun Kogger.warn(msg:()-> String) = warning(msg)


fun Kogger.error(msg: String, e: Throwable? = null) {
    severe(msg)
    e?.printStackTrace()
}
fun Kogger.error(msg: () -> String, e: Throwable? = null) {
    severe(msg)
    e?.printStackTrace()
}
