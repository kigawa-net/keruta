package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.domain.UserIssuer
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import net.kigawa.keruta.ktse.persist.db.table.UserTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedProvider
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUser
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedUserIdp
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

@Suppress("DEPRECATION")
class DbPersisterDsl(private val transaction: JdbcTransaction) {
    fun findVerifyTables(
        userIssuer: Url, userSubject: String, providerIssuer: Url,
    ): Res<PersistedVerifyTables, KtcpErr>? = UserTable.join(UserIdpTable, JoinType.INNER)
        .join(
            ProviderTable, JoinType.INNER, additionalConstraint = {
                UserIdpTable.providerId eq ProviderTable.id and (UserTable.id eq ProviderTable.userId)
            }
        ).selectAll().where {
            (UserIdpTable.issuer eq userIssuer.toStrUrl()) and (UserIdpTable.subject eq userSubject) and
                (ProviderTable.issuer eq providerIssuer.toStrUrl())
        }.distinct().singleOrNull()
        ?.let {
            val idp = ExposedPersistedUserIdp(it)
            PersistedVerifyTables(
                ExposedPersistedUser(it),
                idp,
                ExposedPersistedProvider(it)
            )
        }?.let { Res.Ok(it) }

    fun insertVerifyTables(
        userIssuer: Url, userAudience: String, userSubject: String, providerIssuer: Url, providerAudience: String,
        providerName: String,
    ): Res<PersistedVerifyTables, KtcpErr> {
        // Stub - return error for now
        return Res.Err(NoSingleRecordErr("insertVerifyTables not implemented", null))
    }

    fun insertProviderForUser(
        user: PersistedUser,
        providerIssuer: Url,
        providerAudience: String,
        providerName: String,
        userSubject: String,
        userIssuer: UserIssuer,
    ): Res<ExposedPersistedProvider, KtcpErr> {
        return Res.Err(NoSingleRecordErr("", null))
    }

    fun findUserTables(issuer: Url, subject: String): Res<Pair<PersistedUser, PersistedUserIdp>, KtcpErr> =
        UserTable.join(UserIdpTable, JoinType.INNER)
            .join(
                ProviderTable, JoinType.INNER, additionalConstraint = {
                    UserIdpTable.providerId eq ProviderTable.id and (UserTable.id eq ProviderTable.userId)
                }
            ).select(UserTable.columns + UserIdpTable.columns).where {
                (UserIdpTable.issuer eq issuer.toStrUrl()) and (UserIdpTable.subject eq subject)
            }.distinct().singleOrNull()
            ?.let {
                val idp = ExposedPersistedUserIdp(it)
                Pair(
                    ExposedPersistedUser(it),
                    idp
                )
            }?.let { Res.Ok(it) }
            ?: Res.Err(NoSingleRecordErr("", null))

    val task by lazy { DbTaskPersisterDsl(transaction) }
    val user by lazy { DbUserPersisterDsl() }
    val provider by lazy { DbProviderPersisterDsl(transaction) }
    val queue by lazy { DbQueuePersisterDsl(transaction) }
}