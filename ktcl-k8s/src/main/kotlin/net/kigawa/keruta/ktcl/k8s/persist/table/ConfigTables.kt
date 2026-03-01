package net.kigawa.keruta.ktcl.k8s.persist.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Kubernetes設定テーブル
 */
object K8sConfigTable : Table("k8s_config") {
    val id = long("id").autoIncrement()
    val configKey = varchar("config_key", 255).uniqueIndex()
    val configValue = text("config_value")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

/**
 * キュー設定テーブル
 */
object QueueConfigTable : Table("queue_config") {
    val id = long("id").autoIncrement()
    val configKey = varchar("config_key", 255).uniqueIndex()
    val configValue = text("config_value")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}
