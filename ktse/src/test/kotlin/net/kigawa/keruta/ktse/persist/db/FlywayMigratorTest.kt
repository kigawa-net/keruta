package net.kigawa.keruta.ktse.persist.db

import org.flywaydb.core.api.MigrationState
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * FlywayMigratorの動作を確認するテストクラス
 *
 * テスト用データベースに接続して以下をテストします：
 * - マイグレーションの正常実行
 * - マイグレーション情報の取得
 * - テーブルの作成確認
 * - clean操作
 * - repair操作
 *
 * 環境変数での設定（MySQLテストデータベース）：
 * - TEST_DB_JDBC_URL: データベースJDBC URL (例: jdbc:mysql://localhost:3306/keruta_test)
 * - TEST_DB_USERNAME: データベースユーザー名 (デフォルト: keruta)
 * - TEST_DB_PASSWORD: データベースパスワード (デフォルト: keruta)
 *
 * Dockerコンテナでの起動例：
 * ```bash
 * docker run -d --name mysql-test \
 *   -e MYSQL_ROOT_PASSWORD=root \
 *   -e MYSQL_DATABASE=keruta_test \
 *   -e MYSQL_USER=keruta \
 *   -e MYSQL_PASSWORD=keruta \
 *   -p 3306:3306 \
 *   mysql:8.0
 * ```
 */
class FlywayMigratorTest {

    private lateinit var migrator: FlywayMigrator
    private lateinit var jdbcUrl: String
    private lateinit var username: String
    private lateinit var password: String

    @BeforeEach
    fun setup() {
        // 環境変数からテスト用データベース設定を取得
        jdbcUrl = System.getenv("TEST_DB_JDBC_URL")
            ?: "jdbc:mysql://localhost:3306/keruta_test"
        username = System.getenv("TEST_DB_USERNAME") ?: "net/kigawa/keruta"
        password = System.getenv("TEST_DB_PASSWORD") ?: "net/kigawa/keruta"

        // データベース接続確認
        try {
            DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
                if (!conn.isValid(5)) {
                    fail("テスト用データベースへの接続が無効です。環境変数またはDockerコンテナを確認してください。")
                }
            }
        } catch (e: Exception) {
            fail("テスト用データベースに接続できません: ${e.message}\n" +
                "MySQLコンテナを起動するか、環境変数を設定してください:\n" +
                "  TEST_DB_JDBC_URL=$jdbcUrl\n" +
                "  TEST_DB_USERNAME=$username\n" +
                "  TEST_DB_PASSWORD=***")
        }

        migrator = FlywayMigrator()
    }

    @AfterEach
    fun teardown() {
        // テスト後にデータベースをクリーンアップ
        try {
            migrator.clean(jdbcUrl, username, password)
        } catch (e: Exception) {
            // クリーンアップエラーは無視（既にクリーンな場合など）
        }
    }

    @Test
    fun `マイグレーションが正常に実行されること`() {
        // マイグレーション実行
        migrator.migrate(jdbcUrl, username, password)

        // マイグレーション情報を取得
        val migrations = migrator.info(jdbcUrl, username, password)

        // マイグレーションが存在することを確認
        assertTrue(migrations.isNotEmpty(), "マイグレーションファイルが見つかりませんでした")

        // すべてのマイグレーションがSUCCESS状態であることを確認
        migrations.forEach { migration ->
            assertEquals(
                MigrationState.SUCCESS,
                migration.state,
                "マイグレーション ${migration.version}: ${migration.description} が失敗しています。状態: ${migration.state}"
            )
        }
    }

    @Test
    fun `マイグレーション後にテーブルが作成されていること`() {
        // マイグレーション実行
        migrator.migrate(jdbcUrl, username, password)

        // データベース接続してテーブル存在確認
        DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
            val metaData = conn.metaData

            // 期待されるテーブルのリスト（MySQLでは小文字で保存される）
            val expectedTables = listOf(
                "provider",
                "queue",
                "user",
                "queue_user",
                "task",
                "user_idp",
                "queue_provider"
            )

            expectedTables.forEach { tableName ->
                val rs = metaData.getTables(null, null, tableName, arrayOf("TABLE"))
                assertTrue(
                    rs.next(),
                    "テーブル $tableName が作成されていません"
                )
                rs.close()
            }
        }
    }

    @Test
    fun `マイグレーション情報が正しく取得できること`() {
        // マイグレーション実行
        migrator.migrate(jdbcUrl, username, password)

        // マイグレーション情報を取得
        val migrations = migrator.info(jdbcUrl, username, password)

        // マイグレーションファイルが見つかっていることを確認
        assertTrue(migrations.isNotEmpty(), "マイグレーション情報が取得できませんでした")

        // 各マイグレーションの基本情報を確認
        migrations.forEach { migration ->
            assertNotNull(migration.version, "マイグレーションのバージョンがnullです")
            assertNotNull(migration.description, "マイグレーションの説明がnullです")
            assertNotNull(migration.state, "マイグレーションの状態がnullです")
        }
    }

    @Test
    fun `clean操作が正常に動作すること`() {
        // まずマイグレーションを実行
        migrator.migrate(jdbcUrl, username, password)

        // テーブルが存在することを確認
        DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
            val metaData = conn.metaData
            val rs = metaData.getTables(null, null, "provider", arrayOf("TABLE"))
            assertTrue(rs.next(), "マイグレーション後にテーブルが存在していません")
            rs.close()
        }

        // クリーン実行
        migrator.clean(jdbcUrl, username, password)

        // テーブルが削除されていることを確認
        DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
            val metaData = conn.metaData
            val rs = metaData.getTables(null, null, "provider", arrayOf("TABLE"))
            assertTrue(!rs.next(), "clean後もテーブルが残っています")
            rs.close()
        }
    }

    @Test
    fun `repair操作が正常に動作すること`() {
        // マイグレーション実行
        migrator.migrate(jdbcUrl, username, password)

        // repair操作（エラーが発生しないことを確認）
        migrator.repair(jdbcUrl, username, password)

        // マイグレーション情報が引き続き取得できることを確認
        val migrations = migrator.info(jdbcUrl, username, password)
        assertTrue(migrations.isNotEmpty(), "repair後にマイグレーション情報が取得できませんでした")
    }

    @Test
    fun `重複したマイグレーション実行がべき等であること`() {
        // 1回目のマイグレーション
        migrator.migrate(jdbcUrl, username, password)
        val migrations1 = migrator.info(jdbcUrl, username, password)

        // 2回目のマイグレーション（既に適用済みのため、新規実行はされない）
        migrator.migrate(jdbcUrl, username, password)
        val migrations2 = migrator.info(jdbcUrl, username, password)

        // マイグレーション数が同じであることを確認
        assertEquals(migrations1.size, migrations2.size, "重複実行後にマイグレーション数が変わっています")

        // すべてのマイグレーションがSUCCESS状態であることを確認
        migrations2.forEach { migration ->
            assertEquals(
                MigrationState.SUCCESS,
                migration.state,
                "重複実行後のマイグレーション ${migration.version} の状態が不正です"
            )
        }
    }

    @Test
    fun `無効なJDBC URLでマイグレーションが失敗すること`() {
        val invalidJdbcUrl = "jdbc:mysql://invalid-host:3306/invalid_db"

        // 無効なURLでマイグレーションを実行すると例外が発生することを確認
        assertThrows<Exception> {
            migrator.migrate(invalidJdbcUrl, "invalid_user", "invalid_password")
        }
    }

    @Test
    fun `特定のカラムが正しく作成されていること`() {
        // マイグレーション実行
        migrator.migrate(jdbcUrl, username, password)

        // データベース接続してカラム確認
        DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
            val metaData = conn.metaData

            // providerテーブルのカラム確認
            val providerColumns = mutableListOf<String>()
            val rs = metaData.getColumns(null, null, "provider", null)
            while (rs.next()) {
                providerColumns.add(rs.getString("COLUMN_NAME").lowercase())
            }
            rs.close()

            // 期待されるカラムが存在することを確認
            assertTrue(providerColumns.contains("id"), "providerテーブルにidカラムがありません")
            assertTrue(providerColumns.contains("name"), "providerテーブルにnameカラムがありません")
            assertTrue(providerColumns.contains("issuer"), "providerテーブルにissuerカラムがありません")
            assertTrue(providerColumns.contains("setting"), "providerテーブルにsettingカラムがありません")
            // V006マイグレーションでcreate_atからcreated_atに変更されている
            assertTrue(providerColumns.contains("created_at"), "providerテーブルにcreated_atカラムがありません")
        }
    }
}
