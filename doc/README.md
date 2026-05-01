# keruta ドキュメント

このディレクトリにはプロジェクト全体の技術ドキュメントが含まれています。

## ドキュメント一覧

| ファイル | 内容 |
|---|---|
| [architecture.md](architecture.md) | コアアーキテクチャパターン（Entrypoint、Res\<T,E\>、シリアライゼーション、エラー階層） |
| [authentication.md](authentication.md) | 二重トークン認証フロー（OIDC + プロバイダーJWT）、JWT検証設定、セッション管理 |
| [database.md](database.md) | DBスキーマ、Flywayマイグレーション、HikariCP設定、永続化抽象化 |
| [development.md](development.md) | ローカル開発環境セットアップ、IntelliJ設定、トラブルシューティング |
| [flows.md](flows.md) | 各機能の処理フロー詳細（WS ルーティング、認証、K8s ジョブ実行、KICP） |
| [kicp.md](kicp.md) | KICP（クロスドメインIDフェデレーション）プロトコル仕様、ユースケース |

## 概要

**keruta** は Kubernetes 上でタスクを管理・実行するシステムです。主要コンポーネント:

- **ktse** — タスクサーバー（Ktor + Exposed + MySQL、WebSocket経由でKTCPプロトコルを提供）
- **ktcl-k8s** — K8sクライアント（KTCPでタスク受信 → Kubernetes Jobとして実行）
- **ktcl-front** — フロントエンド（React + TypeScript + Keycloak.js）
- **kicp** — クロスドメインIDフェデレーションプロトコル

## 読み始めるなら

1. [architecture.md](architecture.md) — 設計パターンの全体像
2. [flows.md](flows.md) — 実際の処理フロー
3. [authentication.md](authentication.md) — 認証の詳細
4. [database.md](database.md) — データモデル

## モジュール構成詳細

- **kodel** — 共通ライブラリ（Res型、EntrypointDeferred、Kogger）
- **ktcp** — WebSocketプロトコル（model/client/server、Kotlin Multiplatform対応）
- **ktse** — KtorタスクサーバーWS（Exposed/Flyway/MySQL、二重トークン認証）
- **ktcl-k8s** — KTCPでタスク受信しKubernetes Jobとして実行
- **ktcl-front** — React+TypeScript+Vite+Keycloak.js
- **ktcl-claudecode** — Claude Code統合
- **ktcl-web** — （廃止）ウェブインターフェース