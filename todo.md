# Todo List

* 実行したタスクはリストから削除する

## 未着手

* ドキュメント作成
* sessionからメタデータを削除
* sessionのステータスはユーザーから変更できないようにする
* 各モジュールのアーキテクチャをシンプルにする
* 2025-07-29 03:09:06.560  WARN [keruta] [] [] [http-nio-8080-exec-10] [n.k.k.c.u.w.WorkspaceOrchestrator:217] - ExecutorClient is not available - returning empty list
  2025-07-29 03:09:12.182 ERROR [keruta] [] [] [scheduling-1] [o.s.s.s.TaskUtils$LoggingErrorHandler:95] - Unexpected error occurred in scheduled task
  java.lang.NullPointerException: Cannot invoke "org.slf4j.Logger.debug(String)" because "this.logger" is null
  at net.kigawa.keruta.core.usecase.integration.WorkspaceTaskExecutionService.retryFailedTasks(WorkspaceTaskExecutionService.kt:332)
  at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(Unknown Source)
  at java.base/java.lang.reflect.Method.invoke(Unknown Source)
  at org.springframework.scheduling.support.ScheduledMethodRunnable.runInternal(ScheduledMethodRunnable.java:130)
  at org.springframework.scheduling.support.ScheduledMethodRunnable.lambda$run$2(ScheduledMethodRunnable.java:124)
  at io.micrometer.observation.Observation.observe(Observation.java:499)
  at org.springframework.scheduling.support.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:124)
  at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54)
  at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
  at java.base/java.util.concurrent.FutureTask.runAndReset(Unknown Source)
  at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
  at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
  at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
  at java.base/java.lang.Thread.run(Unknown Source)
  2025-07-29 03:09:12.272 ERROR [keruta] [] [] [scheduling-1] [o.s.s.s.TaskUtils$LoggingErrorHandler:95] - Unexpected error occurred in scheduled task
  java.lang.NullPointerException: Cannot invoke "org.slf4j.Logger.debug(String)" because "this.logger" is null
  at net.kigawa.keruta.core.usecase.integration.WorkspaceTaskExecutionService.monitorRunningTasks(WorkspaceTaskExecutionService.kt:292)
  at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(Unknown Source)
  at java.base/java.lang.reflect.Method.invoke(Unknown Source)
  at org.springframework.scheduling.support.ScheduledMethodRunnable.runInternal(ScheduledMethodRunnable.java:130)
  at org.springframework.scheduling.support.ScheduledMethodRunnable.lambda$run$2(ScheduledMethodRunnable.java:124)
  at io.micrometer.observation.Observation.observe(Observation.java:499)
  at org.springframework.scheduling.support.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:124)
  at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54)
  at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
  at java.base/java.util.concurrent.FutureTask.runAndReset(Unknown Source)
  at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
  at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
  at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
  at java.base/java.lang.Thread.run(Unknown Source)
  2025-07-29 03:09:12.951 ERROR [keruta] [] [] [scheduling-1] [o.s.s.s.TaskUtils$LoggingErrorHandler:95] - Unexpected error occurred in scheduled task
  java.lang.NullPointerException: Cannot invoke "org.slf4j.Logger.debug(String)" because "this.logger" is null
  at net.kigawa.keruta.core.usecase.integration.WorkspaceTaskExecutionService.processPendingTasks(WorkspaceTaskExecutionService.kt:265)
  at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(Unknown Source)
  at java.base/java.lang.reflect.Method.invoke(Unknown Source)
* k8s機能削除
* 2025-07-29 05:53:37.157  INFO [keruta] [] [] [http-nio-8080-exec-7] [n.k.k.a.c.c.CoderController:25] - Fetching Coder templates from Coder server
  2025-07-29 05:53:37.158  INFO [keruta] [] [] [http-nio-8080-exec-7] [n.k.k.c.u.w.WorkspaceServiceImpl:356] - Fetching Coder templates from Coder server
  2025-07-29 05:53:37.158  INFO [keruta] [] [] [http-nio-8080-exec-7] [n.k.k.c.u.w.WorkspaceOrchestrator:212] - Fetching Coder templates via executor service
  2025-07-29 05:53:37.158  WARN [keruta] [] [] [http-nio-8080-exec-7] [n.k.k.c.u.w.WorkspaceOrchestrator:217] - ExecutorClient is not available - returning empty list

## 継続中のタスク

### 中優先度

* **管理パネル機能拡張**
    * Kubernetes設定管理画面の実装
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

* CLAUDE.md - keruta-executorの最新機能を反映させる必要あり
* keruta-doc/ - 独立リポジトリに移行中のため、最新情報の同期が必要
