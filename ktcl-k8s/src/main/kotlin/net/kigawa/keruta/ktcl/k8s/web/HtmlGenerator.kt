package net.kigawa.keruta.ktcl.k8s.web

import kotlinx.html.*
import kotlinx.html.stream.createHTML

/**
 * kotlinx-htmlを使用したHTML生成クラス
 */
object HtmlGenerator {
    fun generateIndexHtml(): String {
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
    transition: background-color 0.15s;
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
input[type="password"],
input[type="number"],
select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid var(--color-border);
    border-radius: 6px;
    font-size: 14px;
    transition: border-color 0.15s, box-shadow 0.15s;
}

input:focus,
select:focus {
    outline: none;
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px rgba(10, 88, 202, 0.1);
}

.checkbox-group {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
}

.checkbox-group input[type="checkbox"] {
    width: 18px;
    height: 18px;
    margin-right: 8px;
    accent-color: var(--color-primary);
}

.checkbox-group label {
    margin-bottom: 0;
    font-weight: 400;
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
    transition: background-color 0.15s, transform 0.1s;
}

button:hover {
    background: #0842a0;
}

button:active {
    transform: scale(0.98);
}

button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.message {
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 16px;
    display: none;
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

.section {
    margin-top: 24px;
}

.login-screen,
.config-screen {
    display: none;
}

.active {
    display: block;
}

.logout-btn {
    background: #dc3545;
}

.logout-btn:hover {
    background: #bb2d3b;
}

.keycloak-btn {
    background: #0d6efd;
    width: 100%;
    padding: 14px;
    font-size: 16px;
}

.keycloak-btn:hover {
    background: #0b5ed7;
}

@media (max-width: 768px) {
    .sidebar {
        position: fixed;
        left: 0;
        top: 0;
        bottom: 0;
        z-index: 100;
        transform: translateX(-100%);
        transition: transform 0.3s;
    }

    .sidebar.open {
        transform: translateX(0);
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
                    // サイドバー
                    aside("sidebar") {
                        id = "sidebar"
                        h1("sidebar-title") { +"Keruta" }
                        nav("sidebar-nav") {
                            a("#") {
                                classes = setOf("sidebar-link")
                                id = "navConfig"
                                onClick = "showPage('config')"
                                +"設定"
                            }
                            a("#") {
                                classes = setOf("sidebar-link")
                                id = "navProviders"
                                onClick = "showPage('providers')"
                                +"プロバイダー一覧"
                            }
                        }
                    }

                    // メインコンテンツ
                    main("main-content") {
                        div("container") {
                            // ログイン画面
                            div("login-screen") {
                                id = "loginScreen"
                                div("card") {
                                    h1 { +"ktcl-k8s 設定管理" }
                                    p("subtitle") { +"Keycloakで認証してください" }
                                    div("message") {
                                        id = "loginMessage"
                                    }
                                    button {
                                        id = "keycloakLoginBtn"
                                        classes = setOf("keycloak-btn")
                                        +"Keycloakでログイン"
                                    }
                                }
                            }

                            // プロバイダー一覧画面
                            div("config-screen") {
                                id = "providersScreen"
                                h1 { +"プロバイダー一覧" }
                                p("subtitle") { +"ktseに登録されているプロバイダーの一覧です" }
                                div("message") {
                                    id = "providersMessage"
                                }
                                div("card") {
                                    h2("card-title") { +"プロバイダー" }
                                    div {
                                        id = "providersTableContainer"
                                        +"読み込み中..."
                                    }
                                }
                            }

                            // 設定画面
                            div("config-screen") {
                                id = "configScreen"
                                h1 { +"設定管理" }
                                p("subtitle") { +"Kubernetes、キュー、Claude Code の設定を変更できます" }
                                div("message") {
                                    id = "configMessage"
                                }

                                // Kubernetes設定
                                div("card") {
                                    h2("card-title") { +"Kubernetes設定" }
                                    div("form-group") {
                                        label {
                                            htmlFor = "namespace"
                                            +"ネームスペース"
                                        }
                                        input(InputType.text) {
                                            id = "namespace"
                                            placeholder = "default"
                                        }
                                    }

                                    div("checkbox-group") {
                                        input(InputType.checkBox) {
                                            id = "useInCluster"
                                        }
                                        label {
                                            htmlFor = "useInCluster"
                                            +"In-Cluster認証を使用"
                                        }
                                    }

                                    div("form-group") {
                                        label {
                                            htmlFor = "kubeconfigPath"
                                            +"Kubeconfigパス（In-Cluster無効時）"
                                        }
                                        input(InputType.text) {
                                            id = "kubeconfigPath"
                                            placeholder = "~/.kube/config"
                                        }
                                    }

                                    div("form-group") {
                                        label {
                                            htmlFor = "jobTimeout"
                                            +"Jobタイムアウト（秒）"
                                        }
                                        input(InputType.number) {
                                            id = "jobTimeout"
                                            placeholder = "600"
                                        }
                                    }

                                    button {
                                        id = "updateK8sBtn"
                                        +"Kubernetes設定を更新"
                                    }
                                }

                                // キュー設定
                                div("card") {
                                    h2("card-title") { +"キュー設定" }
                                    div("form-group") {
                                        label {
                                            htmlFor = "queueId"
                                            +"キューID"
                                        }
                                        input(InputType.number) {
                                            id = "queueId"
                                            placeholder = "1"
                                        }
                                    }

                                    button {
                                        id = "updateQueueBtn"
                                        +"キュー設定を更新"
                                    }
                                }

                                // Claude Code設定
                                div("card") {
                                    h2("card-title") { +"Claude Code設定" }
                                    p("subtitle") {
                                        id = "claudeApiKeyStatus"
                                        +"APIキー未設定"
                                    }
                                    div("form-group") {
                                        label {
                                            htmlFor = "anthropicApiKey"
                                            +"Anthropic APIキー"
                                        }
                                        input(InputType.password) {
                                            id = "anthropicApiKey"
                                            placeholder = "sk-ant-..."
                                        }
                                    }
                                    button {
                                        id = "updateClaudeCodeBtn"
                                        +"Claude Code APIキーを更新"
                                    }
                                }

                                div("card") {
                                    button {
                                        id = "logoutBtn"
                                        classes = setOf("logout-btn")
                                        +"ログアウト"
                                    }
                                }
                            }
                        }
                    }
                }

                script {
                    unsafe {
                        +"""
        // Keycloak設定（環境変数から注入）
        const KEYCLOAK_URL = '${"$"}{KEYCLOAK_URL}';
        const KEYCLOAK_REALM = '${"$"}{KEYCLOAK_REALM}';
        const KEYCLOAK_CLIENT_ID = '${"$"}{KEYCLOAK_CLIENT_ID}';

        let token = null;

        // 画面切り替え
        function showLoginScreen() {
            document.getElementById('loginScreen').classList.add('active');
            document.getElementById('configScreen').classList.remove('active');
            document.getElementById('providersScreen').classList.remove('active');
        }

        function showConfigScreen() {
            document.getElementById('loginScreen').classList.remove('active');
            document.getElementById('configScreen').classList.add('active');
            document.getElementById('providersScreen').classList.remove('active');
        }

        function showPage(page) {
            document.getElementById('configScreen').classList.remove('active');
            document.getElementById('providersScreen').classList.remove('active');
            if (page === 'config') {
                document.getElementById('configScreen').classList.add('active');
            } else if (page === 'providers') {
                document.getElementById('providersScreen').classList.add('active');
                loadProviders();
            }
        }

        async function loadProviders() {
            try {
                const response = await fetch('/api/providers', {
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (!response.ok) throw new Error('プロバイダーの読み込みに失敗しました');
                const data = await response.json();
                const container = document.getElementById('providersTableContainer');
                if (data.providers.length === 0) {
                    container.textContent = 'プロバイダーが登録されていません';
                    return;
                }
                const table = document.createElement('table');
                table.innerHTML = '<thead><tr><th>ID</th><th>名前</th><th>Issuer</th><th>Audience</th></tr></thead>';
                const tbody = document.createElement('tbody');
                for (const p of data.providers) {
                    const tr = document.createElement('tr');
                    tr.innerHTML = '<td>' + p.id + '</td><td>' + p.name + '</td><td>' + p.issuer + '</td><td>' + p.audience + '</td>';
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                container.innerHTML = '';
                container.appendChild(table);
            } catch (error) {
                showMessage('providersMessage', 'エラー: ' + error.message, 'error');
            }
        }

        // メッセージ表示
        function showMessage(elementId, text, type) {
            const el = document.getElementById(elementId);
            el.textContent = text;
            el.className = 'message ' + type;
            el.style.display = 'block';
            setTimeout(() => {
                el.style.display = 'none';
            }, 5000);
        }

        // Keycloakログイン
        document.getElementById('keycloakLoginBtn').addEventListener('click', async () => {
            const authUrl = KEYCLOAK_URL + 'realms/' + KEYCLOAK_REALM + '/protocol/openid-connect/auth';
            const params = new URLSearchParams({
                client_id: KEYCLOAK_CLIENT_ID,
                redirect_uri: window.location.origin + '/login',
                response_type: 'token',
                scope: 'openid'
            });

            window.location.href = authUrl + '?' + params;
        });

        // URLからトークンを取得
        function getTokenFromUrl() {
            const hash = window.location.hash.substring(1);
            const params = new URLSearchParams(hash);
            return params.get('access_token');
        }

        // ログイン処理
        async function login(accessToken) {
            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ token: accessToken })
                });

                const data = await response.json();

                if (data.success) {
                    token = accessToken;
                    await loadConfig();
                    showPage('config');
                } else {
                    showMessage('loginMessage', 'ログインに失敗しました: ' + data.message, 'error');
                }
            } catch (error) {
                showMessage('loginMessage', 'ログインエラー: ' + error.message, 'error');
            }
        }

        // 設定読み込み
        async function loadConfig() {
            try {
                const response = await fetch('/api/config', {
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                });

                if (!response.ok) {
                    throw new Error('設定の読み込みに失敗しました');
                }

                const data = await response.json();

                document.getElementById('namespace').value = data.kubernetes.namespace;
                document.getElementById('useInCluster').checked = data.kubernetes.useInCluster;
                document.getElementById('kubeconfigPath').value = data.kubernetes.kubeconfigPath || '';
                document.getElementById('jobTimeout').value = data.kubernetes.jobTimeout;
                document.getElementById('queueId').value = data.queue.queueId;
                document.getElementById('claudeApiKeyStatus').textContent = data.claudeCode.hasApiKey
                    ? 'APIキー設定済み'
                    : 'APIキー未設定';
            } catch (error) {
                showMessage('configMessage', '設定の読み込みエラー: ' + error.message, 'error');
            }
        }

        // Kubernetes設定更新
        document.getElementById('updateK8sBtn').addEventListener('click', async () => {
            try {
                const response = await fetch('/api/config/kubernetes', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({
                        namespace: document.getElementById('namespace').value,
                        useInCluster: document.getElementById('useInCluster').checked,
                        kubeconfigPath: document.getElementById('kubeconfigPath').value || null,
                        jobTimeout: parseInt(document.getElementById('jobTimeout').value)
                    })
                });

                const data = await response.json();
                showMessage('configMessage', data.message, 'success');
            } catch (error) {
                showMessage('configMessage', '更新エラー: ' + error.message, 'error');
            }
        });

        // Claude Code設定更新
        document.getElementById('updateClaudeCodeBtn').addEventListener('click', async () => {
            try {
                const anthropicApiKey = document.getElementById('anthropicApiKey').value.trim();
                if (!anthropicApiKey) {
                    showMessage('configMessage', 'Anthropic APIキーを入力してください', 'error');
                    return;
                }

                const response = await fetch('/api/config/claudecode', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({
                        anthropicApiKey: anthropicApiKey
                    })
                });

                const data = await response.json();
                if (!response.ok) {
                    throw new Error(data.error || 'Claude Code設定の更新に失敗しました');
                }

                document.getElementById('anthropicApiKey').value = '';
                document.getElementById('claudeApiKeyStatus').textContent = 'APIキー設定済み';
                showMessage('configMessage', data.message, 'success');
            } catch (error) {
                showMessage('configMessage', '更新エラー: ' + error.message, 'error');
            }
        });

        // キュー設定更新
        document.getElementById('updateQueueBtn').addEventListener('click', async () => {
            try {
                const response = await fetch('/api/config/queue', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({
                        queueId: parseInt(document.getElementById('queueId').value)
                    })
                });

                const data = await response.json();
                showMessage('configMessage', data.message, 'success');
            } catch (error) {
                showMessage('configMessage', '更新エラー: ' + error.message, 'error');
            }
        });

        // ログアウト
        document.getElementById('logoutBtn').addEventListener('click', async () => {
            try {
                await fetch('/api/auth/logout', { method: 'POST' });
                token = null;
                showLoginScreen();
            } catch (error) {
                console.error('Logout error:', error);
            }
        });

        // 初期化
        window.addEventListener('load', () => {
            const accessToken = getTokenFromUrl();
            if (accessToken) {
                // URLからトークンを削除
                window.history.replaceState({}, document.title, window.location.pathname);
                login(accessToken);
            } else {
                showLoginScreen();
            }
        });
                        """.trimIndent()
                    }
                }
            }
        }
    }
}
