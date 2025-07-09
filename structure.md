# project structure

* /.github/workflows - GitHub Actions ワークフロー設定
* /api - APIサーバーのソースコード
* /core - コアドメインとユースケース
* /infra - インフラストラクチャ層の実装
  * /security - セキュリティ関連の実装
    * /config - セキュリティ設定（認証、エラーハンドリングなど）
* /keruta-agent - エージェント関連のコード
* /keruta-doc - ドキュメント (https://github.com/kigawa-net/keruta-doc に移行中)
  * /common/apiSpec - 自動生成されたOpenAPI仕様書 (GitHub Actionsによって自動的に生成・プッシュ)
  * /keruta/misc - その他の詳細ドキュメント
    * /error_handling.md - エラーハンドリングの詳細ドキュメント
    * /logging.md - ログ設定の詳細ドキュメント
* /kigawa-net-k8s - Kubernetes関連の設定
