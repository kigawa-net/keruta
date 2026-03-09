ktcl-webプロジェクトのKerutaTaskClientWeb.ktファイルにトップページを作成しました。ルーティングにGET "/"エンドポイントを追加し、"Welcome to Keruta Task Client Web"というテキストレスポンスを返すようにしました。JWT認証のブロックはそのまま残しています。

さらに、ログイン機能を追加しました。POST "/login"エンドポイントを追加し、ユーザー名とパスワードを受け取り、仮の認証（admin/password）でJWTトークンを生成して返します。ContentNegotiationをインストールし、JSONシリアライズを有効にしました。buildSrcのktcl-web.gradle.ktsにserializeプラグインを追加しました。