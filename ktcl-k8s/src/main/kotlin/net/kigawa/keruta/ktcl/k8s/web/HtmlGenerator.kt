package net.kigawa.keruta.ktcl.k8s.web

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import net.kigawa.keruta.ktcl.k8s.dto.ProviderDto

/**
 * kotlinx-htmlを使用したHTML生成クラス
 */
object HtmlGenerator {
    fun generateIndexHtml(
        hasGithubToken: Boolean = false,
        hasClaudeToken: Boolean = false,
        success: String? = null,
        error: String? = null,
    ): String {
        return createHTML().html {
            lang = "ja"
            head {
                meta(charset = "UTF-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                title { +"ktcl-k8s 設定管理" }
                style {
                    unsafe {
                        +"""
:root {
    --color-primary: #0a58ca;
    --color-bg: #ffffff;
    --color-bg-sidebar: #f8f9fa;
    --color-border: #dee2e6;
    --color-text: #212529;
    --color-text-muted: #6c757d;
}

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    background: var(--color-bg);
    color: var(--color-text);
    min-height: 100vh;
}

.app-container {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    width: 256px;
    background: var(--color-bg-sidebar);
    border-right: 1px solid var(--color-border);
    padding: 24px 16px;
    flex-shrink: 0;
}

.sidebar-title {
    font-size: 24px;
    font-weight: 700;
    color: var(--color-primary);
    margin-bottom: 32px;
}

.sidebar-nav {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.sidebar-link {
    display: block;
    padding: 8px 16px;
    color: var(--color-primary);
    text-decoration: none;
    border-radius: 6px;
}

.sidebar-link:hover {
    background: rgba(10, 88, 202, 0.1);
}

.main-content {
    flex: 1;
    padding: 32px;
    overflow-y: auto;
}

.container {
    max-width: 600px;
    margin: 0 auto;
}

h1 {
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 8px;
    color: var(--color-text);
}

.subtitle {
    color: var(--color-text-muted);
    margin-bottom: 32px;
    font-size: 14px;
}

.card {
    background: white;
    border: 1px solid var(--color-border);
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
}

.card-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--color-text);
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--color-border);
}

table {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;
}

th, td {
    text-align: left;
    padding: 10px 12px;
    border-bottom: 1px solid var(--color-border);
}

th {
    font-weight: 600;
    color: var(--color-text-muted);
    background: #f8f9fa;
}

.form-group {
    margin-bottom: 16px;
}

label {
    display: block;
    margin-bottom: 6px;
    color: var(--color-text);
    font-weight: 500;
    font-size: 14px;
}

input[type="text"],
input[type="password"] {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid var(--color-border);
    border-radius: 6px;
    font-size: 14px;
}

input:focus {
    outline: none;
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px rgba(10, 88, 202, 0.1);
}

button {
    padding: 10px 20px;
    background: var(--color-primary);
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
}

button:hover {
    background: #0842a0;
}

.message {
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 16px;
    font-size: 14px;
}

.message.success {
    background: #d1e7dd;
    color: #0f5132;
    border: 1px solid #badbcc;
}

.message.error {
    background: #f8d7da;
    color: #842029;
    border: 1px solid #f5c2c7;
}

.logout-btn {
    background: #dc3545;
}

.logout-btn:hover {
    background: #bb2d3b;
}

@media (max-width: 768px) {
    .sidebar {
        display: none;
    }

    .main-content {
        padding: 16px;
    }
}
                        """.trimIndent()
                    }
                }
            }
            body {
                div("app-container") {
                    aside("sidebar") {
                        h1("sidebar-title") { +"Keruta" }
                        nav("sidebar-nav") {
                            a("/") {
                                classes = setOf("sidebar-link")
                                +"設定"
                            }
                            a("/providers") {
                                classes = setOf("sidebar-link")
                                +"プロバイダー一覧"
                            }
                        }
                    }

                    main("main-content") {
                        div("container") {
                            h1 { +"設定管理" }
                            p("subtitle") { +"Claude Code・GitHub Token の設定を変更できます" }

                            if (success != null) {
                                div("message success") {
                                    +when (success) {
                                        "github_token_saved" -> "GitHub Token を保存しました"
                                        "claude_key_saved" -> "Claude Code APIキーを保存しました"
                                        else -> "設定を保存しました"
                                    }
                                }
                            }

                            if (error != null) {
                                div("message error") {
                                    +when (error) {
                                        "token_required" -> "GitHub Token を入力してください"
                                        "api_key_required" -> "Anthropic APIキーを入力してください"
                                        else -> "エラーが発生しました"
                                    }
                                }
                            }

                            div("card") {
                                h2("card-title") { +"GitHub Token設定" }
                                p("subtitle") {
                                    +(if (hasGithubToken) "Token設定済み" else "Token未設定")
                                }
                                form {
                                    action = "/config/github"
                                    method = FormMethod.post
                                    div("form-group") {
                                        label {
                                            htmlFor = "githubToken"
                                            +"GitHub Personal Access Token"
                                        }
                                        input(InputType.password) {
                                            id = "githubToken"
                                            name = "githubToken"
                                            placeholder = "ghp_..."
                                        }
                                    }
                                    button {
                                        type = ButtonType.submit
                                        +"GitHub Tokenを保存"
                                    }
                                }
                            }

                            div("card") {
                                h2("card-title") { +"Claude Code設定" }
                                p("subtitle") {
                                    +(if (hasClaudeToken) "APIキー設定済み" else "APIキー未設定")
                                }
                                form {
                                    action = "/config/claudecode"
                                    method = FormMethod.post
                                    div("form-group") {
                                        label {
                                            htmlFor = "anthropicApiKey"
                                            +"Anthropic APIキー"
                                        }
                                        input(InputType.password) {
                                            id = "anthropicApiKey"
                                            name = "anthropicApiKey"
                                            placeholder = "sk-ant-..."
                                        }
                                    }
                                    button {
                                        type = ButtonType.submit
                                        +"Claude Code APIキーを保存"
                                    }
                                }
                            }

                            div("card") {
                                form {
                                    action = "/config/logout"
                                    method = FormMethod.post
                                    button {
                                        type = ButtonType.submit
                                        classes = setOf("logout-btn")
                                        +"ログアウト"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun generateProvidersHtml(providers: List<ProviderDto>): String {
        return createHTML().html {
            lang = "ja"
            head {
                meta(charset = "UTF-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                title { +"プロバイダー一覧 - ktcl-k8s" }
                style {
                    unsafe {
                        +"""
:root { --color-primary: #0a58ca; --color-bg: #ffffff; --color-bg-sidebar: #f8f9fa; --color-border: #dee2e6; --color-text: #212529; --color-text-muted: #6c757d; }
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: var(--color-bg); color: var(--color-text); min-height: 100vh; }
.app-container { display: flex; min-height: 100vh; }
.sidebar { width: 256px; background: var(--color-bg-sidebar); border-right: 1px solid var(--color-border); padding: 24px 16px; flex-shrink: 0; }
.sidebar-title { font-size: 24px; font-weight: 700; color: var(--color-primary); margin-bottom: 32px; }
.sidebar-nav { display: flex; flex-direction: column; gap: 8px; }
.sidebar-link { display: block; padding: 8px 16px; color: var(--color-primary); text-decoration: none; border-radius: 6px; }
.sidebar-link:hover { background: rgba(10, 88, 202, 0.1); }
.main-content { flex: 1; padding: 32px; overflow-y: auto; }
.container { max-width: 800px; margin: 0 auto; }
h1 { font-size: 28px; font-weight: 600; margin-bottom: 8px; color: var(--color-text); }
.subtitle { color: var(--color-text-muted); margin-bottom: 32px; font-size: 14px; }
.card { background: white; border: 1px solid var(--color-border); border-radius: 8px; padding: 24px; margin-bottom: 24px; }
.card-title { font-size: 18px; font-weight: 600; color: var(--color-text); margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid var(--color-border); }
table { width: 100%; border-collapse: collapse; font-size: 14px; }
th, td { text-align: left; padding: 10px 12px; border-bottom: 1px solid var(--color-border); }
th { font-weight: 600; color: var(--color-text-muted); background: #f8f9fa; }
@media (max-width: 768px) { .sidebar { display: none; } .main-content { padding: 16px; } }
                        """.trimIndent()
                    }
                }
            }
            body {
                div("app-container") {
                    aside("sidebar") {
                        h1("sidebar-title") { +"Keruta" }
                        nav("sidebar-nav") {
                            a("/") {
                                classes = setOf("sidebar-link")
                                +"設定"
                            }
                            a("/providers") {
                                classes = setOf("sidebar-link")
                                +"プロバイダー一覧"
                            }
                        }
                    }
                    main("main-content") {
                        div("container") {
                            h1 { +"プロバイダー一覧" }
                            p("subtitle") { +"ktseに登録されているプロバイダーの一覧です" }
                            div("card") {
                                h2("card-title") { +"プロバイダー" }
                                if (providers.isEmpty()) {
                                    p { +"プロバイダーが登録されていません" }
                                } else {
                                    table {
                                        thead {
                                            tr {
                                                th { +"名前" }
                                                th { +"Issuer" }
                                                th { +"Audience" }
                                            }
                                        }
                                        tbody {
                                            for (p in providers) {
                                                tr {
                                                    td { +p.name }
                                                    td {
                                                        a {
                                                            href = p.issuer
                                                            +p.issuer
                                                        }
                                                    }
                                                    td { +p.audience }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
