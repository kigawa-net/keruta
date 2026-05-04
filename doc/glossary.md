# 用語集

keruta プロジェクトで使用される主要な用語を定義する。

---

## 1. プロジェクトとモジュール

| 用語 | 説明 |
|------|------|
| keruta | 本プロジェクト全体の名称 |
| kodel | 共通ライブラリ。Res型、EntrypointDeferred、Kogger などを提供 |
| ktcp | Keruta Task Client Protocol。WebSocket + HTTP を用いた本プロジェクト独自のタスク管理プロトコル。Kotlin Multiplatform 対応 |
| ktse | Ktor タスクサーバー。Exposed/Flyway/MySQL を使用、二重トークン認証を実装 |
| ktcl-k8s | KTCP でタスクを受信し、Kubernetes Job として実行するクライアント |
| ktcl-front | 既存フロントエンド。React + Keycloak.js を使用 |
| kicl-web | 次世代フロントエンド。React Router v7 + Kotlin Multiplatform 共有ロジック |
| kicl | Kotlin Multiplatform モジュール。domain/usecase 層を含む |
| ktcl-claudecode | Claude Code 統合モジュール |
| kise | Keruta ID Server（計画中）。統合認証基盤として機能 |
| kicp | Keruta ID Client Protocol（Kigawa Identity Cross-domain Protocol）。分散型ID（Decentralized Identity）を利用したクロスドメインIDフェデレーションプロトコル |

---

## 2. アーキテクチャと設計パターン

| 用語 | 説明 |
|------|------|
| Entrypoint パターン | 型安全な双方向メッセージルーティングの中核設計パターン |
| Entrypoint | メッセージ（Arg）を受け取り、処理を実行するインターフェース。`Entrypoint<C, A>` 形式 |
| Entrypoint Group | 受信メッセージを 14 種類に分類し、対応するエントリーポイントにルーティングする仕組み |
| Res\<T, E\> | 例外を使用しない関数型エラーハンドリングパターン。`Ok<T>` または `Err<E>` の結果を返す |
| EntrypointDeferred | 非同期処理の結果を表す型。`Res<T, E>` を含む |
| Kotlin Multiplatform (KMP) | JVM、JS、iOS など複数プラットフォームで動作する Kotlin コードを書く仕組み |
| DI パターン | 依存性注入。本プロジェクトでは DI フレームワークを使用せず、手動 Factory クラスで実装 |
| 純粋関数 | 同じ入力に対して常に同じ出力を返し、副作用を持たない関数。本プロジェクトで優先的に使用 |
| SOLID 原則 | オブジェクト指向設計の5つの原則。本プロジェクトで遵守 |

---

## 3. プロトコルと通信

| 用語 | 説明 |
|------|------|
| KTCP | Keruta Task Client Protocol。WebSocket + HTTP を用いた本プロジェクト独自のタスク管理プロトコル。Kotlin Multiplatform 対応 |
| IdP Client | Identity Provider Client。OIDC 等の認証プロバイダーと通信するクライアント |
| KTCP Client | KTCP プロトコルを使用してタスクサーバー（ktse）と通信するクライアント（例: ktcl-k8s） |
| KICP Client | KICP プロトコルを使用してクロスドメイン ID フェデレーションを行うクライアント（例: clientA、clientB） |
| WebSocket | 全二重通信を提供するプロトコル。KTCP の基盤 |
| ClientMsg | クライアントから送信されるメッセージの sealed インターフェース。ポリモーフィックシリアライズに対応 |
| ServerMsg | サーバーから送信されるメッセージの sealed インターフェース。ポリモーフィックシリアライズに対応 |
| ReceiveUnknownArg | 受信メッセージの型を判定するためのクラス |
| SendXxxArg | サーバーからクライアントへ送信されるメッセージのパラメータ型 |
| KerutaSerializer | シリアライゼーションのインターフェース |
| JsonKerutaSerializer | kotlinx.serialization を使用した JSON シリアライザ実装 |
| ClientMsgTypeSerializer | ClientMsg の型判別子シリアライザ |
| ServerMsgTypeSerializer | ServerMsg の型判別子シリアライザ |

---

## 4. 認証とセキュリティ

| 用語 | 説明 |
|------|------|
| OIDC | OpenID Connect。標準的な認証プロトコル |
| IdP | Identity Provider。認証提供者（Auth0、Keycloak など） |
| JWT | JSON Web Token。認証や情報交換に使用されるトークン形式 |
| JWK | JSON Web Key。公開鍵を JSON 形式で表現したもの |
| JWKS | JWK Set。複数の JWK をまとめたもの |
| JwksUrl | JWKS を取得するための URL |
| 二重トークン認証 | ユーザー JWT（OIDC）とプロバイダー JWT の両方を検証する ktse の認証方式 |
| User Token | ユーザーの IdP が発行する JWT |
| Provider Token | プロバイダーの IdP が発行する JWT |
| Register Token | KICP で使用されるクロスドメイン登録用トークン。1回使用で削除される |
| IdentityId | OIDC の `issuer:subject` から生成される一意のユーザー識別子 |
| RegisterId | 登録元の IdentityId。KICP で使用 |
| Session | 認証済みユーザーのセッション。状態を管理 |
| AuthenticatedSession | 認証完了後に確立されるセッションオブジェクト |
| OidcSession | ktcl-k8s で使用される OIDC ベースのセッション |
| PKCE | Proof Key for Code Exchange。認可コードインターセプション攻撃を防ぐセキュリティ機構 |
| code_verifier | PKCE で使用される 43-128 文字のランダム文字列 |
| code_challenge | `BASE64URL(SHA256(verifier))` で生成される PKCE チャレンジ文字列 |
| RS256 | RSA 2048-bit 署名アルゴリズム。JWT の署名に使用 |
| LRU Cache | Least Recently Used キャッシュ。JWKS のキャッシュに使用（最大8発行者） |

---

## 5. データベース

| 用語 | 説明 |
|------|------|
| Exposed | Kotlin の SQL フレームワーク。データベースアクセスに使用 |
| Flyway | データベースマイグレーションツール。起動時に自動実行 |
| MySQL | リレーショナルデータベース。keruta で使用（バージョン 9.7） |
| HikariCP | データベース接続プール。最大プールサイズは10 |
| PersisterSession | データ永続化のインターフェース。`verify()` メソッドを提供 |
| AuthenticatedPersisterSession | 認証後の永続化セッションインターフェース。タスク作成などが可能 |
| DbPersisterSession | PersisterSession の ktse 実装 |
| DAO | Data Access Object。データベースアクセスを抽象化するオブジェクト（例: UserDao） |
| Flyway マイグレーション | `V001__xxx.sql` 形式のマイグレーションファイル。`ktse/src/main/resources/db/migration/` に配置 |
| テーブル | user、user_idp、provider、queue、queue_user、task などのデータベース表 |

---

## 6. 開発と CI/CD

| 用語 | 説明 |
|------|------|
| Gradle | ビルドツール。バージョン 9.5.0 |
| buildSrc | Gradle のビルドロジックを集約するディレクトリ。バージョンやプラグインを一元管理 |
| Version.kt | `buildSrc/src/main/kotlin/` 内の依存バージョン一元管理ファイル |
| Conventional Commits | コミットメッセージの規約。`type(scope): 説明` 形式 |
| PR | Pull Request。コードのマージリクエスト。`develop` ブランチをベースに作成 |
| CI/CD | 継続的インテグレーション/継続的デプロイ。GitHub Actions を使用 |
| Renovate | 依存パッケージの自動更新ツール。毎晩19:00に実行 |
| Harbor | コンテナイメージレジストリ（`harbor.kigawa.net`） |
| ArgoCD | Kubernetes の継続的デプロイツール。イメージ更新を自動検知してデプロイ |
| Docker | コンテナ化ツール。各モジュールのイメージをビルド |

---

## 7. エラー型

| 用語 | 説明 |
|------|------|
| KtcpErr | KTCP プロトコルエラーの基底インターフェース |
| KtcpServerErr | KTCP サーバー関連エラー |
| UnauthenticatedErr | 未認証エラー。認証前の操作で発生 |
| VerifyErr | トークン検証エラー基底 |
| VerifyFailErr | トークン検証失敗エラー |
| VerifyUnsupportedKeyErr | 未対応の署名アルゴリズムエラー |
| DeserializeErr | メッセージデシリアライズエラー |
| ResponseErr | レスポンス処理エラー |
| EntrypointNotFoundErr | 対応するエントリーポイントが見つからないエラー |
| IllegalFormatDeserializeErr | 不正形式のデシリアライズエラー |
| InvalidTypeDeserializeErr | 無効な型のデシリアライズエラー |
| KicpErr | KICP プロトコルエラーの基底インターフェース |
| JwksFetchErr | JWKS 取得失敗エラー |
| TokenVerificationErr | JWT 検証失敗エラー |
| RegisterTokenNotFoundErr | 登録トークンが未発見または使用済みエラー |
| RegisterTokenExpiredErr | 登録トークン期限切れエラー |
| PeerVerificationErr | ピアサーバー検証失敗エラー |
| BackendErr | KTSE バックエンドエラー基底 |
| NoSingleRecordErr | 単一レコードが見つからないエラー |
| MultipleRecordErr | 複数レコードが存在するエラー |
| KiseErr | kise モジュールエラー基底 |
| InvalidTokenErr | 無効なトークンエラー |
| TokenExpiredErr | トークン期限切れエラー |
| ProviderNotFoundErr | プロバイダーが見つからないエラー |
| SessionNotFoundErr | セッションが見つからないエラー |
| UserNotFoundErr | ユーザーが見つからないエラー |
| K8sErr | Kubernetes 関連エラー基底 |
| JobWatchErr | K8s Job 監視エラー |

---

## 8. その他

| 用語 | 説明 |
|------|------|
| Kogger | kodel で提供されるログユーティリティ |
| Coroutine | Kotlin の非同期処理フレームワーク。WebSocket 通信などで使用 |
| Fritz2 | Kotlin/JS のリアクティブフレームワーク |
| React Router v7 | フロントエンドのルーティングフレームワーク（kicl-web で使用） |
| Keycloak.js | Keycloak の JavaScript クライアント（ktcl-front で使用） |
| Claude Code | Anthropic のコード生成ツール。ktcl-claudecode で統合 |
