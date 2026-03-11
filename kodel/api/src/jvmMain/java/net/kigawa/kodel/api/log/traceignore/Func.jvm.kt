package net.kigawa.kodel.api.log.traceignore

import net.kigawa.kodel.api.log.Kogger


actual fun Kogger.fine(msg: () -> String) = fine { msg() }


actual fun Kogger.warning(msg: () -> String) = warning { msg() }


actual fun Kogger.severe(msg: () -> String) = severe { msg() }
