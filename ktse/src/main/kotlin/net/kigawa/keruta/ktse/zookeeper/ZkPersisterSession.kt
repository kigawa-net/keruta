package net.kigawa.keruta.ktse.zookeeper

import net.kigawa.keruta.ktcp.server.auth.Verified
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession
import net.kigawa.keruta.ktse.KtseConfig
import org.apache.zookeeper.data.Id

class ZkPersisterSession(
    val persister: ZkPersister,
): PersisterSession {
    override fun verify(
        verifiedSession: AuthenticatedSession,
    ): AuthenticatedPersisterSession {
        return ZkAuthenticatedPersisterSession(persister, verifiedSession)
    }
}
