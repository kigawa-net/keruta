package net.kigawa.keruta.ktcl.k8s.persist.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * ユーザーClaudeCode設定テーブル
 */
object UserClaudeConfigTable : Table("user_claude_config") {
    val id = long("id").autoIncrement()
    val userId = reference("user_id", UserTable.id)
    val anthropicApiKey = text("anthropic_api_key")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId)
    }
}
