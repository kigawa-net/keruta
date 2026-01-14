package net.kigawa.keruta.ktse.persist.db

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktse.err.MultipleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUserIdp
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

class DbPersisterDSL(val transaction: Transaction) {
    fun getUserIdpOrNull(subject: String, issuer: String): Res<PersistedUserIdp, KtcpErr>? = transaction.run {
        val res = UserIdpTable.selectAll().where {
            (UserIdpTable.subject eq subject) and (UserIdpTable.issuer eq issuer)
        }
        if (res.empty()) return@run null
        res.singleOrNull()?.let { return@run Res.Ok(ExposedPersistedUserIdp(it)) }
        return@run Res.Err(MultipleRecordErr("", null))
    }

    fun getUser(value: VerifiedToken): Res<PersistedUser, KtcpErr> {
        TODO("Not yet implemented")
    }

    fun createUserAndIdp(idp: IdpConfig, verifiedToken: VerifiedToken): Res<PersistedUser, KtcpErr> {
        TODO("Not yet implemented")
    }

    fun getProviderOrNull(issuer: String, id: Long): Res<PersistedProvider, KtcpErr>? {
        TODO("Not yet implemented")
    }

    fun createProvider(idp: IdpConfig, value: VerifiedToken): Res<PersistedProvider, KtcpErr> {
        TODO("Not yet implemented")
    }

}
