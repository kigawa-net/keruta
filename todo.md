# Todo List

* 実行したタスクはリストから削除する

## ✅ 完了済み（2025-07-30）

* ✅ NullPointerException修正 - WorkspaceTaskExecutionServiceのlogger初期化をcompanion objectに移動
* ✅ sessionからメタデータを削除 - Session関連のすべてのクラスからmetadataフィールドを削除
* ✅ sessionのステータスはユーザーから変更できないようにする - API経由での直接変更を403で拒否
* ✅ k8s機能削除 - Kubernetes関連のコード、依存関係を全て削除し、汎用的なリソース名に変更
* ✅ 各モジュールのアーキテクチャをシンプルにする - infra:coreをinfra:appに統合、重複設定の統合
* ✅ ドキュメント作成 - CLAUDE.mdを最新の変更内容で更新

## ✅ 追加完了済み（2025-07-30）

* ✅ WorkspaceTaskExecutionServiceのNullPointerException修正（第1回）- private → protectedに変更してSpringプロキシアクセス問題を解決
* ✅ WorkspaceOrchestratorのExecutorClient警告改善 - ログレベルをWARN → DEBUGに変更し、より明確なメッセージに更新
* ✅ WorkspaceTaskExecutionServiceのNullPointerException修正（第2回）- constructor injectionから@Autowiredに変更してCGLIBプロキシ問題を完全解決

## 未着手
なし

## 継続中のタスク

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
