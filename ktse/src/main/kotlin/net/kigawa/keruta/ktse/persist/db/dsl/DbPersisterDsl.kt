package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.UserIssuer
import net.kigawa.keruta.ktcp.model.err.KtcpErr
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
import org.jetbrains.exposed.sql.*

class DbPersisterDsl(val transaction: Transaction) {
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
        val user = UserTable.insert {
        }.resultedValues
            ?.single()
            ?.let { ExposedPersistedUser(it) }
            ?: return Res.Err(NoSingleRecordErr("", null))
        val provider = ProviderTable.insert {
            it[ProviderTable.userId] = user.id
            it[ProviderTable.issuer] = providerIssuer.toStrUrl()
            it[ProviderTable.audience] = providerAudience
            it[ProviderTable.name] = providerName
            it[ProviderTable.setting] = ""
        }.resultedValues
            ?.single()
            ?.let { ExposedPersistedProvider(it) }
            ?: return Res.Err(NoSingleRecordErr("", null))
        val userIdp = UserIdpTable.insert {
            it[UserIdpTable.userId] = user.id
            it[UserIdpTable.providerId] = provider.id
            it[UserIdpTable.issuer] = userIssuer.toStrUrl()
            it[UserIdpTable.subject] = userSubject
            it[UserIdpTable.audience] = userAudience
        }.resultedValues
            ?.single()
            ?.let { ExposedPersistedUserIdp(it) }
            ?: return Res.Err(NoSingleRecordErr("", null))
        return Res.Ok(PersistedVerifyTables(user, userIdp, provider))
    }


    fun insertProviderForUser(
        user: PersistedUser,
        providerIssuer: Url,
        providerAudience: String,
        providerName: String,
        userSubject: String,
        userIssuer: UserIssuer,
    ): Res<ExposedPersistedProvider, KtcpErr> {
        val provider = ProviderTable.insert {
            it[ProviderTable.userId] = user.id
            it[ProviderTable.issuer] = providerIssuer.toStrUrl()
            it[ProviderTable.audience] = providerAudience
            it[ProviderTable.name] = providerName
            it[ProviderTable.setting] = ""
        }.resultedValues
            ?.single()
            ?.let { ExposedPersistedProvider(it) }
            ?: return Res.Err(NoSingleRecordErr("", null))
        UserIdpTable.insert {
            it[UserIdpTable.userId] = user.id
            it[UserIdpTable.providerId] = provider.id
            it[UserIdpTable.issuer] = userIssuer.toStrUrl()
            it[UserIdpTable.subject] = userSubject
            it[UserIdpTable.audience] = providerAudience
        }
        return Res.Ok(provider)
    }

    fun findUserTables(issuer: Url, subject: String): Res<Pair<PersistedUser, PersistedUserIdp>, KtcpErr> =
        UserTable.join(UserIdpTable, JoinType.INNER)
            .join(
                ProviderTable, JoinType.INNER, additionalConstraint = {
                    UserIdpTable.providerId eq ProviderTable.id and (UserTable.id eq ProviderTable.userId)
                }
            ).select(UserTable.fields + UserIdpTable.fields).where {
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
