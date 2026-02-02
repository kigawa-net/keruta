# ktcl-front-mobile

Keruta Task Client Mobile - Kotlin Multiplatform Mobile版タスククライアント

## 概要

`ktcl-front`（React + TypeScript）をKotlin Multiplatform Mobileに移植したiOS/Android向けモバイルアプリケーション。

## 技術スタック

- **Kotlin**: 2.3.0
- **Compose Multiplatform**: 1.10.0
- **Ktor Client**: 3.4.0 (WebSocket通信)
- **kotlinx.serialization**: メッセージシリアライゼーション
- **ktcp:client**: 既存のKTCPクライアントAPI再利用

## アーキテクチャ

### プロジェクト構成

```
ktcl-front-mobile/
├── src/
│   ├── commonMain/kotlin/          # 共通コード
│   │   ├── config/                 # MobileConfig
│   │   ├── connection/             # WebSocket接続（expect/actual）
│   │   ├── auth/                   # OIDC認証、TokenManager
│   │   ├── msg/                    # メッセージ型定義
│   │   ├── task/                   # TaskRepository、TaskReceiver
│   │   ├── queue/                  # QueueRepository
│   │   ├── provider/               # ProviderRepository
│   │   ├── storage/                # SecureStorage（expect/actual）
│   │   └── ui/                     # Compose UI
│   ├── androidMain/kotlin/         # Android固有実装
│   │   ├── connection/             # Ktor OkHttp WebSocket
│   │   ├── auth/                   # AppAuth for Android
│   │   ├── storage/                # EncryptedSharedPreferences
│   │   └── MainActivity.kt
│   └── iosMain/kotlin/             # iOS固有実装
│       ├── connection/             # Ktor Darwin WebSocket
│       ├── auth/                   # AppAuth for iOS (未実装)
│       └── storage/                # NSUserDefaults
└── build.gradle.kts
```

## 主要機能

### 実装済み

- ✅ **WebSocket双方向通信**: Ktor Clientによる接続管理（Android: OkHttp、iOS: Darwin）
- ✅ **二重トークン認証**: userToken（Keycloak OIDC）+ serverToken（`/api/token`エンドポイント）
- ✅ **メッセージ型定義**: ktcl-frontと互換性のあるメッセージ型（auth、task、queue、provider）
- ✅ **Repository パターン**: StateFlowベースのローカルステート管理
- ✅ **TaskReceiver**: WebSocketメッセージ受信・デシリアライズ・Repository更新
- ✅ **SecureStorage**: トークン永続化（Android: EncryptedSharedPreferences、iOS: NSUserDefaults）

### 未実装

- ⚠️ **iOS OIDC認証**: AppAuth for iOSの実装（現在スタブ）
- ⚠️ **UI画面**: LoginScreen、QueueCreateScreen、TaskListScreen等
- ⚠️ **Navigation**: 画面遷移ロジック
- ⚠️ **ViewModel**: StateFlowとCompose統合

## ビルド方法

### 前提条件

- JDK 17以上
- Kotlin 2.3.0
- Android SDK (Android向けビルド時)
- Xcode (iOS向けビルド時)

### Android向けビルド

```bash
# ANDROID_HOME環境変数を設定
export ANDROID_HOME=/path/to/android/sdk

# ビルド
./gradlew :ktcl-front-mobile:assembleDebug
```

### iOS向けビルド

⚠️ **現在、iOS向けビルドは依存関係の問題により失敗します。** 詳細は「既知の課題」セクションを参照。

```bash
# iOSフレームワークビルド（現在エラー）
./gradlew :ktcl-front-mobile:linkDebugFrameworkIosArm64
```

## 認証フロー

ktcl-frontの認証フローを再現：

```
1. LoginScreen表示
   ↓
2. OIDC認証（Keycloak）
   - Authorization Code Flow with PKCE
   - Redirect URI: net.kigawa.keruta.mobile:/oauth2redirect
   ↓
3. userToken取得
   ↓
4. POST /api/token {userToken}
   ↓
5. serverToken取得
   ↓
6. SecureStorageに両トークンを保存
   ↓
7. WebSocket接続
   ↓
8. ServerAuthRequestMsg送信 {userToken, serverToken}
   ↓
9. ClientAuthSuccessMsg受信
   ↓
10. 認証完了 → QueueCreateScreenへ遷移
```

## メッセージフロー

### タスク作成

```kotlin
// 送信
val msg = ServerTaskCreateMsg(
    queueId = 1,
    title = "タスクタイトル",
    description = "タスク説明"
)
websocket.send(Json.encodeToString(msg))

// 受信
TaskReceiver → ClientTaskCreatedMsg → TaskRepository.addTask()
```

### タスク一覧取得

```kotlin
// 送信
websocket.send(Json.encodeToString(ServerTaskListMsg(queueId = 1)))

// 受信
ClientTaskListedMsg → TaskRepository.updateTasks(tasks)
```

## 設定

`MobileConfig.kt`:

```kotlin
data class MobileConfig(
    val websocketUrl: String,           // ws://localhost:8080/ws/ktcp
    val keycloakUrl: String,            // https://user.kigawa.net/
    val keycloakRealm: String,          // develop
    val keycloakClientId: String,       // keruta
    val apiBaseUrl: String,             // http://localhost:5173/
    val ktseAudience: String            // keruta
)
```

## 既知の課題

### 1. iOSビルドエラー（Compose Multiplatform依存関係）

**問題**: Compose Multiplatform 1.10.0が要求するandroidxライブラリのiOS版が見つからない

```
Could not find androidx.lifecycle:lifecycle-viewmodel:2.9.4.
Could not find androidx.savedstate:savedstate:1.3.3.
```

**原因**: Compose Multiplatform 1.10.0の推移的依存関係として要求されるandroidxライブラリ（lifecycle、savedstate等）のiOS版がまだMaven Centralに公開されていない。

**回避策**:
- navigation-composeとlifecycle-viewmodel-composeを一時的にコメントアウト（`compose-mobile.gradle.kts:27-28`）
- ただし、Compose UI自体がlifecycleに依存しているため、完全な解決には至っていない

**恒久的な解決策**:
1. Compose Multiplatformの次期バージョンを待つ
2. androidxライブラリのiOS版がMaven Centralに公開されるまで待つ
3. 依存関係の解決戦略をカスタマイズ（gradle resolutionStrategy）

### 2. Android SDKパス未設定

**問題**: `ANDROID_HOME`環境変数が未設定のため、Androidビルドが失敗

**解決策**:
```bash
export ANDROID_HOME=/path/to/android/sdk
```

または`local.properties`に追加：
```
sdk.dir=/path/to/android/sdk
```

### 3. iOS OIDC認証未実装

**問題**: `OidcAuthManager.ios.kt`がスタブ実装

**TODO**:
- AppAuth for iOSライブラリの統合（CocoaPods）
- Authorization Code Flow with PKCEの実装
- Keychainへのトークン保存

## 開発ロードマップ

### Phase 1: ビルド環境修正 ✅
- [x] buildSrcにCompose Multiplatformプラグイン追加
- [x] compose-mobile.gradle.kts作成
- [x] iOSターゲットをkodel、ktcpに追加

### Phase 2: WebSocket接続 ✅
- [x] expect/actual WebSocket抽象化
- [x] Android: Ktor OkHttp実装
- [x] iOS: Ktor Darwin実装

### Phase 3: 認証 ✅（一部）
- [x] SecureStorage実装
- [x] TokenManager実装
- [ ] iOS OIDC認証実装

### Phase 4: UI実装 ⏳
- [ ] LoginScreen
- [ ] QueueCreateScreen
- [ ] QueueDetailScreen（タスク一覧）
- [ ] TaskCreateScreen
- [ ] ProviderListScreen
- [ ] Navigation統合

### Phase 5: iOSビルド修正 ⏳
- [ ] Compose Multiplatform依存関係解決
- [ ] iOSフレームワークビルド成功
- [ ] Xcodeプロジェクト生成

## 参考資料

- [ktcl-front（React版）](/ktcl-front/)
- [KTCP Protocol Documentation](/doc/architecture.md)
- [Authentication Documentation](/doc/authentication.md)
- [Compose Multiplatform公式](https://www.jetbrains.com/lp/compose-multiplatform/)
