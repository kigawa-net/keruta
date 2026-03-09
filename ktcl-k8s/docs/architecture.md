# アーキテクチャ

## システム構成

```
┌──────────────┐     WebSocket/KTCP     ┌──────────────┐
│   KTSE       │<────────────────────────│  ktcl-k8s    │
│  (Server)    │                         │  (Client)    │
└──────────────┘                         └──────┬───────┘
                                               │
                                         Kubernetes API
                                               │
                                               ▼
                                       ┌──────────────┐
                                       │ Kubernetes   │
                                       │   Job        │
                                       └──────────────┘
```

## コンポーネント構成

### 認証・接続管理

- **ConnectionManager**: WebSocket接続の確立と管理
- **AuthManager**: KTCP二重トークン認証（ユーザー + プロバイダー）
- **JvmWebSocketConnection**: JVM用WebSocket実装

### Kubernetes統合

- **K8sClientFactory**: Kubernetes Clientの初期化（in-cluster / kubeconfig）
- **K8sJobExecutor**: Kubernetes Job作成とパラメータ設定
- **K8sJobWatcher**: Job実行状態の監視（5秒間隔ポーリング）
- **JobTemplateLoader**: YAMLテンプレートの読み込みと変数置換

### タスク処理

- **TaskReceiver**: KTCPメッセージ受信ループ
- **TaskExecutor**: タスク受信時のJob実行オーケストレーション
- **ReceiveTaskListedEntrypoint**: タスク一覧受信エントリーポイント
- **ReceiveTaskShowedEntrypoint**: タスク詳細受信エントリーポイント

### Webモード（オプション）

- **WebServer**: Ktor HTTP Server
- **AuthConfig**: Keycloak OIDC認証
- **ConfigRoutes**: 設定管理API（Kubernetes、キュー）

## 動作フロー

### 起動フロー

1. **起動**: 環境変数から設定を読み込み
2. **WebSocket接続**: ConnectionManagerがKTSEサーバーへ接続
3. **KTCP認証**: AuthManagerが二重トークン認証を実行
4. **K8s Client初期化**: K8sClientFactoryがKubernetes Clientを作成
5. **タスク一覧取得**: 起動時に既存のpendingタスクを取得
6. **メッセージ受信ループ**: TaskReceiverがメッセージを待機

### タスク実行フロー

1. **タスク受信**: `ClientTaskListedMsg`または`ClientTaskShowedMsg`を受信
2. **ステータス更新**: `status="running"`に更新
3. **Job作成**:
   - YAMLテンプレートを読み込み
   - タスク情報（ID、タイトル、説明）を環境変数として設定
   - Kubernetes BatchV1 APIでJob作成
4. **Job監視**: 5秒間隔でJob状態をポーリング
5. **ステータス更新**:
   - `succeeded` → `status="completed"`
   - `failed` → `status="failed"`
   - `timeout` → `status="failed"`

### Webモードフロー

1. **Web UIアクセス**: ブラウザで`http://localhost:8081`にアクセス
2. **Keycloakログイン**: OIDCフローでKeycloak認証
3. **JWT検証**: Auth0 JWT Verifierで署名検証
4. **設定管理**: REST APIで設定を取得・更新
5. **ランタイム反映**: 設定変更が即座に反映（再起動不要）

## エラーハンドリング

- **K8sErr**: Kubernetes関連エラーの基底クラス
  - `K8sClientErr`: Kubernetes Client初期化エラー
  - `JobCreateErr`: Job作成エラー
  - `JobWatchErr`: Job監視エラー
  - `JobTimeoutErr`: Jobタイムアウトエラー
  - `TemplateLoadErr`: テンプレート読み込みエラー

## 技術スタック

- **Kotlin Multiplatform**: JVM対応
- **Ktor Client**: WebSocket通信
- **Ktor Server**: Webモード（認証、API、静的ファイル配信）
- **Kubernetes Java Client 25.x**: Kubernetes API統合
- **KTCP**: Kerutaプロトコル
- **kotlinx.serialization**: JSON処理
- **kotlinx.coroutines**: 非同期処理
- **Auth0 Java JWT**: JWT検証とJWKSサポート