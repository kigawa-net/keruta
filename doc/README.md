# keruta ドキュメント

このディレクトリにはプロジェクト全体の技術ドキュメントが含まれています。

## ドキュメント構成

ドキュメントは以下4つのカテゴリに体系化されています：

### 📚 **domain/** — 用語・概念・プロトコル定義

システムの根本となる用語、概念、プロトコル仕様を定義。

| ファイル | 内容 |
|---|---|
| [glossary.md](domain/glossary.md) | Keruta プロジェクト全体の用語集・概念定義 |
| [kicp.md](domain/kicp.md) | KICP（クロスドメインIDフェデレーションプロトコル）仕様、ユースケース |

### 🔄 **usecase/** — フロー・処理ステップ・ユースケース

各機能がどのように動作するか、処理フローと実行ステップ。

| ファイル | 内容 |
|---|---|
| [flows.md](usecase/flows.md) | 各機能の処理フロー詳細（WS ルーティング、認証、K8s ジョブ実行、KICP） |
| [authentication.md](usecase/authentication.md) | 二重トークン認証フロー（OIDC + プロバイダーJWT）、JWT検証、セッション管理 |
| [kise-oidc-login.md](usecase/kise-oidc-login.md) | OIDC ログインフローの詳細ステップ |

### 🏗️ **infra/** — 実装詳細・システム構成・技術スタック

システムの実装方式、技術選択、インフラ構成。

| ファイル | 内容 |
|---|---|
| [architecture.md](infra/architecture.md) | コアアーキテクチャパターン、モジュール構成、依存関係、Mermaid図 |
| [database.md](infra/database.md) | DBスキーマ、Flywayマイグレーション、永続化抽象化 |
| [kicl-web.md](infra/kicl-web.md) | kicl-web（ユーザー管理UI）アーキテクチャ、技術スタック |
| [kise.md](infra/kise.md) | kise（IDサーバー）実装、認証基盤、KICP統合 |

### 📋 **convention/** — 規約・ルール・開発ガイドライン

開発時に守るべき規約、標準手順、ガイドライン。

| ファイル | 内容 |
|---|---|
| [general.md](convention/general.md) | ドキュメント作成規約（ファイル命名、構成、記法） |
| [setup.md](convention/setup.md) | 開発環境セットアップ、IntelliJ設定、初期化手順 |
| [development.md](convention/development.md) | 開発手順規約（ブランチ作成〜PRマージまでの標準フロー） |
| [ci.md](convention/ci.md) | CI（GitHub Actions）作成・運用規約（ワークフロー命名、トリガー） |
| [issue.md](convention/issue.md) | Issue 作成・運用規約（作業計画、タイトル、ラベル） |
| [pull-request.md](convention/pull-request.md) | PR 作成・運用規約（PR作成、レビュー、マージ） |

---

## 読み始めるなら

### 初回・システム理解

1. **[domain/glossary.md](domain/glossary.md)** — Keruta の主要概念・用語を学ぶ
2. **[infra/architecture.md](infra/architecture.md)** — システムの全体像と設計パターン
3. **[usecase/flows.md](usecase/flows.md)** — 実際の処理フロー

### 機能詳細

4. **[usecase/authentication.md](usecase/authentication.md)** — 認証・認可の詳細
5. **[domain/kicp.md](domain/kicp.md)** — クロスドメイン連携の仕様
6. **[infra/database.md](infra/database.md)** — データモデルとDB設計

### 開発作業

7. **[convention/setup.md](convention/setup.md)** — 開発環境セットアップ
8. **[convention/development.md](convention/development.md)** — 開発フロー
9. **[convention/pull-request.md](convention/pull-request.md)** — PRルール

---

## 概要

**keruta** は Kubernetes 上でタスクを管理・実行するシステムです。主要コンポーネント:

- **ktse** — タスクサーバー（Ktor + Exposed + MySQL、WebSocket経由でKTCPプロトコルを提供）
- **ktcl-k8s** — K8sクライアント（KTCPでタスク受信 → Kubernetes Jobとして実行）
- **ktcl-front** — フロントエンド（React + TypeScript + Keycloak.js、タスク管理UI）
- **kicl-web** — ユーザー管理UI（React Router v7 + Kotlin Multiplatform共有ロジック）
- **kise** — IDサーバー（KICP プロトコル実装、ユーザー管理・認証基盤）
- **kicp** — クロスドメインIDフェデレーションプロトコル（id連携）

---

## リポジトリ規約

リポジトリ全体の開発規約はルートの [CONVENTION.md](../CONVENTION.md) を参照。
ブランチ戦略、コミットメッセージ、PRルール、CI/CDフローなどを記載。

---

## モジュール構成詳細

| モジュール | 役割 |
|---|---|
| **kodel** | 共通ライブラリ（Res型、Entrypoint、Kogger） |
| **ktcp-sdk** | WebSocketプロトコル（KTCP）、Kotlin Multiplatform対応 |
| **kicp** | クロスドメインIDフェデレーションプロトコル仕様 |
| **kicl** | Kotlin Multiplatformライブラリ（Web向けドメイン・ユースケース） |
| **ktse** | KtorタスクサーバーWS（Exposed/Flyway/MySQL、二重トークン認証） |
| **ktcl-k8s** | KTCPでタスク受信しKubernetes Jobとして実行 |
| **ktcl-front** | React+TypeScript+Vite+Keycloak.js（タスク管理UI） |
| **kicl-web** | React Router v7 + KMP共有ロジック（ユーザー管理UI） |
| **kise** | IDサーバー（KICP実装、ユーザー・認証管理） |
| **ktcl-claudecode** | Claude Code統合クライアント |

---

## ナビゲーション

- **ドメイン知識** → [domain/](domain/)
- **処理フロー・シーケンス** → [usecase/](usecase/)
- **システム実装・技術スタック** → [infra/](infra/)
- **開発規約・ガイドライン** → [convention/](convention/)
- **開発計画・提案** → [plan/](plan/)
