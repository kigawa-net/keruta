# Todo List

* 実行したタスクはリストから削除する

## 未着手

* ドキュメント作成
* sessionからメタデータを削除
* ステータスはユーザーから変更できないようにする
* 2025-07-28 02:54:24.597  INFO [keruta] [] [] [http-nio-8080-exec-1] [o.s.w.s.DispatcherServlet:532] - Initializing Servlet 'dispatcherServlet'
  2025-07-28 02:54:24.598  INFO [keruta] [] [] [http-nio-8080-exec-1] [o.s.w.s.DispatcherServlet:554] - Completed initialization in 1 ms
  2025-07-28 02:54:24.602  WARN [keruta] [] [] [http-nio-8080-exec-1] [o.s.w.s.h.HandlerMappingIntrospector:454] - Cache miss for REQUEST dispatch to '/api/v1/health' (previous null). Performing MatchableHandlerMapping lookup. This is logged once only at WARN level, and every time at TRACE.
  2025-07-28 02:54:35.617  INFO [keruta] [] [] [http-nio-8080-exec-5] [n.k.k.a.c.c.CoderController:25] - Fetching Coder templates from Coder server
  2025-07-28 02:54:35.618  INFO [keruta] [] [] [http-nio-8080-exec-5] [n.k.k.c.u.w.WorkspaceServiceImpl:356] - Fetching Coder templates from Coder server
  2025-07-28 02:54:35.618  INFO [keruta] [] [] [http-nio-8080-exec-5] [n.k.k.c.u.w.WorkspaceOrchestrator:212] - Fetching Coder templates via executor service
  2025-07-28 02:54:35.618  WARN [keruta] [] [] [http-nio-8080-exec-5] [n.k.k.c.u.w.WorkspaceOrchestrator:217] - ExecutorClient is not available - returning empty list
* クロスオリジン要求をブロックしました: 同一生成元ポリシーにより、https://static.cloudflareinsights.com/beacon.min.js/vcd15cbe7772f49c399c6a5babf22c1241717689176015 にあるリモートリソースの読み込みは拒否されます (理由: CORS 要求が成功しなかった)。ステータスコード: (null)
* integrity 属性内の “sha512” ハッシュが “https://static.cloudflareinsights.com/beacon.min.js/vcd15cbe7772f49c399c6a5babf22c1241717689176015” の subresource のコンテンツと一致しません。計算されたハッシュ値は “z4PhNX7vuL3xVChQ1m2AB9Yg5AULVxXcg/SpIdNs6c5H0NE8XYXysP+DGNKHfuwvY7kxvUdBeoGlODJ6+SfaPg==” です。 tasks
* XHRGET
  https://keruta-api.kigawa.net/api/v1/tasks
  [HTTP/3 500  338ms]

* タスク一覧の取得に失敗しました: ApiError: API request failed:
  a https://keruta.kigawa.net/build/_shared/chunk-Y5F5OAQ6.js:1
  r https://keruta.kigawa.net/build/_shared/chunk-Y5F5OAQ6.js:1
  l https://keruta.kigawa.net/build/routes/tasks._index-RW7BZWOE.js:1
  x https://keruta.kigawa.net/build/routes/tasks._index-RW7BZWOE.js:1
  Xa https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  $n https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  Z0 https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  au https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  tu https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  lf https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  n https://keruta.kigawa.net/build/_shared/chunk-Q3IECNXJ.js:1
  sf https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  n https://keruta.kigawa.net/build/_shared/chunk-Q3IECNXJ.js:1
  pm https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:2
  n https://keruta.kigawa.net/build/_shared/chunk-Q3IECNXJ.js:1
  vm https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  n https://keruta.kigawa.net/build/_shared/chunk-Q3IECNXJ.js:1
  Mr https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  m https://keruta.kigawa.net/build/_shared/chunk-Q3IECNXJ.js:1
  <anonymous> https://keruta.kigawa.net/build/_shared/chunk-BW2ECPS6.js:9
  tasks._index-RW7BZWOE.js:1:1578
* 各モジュールのアーキテクチャをシンプルにする


## 継続中のタスク

### 高優先度

* **keruta-executor機能強化**
    * タスク実行エラーハンドリング改善
    * ログストリーミング機能統合
    * リトライ機能とフェイルオーバー
* **ワークスペース機能完成**
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

### ⚠️ 部分更新必要

* CLAUDE.md - keruta-executorの最新機能を反映させる必要あり
* keruta-doc/ - 独立リポジトリに移行中のため、最新情報の同期が必要
