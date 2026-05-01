# Development

## ローカル開発環境セットアップ

### 前提条件

- JDK 17以上
- Docker

### DB起動

```bash
docker-compose -f compose.test.yml up -d mysql
```

環境変数（`application.yaml` または shell export）:

```
DB_JDBC_URL=jdbc:mysql://localhost:3306/keruta
DB_USERNAME=keruta
DB_PASSWORD=keruta
```

ZooKeeperは現在ほぼ未使用。必要な場合は `KTSE_ZK_HOST=localhost:2181` を設定。

### IntelliJ IDEA — ktse 実行設定

- Main class: `net.kigawa.keruta.ktse.KerutaTaskServerKt`
- Module: `keruta.ktse.main`
- 環境変数: `DB_JDBC_URL`、`DB_USERNAME`、`DB_PASSWORD`

デフォルトポート: 8080

## リモートデバッグ

```bash
./gradlew :ktse:run --debug-jvm   # ポート 5005 でリッスン
```

IntelliJ: Run → Edit Configurations → + → Remote JVM Debug → Port: 5005

## トラブルシューティング

| 問題 | 対処 |
|---|---|
| ビルドエラー（キャッシュ起因） | `./gradlew clean build` |
| ktlintエラー | `./gradlew ktlintFormat && ./gradlew build` |
| DB接続エラー | `docker ps` でコンテナ確認、環境変数を再確認 |
| テスト失敗 | `./gradlew cleanTest :ktse:test --info --stacktrace` |
