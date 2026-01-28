# Development Documentation

このドキュメントは、Keruta開発環境のセットアップと詳細を説明します。

## ローカル開発環境セットアップ

### 前提条件

- JDK 17以上
- Docker（データベース、ZooKeeper用）
- Git

### データベースセットアップ

#### Docker Composeを使用

```bash
# MySQLを起動
docker-compose -f compose.test.yml up -d mysql

# または手動でMySQLコンテナを起動
docker run -d \
  --name keruta-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=keruta \
  -e MYSQL_USER=keruta \
  -e MYSQL_PASSWORD=keruta \
  mysql:8.0
```

#### 環境変数設定

```bash
export DB_JDBC_URL="jdbc:mysql://localhost:3306/keruta"
export DB_USERNAME="keruta"
export DB_PASSWORD="keruta"
```

### ZooKeeperセットアップ（オプション）

現在、ZooKeeperはほぼ使用されていませんが、将来的な機能のために起動可能です。

```bash
# ZooKeeperを起動
docker run -d \
  --name zookeeper \
  -p 2181:2181 \
  zookeeper

# 環境変数設定
export KTSE_ZK_HOST="localhost:2181"
```

### KTSE（サーバー）起動

```bash
# ビルドと起動
./gradlew :ktse:run

# または開発モードで起動（自動リロード）
./gradlew :ktse:run --continuous
```

**デフォルトポート:** 8080

### KTCL-Web（クライアント）起動

```bash
# ビルドと起動
./gradlew :ktcl-web:run
```

## 開発コマンド詳細

### ビルド関連

```bash
# 全モジュールビルド
./gradlew build

# 特定モジュールビルド
./gradlew :ktse:build
./gradlew :ktcp:server:build
./gradlew :ktcp:model:build

# クリーンビルド
./gradlew clean build

# 依存関係の更新
./gradlew dependencies --refresh-dependencies
```

### テスト関連

```bash
# 全テスト実行
./gradlew test

# 特定モジュールのテスト
./gradlew :ktcp:server:test
./gradlew :ktse:test

# 単一テストクラス実行
./gradlew :ktse:test --tests "ReceiveTaskCreateArgTest"

# 詳細出力でテスト実行
./gradlew test --info

# 失敗後も継続してテスト実行
./gradlew test --continue

# テストレポート確認
# build/reports/tests/test/index.html を開く
```

### コード品質

```bash
# コードフォーマット
./gradlew ktlintFormat

# コードスタイルチェック
./gradlew ktlintCheck

# フォーマットとチェックを連続実行
./gradlew ktlintFormat && ./gradlew ktlintCheck
```

### Gradle関連

```bash
# Gradleデーモン状態確認
./gradlew --status

# Gradleデーモン停止
./gradlew --stop

# ビルドキャッシュクリア
./gradlew cleanBuildCache

# タスク一覧表示
./gradlew tasks

# 特定モジュールのタスク一覧
./gradlew :ktse:tasks
```

## IDE設定

### IntelliJ IDEA

#### 推奨プラグイン
- Kotlin
- Gradle
- Database Navigator

#### プロジェクトインポート
1. File > Open > `keruta`ディレクトリを選択
2. Trust Projectを選択
3. Gradleの自動インポートを待つ

#### 実行設定
- Main class: `net.kigawa.keruta.ktse.KerutaTaskServerKt`
- Module: `keruta.ktse.main`
- Environment variables: `DB_JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD`

## トラブルシューティング

### ビルドエラー

#### Gradle build cache issues
```bash
./gradlew clean build
# または
rm -rf .gradle build
./gradlew build
```

#### ktlint failures
```bash
./gradlew ktlintFormat
./gradlew build
```

#### 依存関係の問題
```bash
./gradlew dependencies --refresh-dependencies
./gradlew clean build
```

### ランタイムエラー

#### データベース接続エラー
- MySQLコンテナが起動しているか確認: `docker ps`
- 環境変数が正しく設定されているか確認
- JDBCドライバーがクラスパスに含まれているか確認

#### ZooKeeper接続エラー
- ZooKeeperコンテナが起動しているか確認
- `KTSE_ZK_HOST`環境変数を確認
- 現在ZooKeeperはほぼ使用されていないため、起動不要

#### WebSocket接続エラー
- CORS設定を確認（開発環境では`anyHost()`を使用）
- クライアントとサーバーのポート番号を確認
- ブラウザのコンソールでエラーメッセージを確認

#### 認証エラー
- JWT設定を確認
- IdP設定（`application.yaml`）を確認
- トークンの有効期限を確認

### テストエラー

#### テストが失敗する
```bash
# テストをクリーン実行
./gradlew cleanTest test

# 詳細ログで実行
./gradlew test --info --stacktrace

# 特定のテストのみ実行
./gradlew :ktse:test --tests "SpecificTest"
```

#### テストデータベース
テスト用データベースを分離する場合：
```bash
export DB_JDBC_URL="jdbc:mysql://localhost:3306/keruta_test"
```

## デバッグ

### IntelliJ IDEAでデバッグ

1. ブレークポイントを設定
2. Debug実行設定を作成
3. Debugボタンでアプリケーション起動

### リモートデバッグ

```bash
# JVMデバッグオプション付きで起動
./gradlew :ktse:run --debug-jvm

# またはJVMオプションを直接指定
JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" \
  ./gradlew :ktse:run
```

IntelliJ IDEAで：
1. Run > Edit Configurations
2. + > Remote JVM Debug
3. Port: 5005
4. Start debugging

### ログレベル設定

`ktse/src/main/resources/logback.xml`で設定：

```xml
<logger name="net.kigawa.keruta" level="DEBUG"/>
```

## パフォーマンス

### Gradle Build Cache

```bash
# 設定キャッシュを有効化（初回は遅いが2回目以降高速化）
./gradlew build --configuration-cache
```

`gradle.properties`に追加：
```properties
org.gradle.configuration-cache=true
org.gradle.caching=true
```

### 並列ビルド

```properties
org.gradle.parallel=true
org.gradle.workers.max=4
```

## コーディング規約

### Kotlinスタイル

- ktlintの規則に従う
- `.editorconfig`の設定を尊重
- SOLID原則を適用
- 純粋関数を優先

### コミット前チェックリスト

1. `./gradlew ktlintFormat` - コードフォーマット
2. `./gradlew test` - テスト実行
3. `./gradlew build` - ビルド確認
4. コミットメッセージは明確に

### ブランチ戦略

- `main` - 安定版
- `features/*` - 新機能開発
- `fix/*` - バグ修正
- `refactor/*` - リファクタリング

## 開発ワークフロー

### 新機能開発

1. featureブランチ作成
   ```bash
   git checkout -b features/add-new-feature
   ```

2. 実装とテスト
   ```bash
   ./gradlew ktlintFormat
   ./gradlew test
   ```

3. コミットとプッシュ
   ```bash
   git add .
   git commit -m "Add new feature: ..."
   git push origin features/add-new-feature
   ```

4. プルリクエスト作成

### バグ修正

1. fixブランチ作成
   ```bash
   git checkout -b fix/fix-bug-description
   ```

2. 再現テストを追加
3. バグ修正
4. テスト通過確認
5. プルリクエスト作成

## デプロイ

### Dockerイメージビルド

```bash
# KTSEイメージビルド
docker build -t harbor.kigawa.net/library/keruta/ktse:latest -f ktse/Dockerfile .

# プッシュ
docker push harbor.kigawa.net/library/keruta/ktse:latest
```

### 環境変数（本番）

```bash
DB_JDBC_URL=jdbc:mysql://prod-db:3306/keruta
DB_USERNAME=keruta_prod
DB_PASSWORD=<secure-password>
KTSE_ZK_HOST=zk1:2181,zk2:2181,zk3:2181
```

## 参考資料

- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Ktor Documentation](https://ktor.io/docs/)
- [Exposed Documentation](https://github.com/JetBrains/Exposed/wiki)
- [Flyway Documentation](https://flywaydb.org/documentation/)