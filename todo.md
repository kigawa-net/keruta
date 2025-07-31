# Todo List

* 完了したタスクはリストから削除する

## 未着手

```log
 null). Performing MatchableHandlerMapping lookup. This is logged once only at WARN level, and every time at TRACE.
2025-07-31 14:01:53.613  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:02:53.614  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:03:53.615  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:04:53.615  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:05:53.613  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:317] - Service not yet initialized, skipping running tasks monitoring
2025-07-31 14:05:53.616  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:06:53.617  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:07:53.617  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
2025-07-31 14:08:51.080  INFO [keruta] [] [] [http-nio-8080-exec-16] [n.k.k.a.c.c.CoderController:25] - Fetching Coder templates from Coder server
2025-07-31 14:08:51.081  INFO [keruta] [] [] [http-nio-8080-exec-16] [n.k.k.c.u.w.WorkspaceServiceImpl:356] - Fetching Coder templates from Coder server
2025-07-31 14:08:51.081  INFO [keruta] [] [] [http-nio-8080-exec-16] [n.k.k.c.u.w.WorkspaceOrchestrator:212] - Fetching Coder templates via executor service
2025-07-31 14:08:51.081 ERROR [keruta] [] [] [http-nio-8080-exec-16] [n.k.k.c.u.w.WorkspaceOrchestrator:217] - ExecutorClient is not configured - returning empty template list (this is normal if keruta-executor is not running)
2025-07-31 14:08:53.618  WARN [keruta] [] [] [scheduling-1] [n.k.k.c.u.i.WorkspaceTaskExecutionService:285] - Service not yet initialized, skipping pending tasks processing
```
* /Users/ogawaatsuki/project/keruta/keruta-api/src/main/kotlin/net/kigawa/keruta/api/template/controller/TemplateController.kt
  250 // TODO: 実際のCoderサーバーとの連携を実装
  /Users/ogawaatsuki/project/keruta/keruta-api/src/main/kotlin/net/kigawa/keruta/core/usecase/session/SessionEventListener.kt
  166 // TODO: Consider implementing background cleanup task for failed workspaces
### 中優先度

* **管理パネル機能拡張**
    * ~~Kubernetes設定管理画面の実装~~ （削除済み）
    * エージェント管理画面の実装
    * セッション管理UIの改善（リアルタイム状態更新）
    * ワークスペース管理UIの改善（ログ表示、リソース情報）
* **コード品質向上**
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

### ⚠️ 部分更新必要

* keruta-doc/ - 独立リポジトリに移行中のため、最新情報の同期が必要

## アーキテクチャ改善履歴

### 2025-07-30

- モジュール構成の簡素化：6モジュール → 5モジュール（infra:core統合）
- Kubernetes機能の完全削除
- セキュリティ強化（ステータス変更制限）
- ドメインモデルのクリーンアップ（メタデータ削除）
- 依存関係の整理とビルド最適化
