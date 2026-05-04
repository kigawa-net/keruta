package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.queue.QueueToCreate
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.QueueTable
import net.kigawa.keruta.ktse.persist.db.table.QueueUserTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedQueue
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

@Suppress("DEPRECATION")
class DbQueuePersisterDsl(
    private val transaction: JdbcTransaction,
) {
    fun createQueue(
        queueToCreate: QueueToCreate, provider: PersistedProvider,
        user: PersistedUser,
    ): Res<PersistedQueue, KtcpErr> =
        transaction.run {
            val queue = QueueTable.insert {
                it[QueueTable.name] = queueToCreate.name
                it[QueueTable.setting] = queueToCreate.setting
                it[QueueTable.providerId] = provider.id
            }.resultedValues?.singleOrNull() ?: return Res.Err(NoSingleRecordErr("", null))
            QueueUserTable.insert {
                it[QueueUserTable.queueId] = queue[QueueTable.id]
                it[QueueUserTable.userId] = user.id
            }
            return Res.Ok(ExposedPersistedQueue(queue))
        }

    fun getAll(user: PersistedUser): Res<List<PersistedQueue>, KtcpErr> = transaction.run {
        QueueTable.join(
            QueueUserTable, joinType = JoinType.INNER,
            additionalConstraint = { QueueUserTable.queueId eq QueueTable.id }
        ).selectAll().where { QueueUserTable.userId eq user.id }
            .map { ExposedPersistedQueue(it) }
            .let { Res.Ok(it) }
    }

    fun findByUserAndId(user: PersistedUser, id: Long): Res<PersistedQueue, KtcpErr> = transaction.run {
        QueueTable.join(
            QueueUserTable, joinType = JoinType.INNER,
        ).selectAll().where {
            QueueUserTable.userId eq user.id and (QueueUserTable.queueId eq id)
        }.singleOrNull()
            ?.let { Res.Ok(ExposedPersistedQueue(it)) }
            ?: Res.Err(NoSingleRecordErr("", null))
    }

    fun updateQueue(user: PersistedUser, queueId: Long, name: String): Res<PersistedQueue, KtcpErr> =
        transaction.run {
            val owns = QueueUserTable.selectAll()
                .where { QueueUserTable.userId eq user.id and (QueueUserTable.queueId eq queueId) }
                .count() > 0
            if (!owns) return Res.Err(NoSingleRecordErr("Queue not found or access denied", null))
            QueueTable.update({ QueueTable.id eq queueId }) {
                it[QueueTable.name] = name
            }
            QueueTable.join(
                QueueUserTable, joinType = JoinType.INNER,
            ).selectAll().where {
                QueueUserTable.userId eq user.id and (QueueUserTable.queueId eq queueId)
            }.singleOrNull()
                ?.let { Res.Ok(ExposedPersistedQueue(it)) }
                ?: Res.Err(NoSingleRecordErr("", null))
        }

    fun deleteQueue(user: PersistedUser, queueId: Long): Res<Unit, KtcpErr> = transaction.run {
        val owns = QueueUserTable.selectAll()
            .where { QueueUserTable.userId eq user.id and (QueueUserTable.queueId eq queueId) }
            .count() > 0
        if (!owns) return Res.Err(NoSingleRecordErr("Queue not found or access denied", null))
        QueueUserTable.deleteWhere { QueueUserTable.queueId eq queueId }
        QueueTable.deleteWhere { QueueTable.id eq queueId }
        Res.Ok(Unit)
    }

}