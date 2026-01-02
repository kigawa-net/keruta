ktcl-webモジュールにトップページとログインページのUIを追加しました。

変更点：
- buildSrc/src/main/kotlin/ktor-server.gradle.ktsにktor-server-html-builder依存関係を追加
- ktcl-web/src/main/kotlin/net/kigawa/keruta/ktcl/web/KerutaTaskClientWeb.ktを編集：
  - トップページ（"/"）をHTMLに変更し、ログインページへのリンクを追加
  - 新しいGET "/login"エンドポイントを追加し、ログインフォームを表示
  - 既存のPOST "/login"はAPIとして残し、フォームからの送信に対応

これにより、ユーザーはトップページからログインページに遷移できるようになりました。