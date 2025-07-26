# Todo List

## 未着手

* XHRGET
  https://keruta-api.kigawa.net/api/v1/tasks
  [HTTP/3 500  75ms]

* タスク一覧の取得に失敗しました: ApiError: API request failed:
  r https://keruta.kigawa.net/build/_shared/chunk-3AUWNZR7.js:1
  d https://keruta.kigawa.net/build/_shared/chunk-3AUWNZR7.js:1
  l https://keruta.kigawa.net/build/routes/tasks._index-OLMDMDWE.js:1
  x https://keruta.kigawa.net/build/routes/tasks._index-OLMDMDWE.js:1
  Xa https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  $n https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  Z0 https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  au https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  tu https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  tasks._index-OLMDMDWE.js:1:1578

* Session status updated successfully: id=2c75eebb-c9bc-4cdd-bcc0-e6a3c166e9d6 status=ACTIVE
  2025-07-26 15:55:21.178 ERROR [keruta] [] [] [http-nio-8080-exec-11] [n.k.k.i.s.c.GlobalExceptionHandler:78] - 500 Internal Server Error: No static resource api/v1/tasks. - Path: /api/v1/tasks
  org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/v1/tasks.
  at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:585)
  at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52)
  at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)
  at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)

* 設定の初期化に失敗しました: 設定の検証に失敗: KERUTA_TASK_ID、KERUTA_SESSION_ID、またはKERUTA_WORKSPACE_ID のいずれかが設定されている必要があります
  coder@coder-5676ddd1-283c-41fd-b67f-1d8c6961b35e-7fc6b8898b-kt7bv:~$

## 完了済み

### ✅ セッション-ワークスペース統合 (2025-07-20)

- **Coderワークスペース状態に基づくセッションステータス管理の実装完了**
    - ワークスペース状態変更イベントの監視機能実装
    - ワークスペースステータスからセッションステータスへの自動マッピング機能実装
    - SessionEventListenerでのワークスペース状態監視実装
    - SessionWorkspaceStatusSyncServiceによる定期同期実装
    - CoderWorkspaceMonitoringServiceによる外部API監視実装
    - session1つに付きworkspace一つの1:1関係実装
    - FAILED状態ワークスペースの自動回復機能実装

### ✅ 技術的問題修正 (2025-07-20)

- SpringのCGLIBプロキシ対応（Kotlinクラスのopen化）
- Logger NullPointerException修正
- ktlintコードスタイル違反修正
- デフォルトテンプレート重複問題修正
- Coder APIユーザー認証問題修正
- ワークスペース削除・再作成タイミング問題修正
- **Coderワークスペース名バリデーション対応**
    - 日本語セッション名をCoder互換の形式に自動正規化
    - ワークスペース名の英数字・ハイフン制限への対応
    - SessionEventListenerでのワークスペース名正規化機能実装

## 継続中のタスク

### 高優先度

* **keruta-executor機能強化**
    * タスク実行エラーハンドリング改善
    * ログストリーミング機能統合
    * リトライ機能とフェイルオーバー
* **ワークスペース機能完成**
    * WorkspaceKubernetesHandlerの実装完了（現在TODO）
    * 実際Kubernetesリソース（Pod、Service、Ingress）の作成機能
    * ワークスペースとタスク実行の統合フロー
* **テンプレート管理機能**
    * デフォルトテンプレートの作成と管理
    * カスタムテンプレートの作成・編集機能
    * テンプレートパラメータの動的検証機能

### 中優先度

* **管理パネル機能拡張**
    * リポジトリ管理画面の実装
    * Kubernetes設定管理画面の実装
    * エージェント管理画面の実装
    * セッション管理UIの改善（リアルタイム状態更新）
    * ワークスペース管理UIの改善（ログ表示、リソース情報）
* **コード品質向上**
    * ktlintルールの全面有効化
    * テストカバレッジの向上（keruta-executor、keruta-agent）
    * Goエージェントのテスト強化

### 低優先度（将来の改善）

* **高度なワークスペース管理**
    * ワークスペースのファイル管理API
    * ワークスペース内リアルタイムログストリーミング
    * ワークスペースのスナップショット機能
* **監視とアラート**
    * FAILEDワークスペースの自動クリーンアップタスク
    * ワークスペース状態同期の監視・アラート機能
    * Coder API通信の冗長化・ヘルスチェック
    * セッション-ワークスペース関係の詳細ログ・デバッグ機能
* **パフォーマンス最適化**
    * データベースインデックスの最適化
    * キャッシュ機能の導入（Redis等）
    * 非同期処理の強化

## ドキュメント更新状況

### ✅ 更新完了 (2025-07-22)

* README.md - 最新のアーキテクチャと技術スタックに更新
* structure.md - コードベース構造とコンポーネント情報を最新化
* todo.md - 開発タスクの優先度と進捗状況を整理

### ⚠️ 部分更新必要

* CLAUDE.md - keruta-executorの最新機能を反映させる必要あり
* keruta-doc/ - 独立リポジトリに移行中のため、最新情報の同期が必要

## アーキテクチャ分析結果

### 現在の状況

Kerutaは成熟したKubernetes-nativeタスク実行システムとして発展しており、以下の特徴を持つ：

1. **マルチコンポーネント構成**: API Server、Agent、Executor、Admin Panel の4つの主要コンポーネント
2. **完全統合されたワークスペース管理**: セッションと1対1で管理されるCoderワークスペース
3. **高度な状態同期**: 2分間隔でのワークスペース状態監視と自動同期
4. **国際化対応**: 日本語セッション名の自動正規化機能
5. **堅牢な技術スタック**: Spring Boot + Kotlin、Go、TypeScript + Remix

### 技術的成果

* Spring CGLIBプロキシ対応によるKotlin統合の完成
* Coderワークスペース名バリデーション対応
* セッション-ワークスペース1対1関係の実装
* 定期的な状態監視システムの構築
* マルチモジュールGradle構成の最適化
