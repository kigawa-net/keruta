package net.kigawa.keruta.ktse.persist.db.dsl

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktse.persist.db.table.ProviderAddTokenTable
import net.kigawa.keruta.ktse.persist.model.ProviderAddTokenData
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

class DbProviderAddTokenDsl(val transaction: Transaction) {

    fun insert(userId: Long, name: String, issuer: String, audience: String): String {
        val token = UUID.randomUUID().toString()
        val expiresAt = Clock.System.now().plus(10.minutes).toLocalDateTime(TimeZone.UTC)
        transaction.run {
            ProviderAddTokenTable.insert {
                it[ProviderAddTokenTable.token] = token
                it[ProviderAddTokenTable.userId] = userId
                it[ProviderAddTokenTable.name] = name
                it[ProviderAddTokenTable.issuer] = issuer
                it[ProviderAddTokenTable.audience] = audience
                it[ProviderAddTokenTable.expiresAt] = expiresAt
            }
        }
        return token
    }

    fun findAndDelete(token: String): Res<ProviderAddTokenData, KtcpErr>? {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val row = transaction.run {
            ProviderAddTokenTable.selectAll()
                .where { ProviderAddTokenTable.token eq token }
                .singleOrNull()
        } ?: return null

        if (row[ProviderAddTokenTable.expiresAt] < now) return null

        transaction.run {
            ProviderAddTokenTable.deleteWhere { ProviderAddTokenTable.token eq token }
        }
        return Res.Ok(
            ProviderAddTokenData(
                userId = row[ProviderAddTokenTable.userId],
                name = row[ProviderAddTokenTable.name],
                issuer = row[ProviderAddTokenTable.issuer],
                audience = row[ProviderAddTokenTable.audience],
            )
        )
    }
}
