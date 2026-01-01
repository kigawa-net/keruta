@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package net.kigawa.kodel.api.log

import java.util.logging.Logger

actual typealias Kogger = Logger

actual var Kogger.logLevel: LogLevel
    get() = LogLevel.fromJvm(level)
    set(value) {
        level = value.primary
    }

actual fun Kogger.removeAllHandlers() {
    handlers.forEach { removeHandler(it) }
}