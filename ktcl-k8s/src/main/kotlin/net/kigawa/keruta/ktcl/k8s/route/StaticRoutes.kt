package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.auth.UserSession
import net.kigawa.keruta.ktcl.k8s.auth.AuthenticationHelper
import net.kigawa.kodel.api.log.getKogger

class StaticRoutes(
    private val authenticationHelper: AuthenticationHelper,
) {
    private val logger = getKogger()

    fun configure(route: Route) {
        route.apply {
            get("/") {
                logger.fine("Request to root path, checking session")
                val session = call.sessions.get<UserSession>()
                logger.fine("Session from cookie: $session")

                val user = authenticationHelper.getAuthenticatedUser(call)
                logger.fine("Authenticated user: $user")

                if (user == null) {
                    logger.fine("User not authenticated, redirecting to /login")
                    call.respondRedirect("/login")
                } else {
                    logger.fine("User authenticated: ${user.userId}")
                    call.respondText(getIndexHtml(), ContentType.Text.Html)
                }
            }

            get("/health") {
                call.respondText("OK", ContentType.Text.Plain)
            }

            get("/ready") {
                call.respondText("OK", ContentType.Text.Plain)
            }


            get("/config") {
                call.respondText(getIndexHtml(), ContentType.Text.Html)
            }
        }
    }

    private fun getIndexHtml(): String {
        return $$"""
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ktcl-k8s 設定管理</title>
    <style>
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
    </style>
</head>
<body>
    <div class="app-container">
        <!-- サイドバー -->
        <aside id="sidebar" class="sidebar">
            <h1 class="sidebar-title">Keruta</h1>
            <nav class="sidebar-nav">
                <a href="/" class="sidebar-link active">設定</a>
            </nav>
        </aside>

        <!-- メインコンテンツ -->
        <main class="main-content">
            <div class="container">
                <!-- ログイン画面 -->
                <div id="loginScreen" class="login-screen">
                    <div class="card">
                        <h1>ktcl-k8s 設定管理</h1>
                        <p class="subtitle">Keycloakで認証してください</p>
                        <div id="loginMessage" class="message"></div>
                        <button id="keycloakLoginBtn" class="keycloak-btn">Keycloakでログイン</button>
                    </div>
                </div>

                <!-- 設定画面 -->
                <div id="configScreen" class="config-screen">
                    <h1>設定管理</h1>
                    <p class="subtitle">Kubernetes とキューの設定を変更できます</p>
                    <div id="configMessage" class="message"></div>

                    <!-- Kubernetes設定 -->
                    <div class="card">
                        <h2 class="card-title">Kubernetes設定</h2>
                        <div class="form-group">
                            <label for="namespace">ネームスペース</label>
                            <input type="text" id="namespace" placeholder="default">
                        </div>

                        <div class="checkbox-group">
                            <input type="checkbox" id="useInCluster">
                            <label for="useInCluster">In-Cluster認証を使用</label>
                        </div>

                        <div class="form-group">
                            <label for="kubeconfigPath">Kubeconfigパス（In-Cluster無効時）</label>
                            <input type="text" id="kubeconfigPath" placeholder="~/.kube/config">
                        </div>

                        <div class="form-group">
                            <label for="jobTimeout">Jobタイムアウト（秒）</label>
                            <input type="number" id="jobTimeout" placeholder="600">
                        </div>

                        <button id="updateK8sBtn">Kubernetes設定を更新</button>
                    </div>

                    <!-- キュー設定 -->
                    <div class="card">
                        <h2 class="card-title">キュー設定</h2>
                        <div class="form-group">
                            <label for="queueId">キューID</label>
                            <input type="number" id="queueId" placeholder="1">
                        </div>

                        <button id="updateQueueBtn">キュー設定を更新</button>
                    </div>

                    <div class="card">
                        <button id="logoutBtn" class="logout-btn">ログアウト</button>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script>
        // Keycloak設定（環境変数から注入）
        const KEYCLOAK_URL = '$${System.getenv("KEYCLOAK_URL") ?: "https://user.kigawa.net/"}';
        const KEYCLOAK_REALM = '$${System.getenv("KEYCLOAK_REALM") ?: "develop"}';
        const KEYCLOAK_CLIENT_ID = '$${System.getenv("KEYCLOAK_CLIENT_ID") ?: "keruta"}';

        let token = null;

        // 画面切り替え
        function showLoginScreen() {
            document.getElementById('loginScreen').classList.add('active');
            document.getElementById('configScreen').classList.remove('active');
        }

        function showConfigScreen() {
            document.getElementById('loginScreen').classList.remove('active');
            document.getElementById('configScreen').classList.add('active');
        }

        // メッセージ表示
        function showMessage(elementId, text, type) {
            const el = document.getElementById(elementId);
            el.textContent = text;
            el.className = `message ${type}`;
            el.style.display = 'block';
            setTimeout(() => {
                el.style.display = 'none';
            }, 5000);
        }

        // Keycloakログイン
        document.getElementById('keycloakLoginBtn').addEventListener('click', async () => {
            const authUrl = `${KEYCLOAK_URL}realms/${KEYCLOAK_REALM}/protocol/openid-connect/auth`;
            const params = new URLSearchParams({
                client_id: KEYCLOAK_CLIENT_ID,
                redirect_uri: window.location.origin + '/login',
                response_type: 'token',
                scope: 'openid'
            });

            window.location.href = `${authUrl}?${params}`;
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
                    showConfigScreen();
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
                        'Authorization': `Bearer ${token}`
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
                        'Authorization': `Bearer ${token}`
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

        // キュー設定更新
        document.getElementById('updateQueueBtn').addEventListener('click', async () => {
            try {
                const response = await fetch('/api/config/queue', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
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
    </script>
</body>
</html>
    """.trimIndent()
    }
}

