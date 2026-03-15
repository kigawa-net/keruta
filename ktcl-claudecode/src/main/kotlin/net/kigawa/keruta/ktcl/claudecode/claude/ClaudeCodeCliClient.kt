package net.kigawa.keruta.ktcl.claudecode.claude

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kigawa.keruta.ktcl.claudecode.err.ClaudeApiErr
import net.kigawa.kodel.api.err.Res
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class ClaudeCodeCliClient {
    private val claudePath = System.getenv("CLAUDE_CLI_PATH") ?: "claude"

    suspend fun sendMessage(prompt: String): Res<String, ClaudeApiErr> = withContext(Dispatchers.IO) {
        try {
            val processBuilder = ProcessBuilder(
                claudePath, "--allow-dangerously-skip-permissions", "-p", prompt
            )
            processBuilder.directory(java.io.File("/workspace"))

            val process = processBuilder.start()
            process.outputStream.close()

            val result = StringBuilder()
            BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.forEachLine { line ->
                    result.appendLine(line)
                }
            }

            val errorOutput = StringBuilder()
            BufferedReader(InputStreamReader(process.errorStream, StandardCharsets.UTF_8)).use { err ->
                err.forEachLine { line ->
                    errorOutput.appendLine(line)
                }
            }

            val finished = process.waitFor(10, TimeUnit.MINUTES)

            if (!finished) {
                process.destroyForcibly()
                return@withContext Res.Err(ClaudeApiErr("Claude Code CLI execution timeout", null))
            }

            val exitCode = process.exitValue()
            if (exitCode != 0) {
                return@withContext Res.Err(
                    ClaudeApiErr("Claude Code CLI failed (exit code: $exitCode)\nstdout: $result\nstderr: $errorOutput", null)
                )
            }

            Res.Ok(result.toString())
        } catch (e: Exception) {
            Res.Err(ClaudeApiErr("Claude Code CLI execution failed: ${e.message}", e))
        }
    }
}
