# keruta ドキュメント

このディレクトリにはプロジェクト全体の技術ドキュメントが含まれています。

## ドキュメント一覧

| ファイル | 内容 |
|---|---|
| [architecture.md](architecture.md) | コアアーキテクチャパターン（Entrypoint、Res\<T,E\>、シリアライゼーション、エラー階層） |
| [authentication.md](authentication.md) | 二重トークン認証フロー（OIDC + プロバイダーJWT）、JWT検証設定、セッション管理 |
| [ci-convention.md](ci-convention.md) | CI（GitHub Actions）作成・運用規約（ワークフロー命名、トリガー、可重用ワークフロー） |
| [convention.md](convention.md) | ドキュメント作成規約（ファイル命名、構成、記法、更新ルール） |
| [development-convention.md](development-convention.md) | 開発手順規約（ブランチ作成〜PRマージまでの標準フロー） |
| [pr-convention.md](pr-convention.md) | Pull Request 作成・運用規約（PR作成、レビュー、マージの手順） |
| [database.md](database.md) | DBスキーマ、Flywayマイグレーション、HikariCP設定、永続化抽象化 |
| [development.md](development.md) | ローカル開発環境セットアップ、IntelliJ設定、トラブルシューティング |
| [flows.md](flows.md) | 各機能の処理フロー詳細（WS ルーティング、認証、K8s ジョブ実行、KICP） |
| [kicp.md](kicp.md) | KICP（クロスドメインIDフェデレーションプロトコル）仕様、ユースケース |
| [kicl-web.md](kicl-web.md) | kicl-web（次世代フロントエンド）アーキテクチャ、技術スタック、ビルド手順 |

## 概要

**keruta** は Kubernetes 上でタスクを管理・実行するシステムです。主要コンポーネント:

- **ktse** — タスクサーバー（Ktor + Exposed + MySQL、WebSocket経由でKTCPプロトコルを提供）
- **ktcl-k8s** — K8sクライアント（KTCPでタスク受信 → Kubernetes Jobとして実行）
- **ktcl-front** — フロントエンド（React + TypeScript + Keycloak.js）
- **kicl-web** — 次世代フロントエンド（React Router v7 + Kotlin Multiplatform共有ロジック）
- **kicp** — クロスドメインIDフェデレーションプロトコル

## 読み始めるなら

1. [architecture.md](architecture.md) — 設計パターンの全体像
2. [flows.md](flows.md) — 実際の処理フロー
3. [authentication.md](authentication.md) — 認証の詳細
4. [database.md](database.md) — データモデル
5. [convention.md](convention.md) — ドキュメント作成規約

## リポジトリ規約

リポジトリ全体の開発規約はルートの [CONVENTION.md](../CONVENTION.md) を参照。
ブランチ戦略、コミットメッセージ、PRルール、CI/CDフローなどを記載。

## モジュール構成詳細

- **kodel** — 共通ライブラリ（Res型、EntrypointDeferred、Kogger）
- **ktcp** — WebSocketプロトコル（model/client/server、Kotlin Multiplatform対応）
- **ktse** — KtorタスクサーバーWS（Exposed/Flyway/MySQL、二重トークン認証）
- **ktcl-k8s** — KTCPでタスク受信しKubernetes Jobとして実行
- **ktcl-front** — React+TypeScript+Vite+Keycloak.js（既存フロントエンド）
- **kicl-web** — React Router v7 + Kotlin Multiplatform共有ロジック（次世代フロントエンド）
- **kicl** — Kotlin Multiplatformモジュール（domain/usecase）
- **ktcl-claudecode** — Claude Code統合