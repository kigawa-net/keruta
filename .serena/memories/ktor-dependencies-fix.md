ktcl-webモジュールのビルドエラー（ContentNegotiationとkotlinx.jsonのUnresolved reference）を修正するために、buildSrc/src/main/kotlin/ktor-server.gradle.ktsに以下の依存関係を追加した：
- implementation("io.ktor:ktor-server-content-negotiation")
- implementation("io.ktor:ktor-serialization-kotlinx-json")

これにより、Ktor 3.xのサーバー側Content NegotiationとJSONシリアライズが利用可能になった。