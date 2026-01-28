package net.kigawa.keruta.ktcl.claudecode.claude

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcl.claudecode.err.ClaudeApiErr
import net.kigawa.kodel.api.err.Res
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class ClaudeCodeCliClient {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendMessage(prompt: String): Res<String, ClaudeApiErr> = withContext(Dispatchers.IO) {
        try {
            val processBuilder = ProcessBuilder(
                "claude",
                "code"
            )

            // 環境変数を渡す（必要に応じて）
            val env = processBuilder.environment()
            // env["ANTHROPIC_API_KEY"] は既に環境変数に設定されていると想定

            // プロセス開始
            val process = processBuilder.start()

            // stdin に書き込む
            process.outputStream.bufferedWriter(StandardCharsets.UTF_8).use { writer ->
                writer.write(prompt)
                writer.flush()
            }

            // 結果を読む
            val result = StringBuilder()
            BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.forEachLine { line ->
                    result.appendLine(line)
                }
            }

            // エラー出力を読む
            val errorOutput = StringBuilder()
            BufferedReader(InputStreamReader(process.errorStream, StandardCharsets.UTF_8)).use { err ->
                err.forEachLine { line ->
                    errorOutput.appendLine(line)
                }
            }

            // タイムアウト付きで終了を待つ（30秒）
            val finished = process.waitFor(30, TimeUnit.SECONDS)

            if (!finished) {
                process.destroyForcibly()
                return@withContext Res.Err(ClaudeApiErr("Claude Code CLI execution timeout", null))
            }

            val exitCode = process.exitValue()
            if (exitCode != 0) {
                return@withContext Res.Err(
                    ClaudeApiErr("Claude Code CLI failed (exit code: $exitCode): $errorOutput", null)
                )
            }

            val output = result.toString()
            if (output.isBlank()) {
                return@withContext Res.Err(ClaudeApiErr("No response from Claude Code CLI", null))
            }

            Res.Ok(output)
        } catch (e: Exception) {
            Res.Err(ClaudeApiErr("Claude Code CLI execution failed: ${e.message}", e))
        }
    }
}