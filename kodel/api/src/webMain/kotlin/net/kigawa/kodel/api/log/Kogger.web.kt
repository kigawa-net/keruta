package net.kigawa.kodel.api.log

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kogger {
    var logLevel: LogLevel = LogLevel.INFO
    fun removeAllHandlers() {
    }

    actual fun fine(msg: String) {
        println(msg)
    }

    actual fun warning(msg: String) {
        println(msg)
    }

    actual fun severe(msg: String) {
        println(msg)
    }
}

actual var Kogger.logLevel: LogLevel
    get() = logLevel
    set(value) {
        logLevel = value
    }

actual fun Kogger.removeAllHandlers() {
    removeAllHandlers()
}