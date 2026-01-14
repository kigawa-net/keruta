package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp

class ExposedPersistedUser: PersistedUser {
    override val currentIdp: PersistedUserIdp
        get() = TODO("Not yet implemented")
    override val id: Long
        get() = TODO("Not yet implemented")
}
