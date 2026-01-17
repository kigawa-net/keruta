package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktse.err.MultipleRecordErr
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import net.kigawa.keruta.ktse.persist.db.table.UserTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUser
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUserIdp
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class DbUserPersisterDsl(
    val transaction: Transaction,
) {
    fun getUserIdpOrNull(subject: String, issuer: String): Res<PersistedUserIdp, KtcpErr>? = transaction.run {
        val res = UserIdpTable.selectAll().where {
            (UserIdpTable.subject eq subject) and (UserIdpTable.issuer eq issuer)
        }
        if (res.empty()) return@run null
        res.singleOrNull()?.let { return@run Res.Ok(ExposedPersistedUserIdp(it)) }
        return@run Res.Err(MultipleRecordErr("", null))
    }

    fun getUser(
        userIdp: PersistedUserIdp,
    ): Res<PersistedUser, KtcpErr> = transaction.run {
        val res = UserTable.selectAll().where {
            UserTable.id eq userIdp.userId
        }
        res.singleOrNull()?.let {
            return@run Res.Ok(ExposedPersistedUser(it, userIdp))
        }
        return@run Res.Err(NoSingleRecordErr("", null))
    }

    fun createUserAndIdp(
        idp: IdpConfig, verifiedToken: VerifiedToken,
    ): Res<PersistedUser, KtcpErr> = transaction.run {
        val user = UserTable.insert {}.resultedValues
            ?.singleOrNull() ?: return@run Res.Err(NoSingleRecordErr("", null))
        val idp = UserIdpTable.insert {
            it[UserIdpTable.userId] = user[UserTable.id]
            it[UserIdpTable.subject] = verifiedToken.subject
            it[UserIdpTable.issuer] = idp.issuer
            it[UserIdpTable.audience] = idp.audience
        }.resultedValues?.singleOrNull() ?: return@run Res.Err(NoSingleRecordErr("", null))
        Res.Ok(
            ExposedPersistedUser(user, ExposedPersistedUserIdp(idp))
        )
    }
}
