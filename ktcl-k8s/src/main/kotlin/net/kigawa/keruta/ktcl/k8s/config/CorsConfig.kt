package net.kigawa.keruta.ktcl.k8s.config

/**
 * CORS設定を保持するクラス
 *
 * 環境変数:
 * - CORS_ALLOWED_ORIGINS: カンマ区切りの許可オリジンリスト（例: "https://example.com,http://localhost:3000"）
 *   未設定の場合はすべてのオリジンを許可する
 */
data class CorsConfig(
    val allowedOrigins: List<String>?
) {
    companion object {
        fun fromEnvironment(): CorsConfig {
            val originsString = System.getenv("CORS_ALLOWED_ORIGINS")
            return fromString(originsString)
        }

        fun fromString(originsString: String?): CorsConfig {
            val origins = originsString
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
            return CorsConfig(allowedOrigins = origins)
        }
    }
}
