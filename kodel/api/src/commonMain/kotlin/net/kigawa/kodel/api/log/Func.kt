@file:Suppress("unused")

package net.kigawa.kodel.api.log

import kotlin.reflect.KClass

fun <T: Any> KClass<T>.getKogger(): Kogger {
    return LoggerFactory.get(this)
}

fun <T: Any> T.getKogger(): Kogger {
    return LoggerFactory.get(this::class)
}
