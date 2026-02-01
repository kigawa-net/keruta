package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import net.kigawa.keruta.ktse.persist.db.table.UserTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedProvider
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUser
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUserIdp
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

class DbPersisterDsl(val transaction: Transaction) {
    fun findVerifyTables(
        userIssuer: String, userSubject: String, providerIssuer: String,
    ): Res<PersistedVerifyTables, KtcpErr>? = UserTable.join(UserIdpTable, JoinType.INNER)
        .join(
            ProviderTable, JoinType.INNER, additionalConstraint = {
                UserIdpTable.providerId eq ProviderTable.id and (UserTable.id eq ProviderTable.userId)
            }
        ).selectAll().where {
            (UserIdpTable.issuer eq userIssuer) and (UserIdpTable.subject eq userSubject) and
                (ProviderTable.issuer eq providerIssuer)
        }.distinct().singleOrNull()
        ?.let {
            val idp = ExposedPersistedUserIdp(it)
            PersistedVerifyTables(
                ExposedPersistedUser(it, idp),
                idp,
                ExposedPersistedProvider(it)
            )
        }?.let { Res.Ok(it) }


    val task by lazy { DbTaskPersisterDsl(transaction) }
    val user by lazy { DbUserPersisterDsl(transaction) }
    val provider by lazy { DbProviderPersisterDsl(transaction) }
    val queue by lazy { DbQueuePersisterDsl(transaction) }
}
