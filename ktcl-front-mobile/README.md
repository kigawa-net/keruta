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

## 参考資料

- [ktcl-front（React版）](../ktcl-front)
- [KTCP Protocol Documentation](../doc/architecture.md)
- [Authentication Documentation](../doc/authentication.md)
- [Compose Multiplatform公式](https://www.jetbrains.com/lp/compose-multiplatform/)
