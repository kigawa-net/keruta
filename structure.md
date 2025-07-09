# project structure

* /.github/workflows - GitHub Actions ワークフロー設定
* /api - APIサーバーのソースコード
* /core - コアドメインとユースケース
* /infra - インフラストラクチャ層の実装
* /keruta-agent - エージェント関連のコード
* /keruta-doc - ドキュメント (https://github.com/kigawa-net/keruta-doc に移行中)
  * /common/apiSpec - 自動生成されたOpenAPI仕様書 (GitHub Actionsによって自動的に生成・プッシュ)
* /kigawa-net-k8s - Kubernetes関連の設定
