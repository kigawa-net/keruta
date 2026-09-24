package net.kigawa.keruta.kicp.domain.repo

import net.kigawa.keruta.kicp.domain.token.RegisterToken

fun interface RegisterTokenGenerator {
    fun generate(): RegisterToken
}
