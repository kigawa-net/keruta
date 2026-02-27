package net.kigawa.keruta.ktcl.k8s.web.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthenticationHelper
import net.kigawa.kodel.api.log.LoggerFactory

class StaticRoutes(
    private val authenticationHelper: AuthenticationHelper,
) {
    private val logger = LoggerFactory.get("StaticRoutes")

    fun configure(route: Route) {
        route.apply {
            get("/") {
                val user = authenticationHelper.getAuthenticatedUser(call)
                if (user == null) {
                    logger.fine("User not authenticated, redirecting to /login")
                    call.respondRedirect("/login")
                } else {
                    logger.fine("User authenticated: ${user.userId}")
                    call.respondText(getIndexHtml(), ContentType.Text.Html)
                }
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
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 40px;
            max-width: 600px;
            width: 100%;
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 28px;
        }

        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-size: 14px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #444;
            font-weight: 500;
            font-size: 14px;
        }

        input[type="text"],
        input[type="number"],
        select {
            width: 100%;
            padding: 12px;
            border: 2px solid #e1e8ed;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
        }

        .checkbox-group {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        .checkbox-group input[type="checkbox"] {
            width: 20px;
            height: 20px;
            margin-right: 10px;
        }

        .checkbox-group label {
            margin-bottom: 0;
        }

        button {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }

        button:active {
            transform: translateY(0);
        }

        button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .message {
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: none;
            font-size: 14px;
        }

        .message.success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .message.error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .section {
            border-top: 1px solid #e1e8ed;
            padding-top: 20px;
            margin-top: 20px;
        }

        .section h2 {
            color: #444;
            margin-bottom: 15px;
            font-size: 18px;
        }

        .login-screen,
        .config-screen {
            display: none;
        }

        .active {
            display: block;
        }

        .keycloak-btn {
            background: linear-gradient(135deg, #4285f4 0%, #34a853 100%);
        }

        .keycloak-btn:hover {
            box-shadow: 0 10px 20px rgba(66, 133, 244, 0.4);
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- ログイン画面 -->
        <div id="loginScreen" class="login-screen active">
            <h1>ktcl-k8s 設定管理</h1>
            <p class="subtitle">Keycloakで認証してください</p>
            <div id="loginMessage" class="message"></div>
            <button id="keycloakLoginBtn" class="keycloak-btn">Keycloakでログイン</button>
        </div>

        <!-- 設定画面 -->
        <div id="configScreen" class="config-screen">
            <h1>設定管理</h1>
            <p class="subtitle">Kubernetes とキューの設定を変更できます</p>
            <div id="configMessage" class="message"></div>

            <!-- Kubernetes設定 -->
            <div class="section">
                <h2>Kubernetes設定</h2>
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
            <div class="section">
                <h2>キュー設定</h2>
                <div class="form-group">
                    <label for="queueId">キューID</label>
                    <input type="number" id="queueId" placeholder="1">
                </div>

                <button id="updateQueueBtn">キュー設定を更新</button>
            </div>

            <div class="section">
                <button id="logoutBtn" style="background: #dc3545;">ログアウト</button>
            </div>
        </div>
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

