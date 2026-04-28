# Processing Flows

各機能の処理フローを記述する。アーキテクチャパターンの説明は `architecture.md` を参照。

---

## 1. WebSocket メッセージルーティング（KTCP）

クライアントから受信した WebSocket フレームを型判定し、対応するエントリーポイントに渡す。

```
WebSocket フレーム受信 (Frame.Text)
  │
  ▼
ReceiveUnknownArg.fromFrame()
  ├─ Frame.Text 以外 → InvalidTypeDecodeFrameErr
  ├─ JSON パース失敗 → DeserializeDecodeFrameErr
  └─ "type" フィールド抽出 → ReceiveUnknownArg 生成
  │
  ▼
KtcpServerEntrypoints.access(unknownArg, ctx)
  │
  ├─ arg.tryToAuthRequest()      → ReceiveAuthRequestEntrypoint
  ├─ arg.tryToProviderIssueToken() → ReceiveProviderIssueTokenEntrypoint
  ├─ arg.tryToProviderComplete() → ReceiveProviderCompleteEntrypoint
  ├─ arg.tryToProviderDelete()   → ReceiveProviderDeleteEntrypoint
  ├─ arg.tryToQueueCreate/List/Show/Update/Delete
  ├─ arg.tryToTaskCreate/List/Show/Update/Move
  └─ すべて null → EntrypointNotFoundErr
  │
  ▼
EntrypointDeferred<Res<Unit, KtcpErr>> を返す
  ├─ Res.Ok  → 正常完了
  └─ Res.Err
     ├─ UnauthenticatedErr    → 警告ログ（切断しない）
     └─ その他 KtcpErr        → ClientGenericErrorMsg 送信
```

**セッションタイムアウト管理**（`KtcpSession`）
- フレーム受信のたびに `updateTimeout()` 呼び出し
- 1分間無受信で切断
- 30分の時間窓で3エラー超過でも切断

---

## 2. 二重トークン認証フロー（ktse）

ユーザーJWT（OIDC）とプロバイダーJWT の両方を検証して `AuthenticatedSession` を確立する。

### 2-1. 認証リクエスト処理

```
ServerAuthRequestMsg { userToken, serverToken }
  │
  ▼
Auth0AuthTokenDecoder.decodeAuthRequestMsg()
  ├─ JWT.decode(userToken)   → UnverifiedUserToken（署名未検証）
  ├─ JWT.decode(serverToken) → UnverifiedProviderToken（署名未検証）
  └─ UnverifiedAuthTokens 生成
  │
  ▼
VerifyTablesPersister.getVerifyTables(unverifiedTokens)
  ├─ issuer + subject でユーザー/IdP/プロバイダーをDB検索
  ├─ 全レコード存在 → verifyWithTable()
  └─ 未登録            → createUserAndVerify()
  │
  ▼
unverifiedTokens.verify(JwtVerifyValues)
  ├─ userToken:   Auth0 JWKS（OIDC Discovery で jwks_url 自動取得）で RSA256 検証
  │                issuer / audience / subject をチェック
  ├─ serverToken: KtseJwtVerifier の PEM 鍵で検証
  │                issuer / audience / subject をチェック
  └─ いずれか失敗 → VerifyFailErr / VerifyUnsupportedKeyErr
  │
  ▼
AuthenticatedSession 確立
  └─ KtcpSession.authenticatedSession.value に保存
     → 以降の全エントリーポイントで ctx.session.authenticated() から参照可能
```

### 2-2. プロバイダー登録フロー

```
(認証済み状態で)
ServerProviderIssueTokenMsg 受信
  └─ JwtVerifier.createToken(issuer, audience="provider_register", subject)
     → 1時間有効の登録用 JWT 生成
     → ClientProviderAddTokenMsg で返却

クライアントが外部プロバイダーに登録用 JWT を提示
  └─ プロバイダーが JWT を用いて自身のトークン生成

ServerProviderCompleteMsg { registerToken, userToken, serverToken } 受信
  └─ KtcpSession.registerProvider()
     ├─ registerToken を PEM 鍵で検証
     ├─ userToken を JWKS で検証
     ├─ serverToken を JWKS で検証
     ├─ DB に provider レコード保存 (provider_idp_table)
     └─ ClientProviderIdpAddedMsg 送信
```

---

## 3. K8s タスク実行フロー（ktcl-k8s）

ktcl-k8s は KTCP で ktse に接続し、キューからタスクを取得して Kubernetes Job として実行する。

### 3-1. 起動・接続フロー

```
K8sModule.configure()
  └─ KerutaK8sClient.start() をバックグラウンド起動
       │
       ▼ Worker ループ（concurrentCount=1）
       │
       ├─ userTokenDao.getRefreshTokens() で全ユーザーのリフレッシュトークン取得
       └─ each refreshToken:
            ├─ TokenRefresher.refresh()
            │  ├─ TokenRefreshException → トークン削除、次のユーザーへ
            │  └─ Exception            → 30秒待機後リトライ
            │
            ├─ 新 accessToken を DB 保存
            │
            └─ runTaskReceiver(subject, issuer, accessToken)
                 ├─ ConnectionManager.connect()
                 │  └─ HttpClient で /ws/ktcp に WebSocket 接続
                 ├─ providerTokenCreator.create(subject) でプロバイダー JWT 生成
                 ├─ ServerAuthRequestMsg 送信（認証開始）
                 ├─ TaskReceiver.startReceiving() 実行（後述）
                 └─ finally: connection.close()
```

### 3-2. タスク受信・実行フロー（TaskReceiver）

```
startReceiving()
  │
  ├─ [1] 認証完了待機
  │       AUTH_SUCCESS メッセージを最大30秒待機
  │       タイムアウト → false 返却
  │
  ├─ [2] プロバイダーマッチング
  │       ServerProviderListMsg 送信 → ClientProviderListedMsg 受信
  │       issuer == ktclIssuer のプロバイダーを抽出
  │       なし → false 返却（このユーザーはスキップ）
  │
  ├─ [3] キュー一覧取得
  │       ServerQueueListMsg 送信 → ClientQueueListedMsg 受信
  │       myProviderIds に含まれるキューを抽出
  │       なし → false 返却
  │
  └─ [4] キューごとに processQueue()
           │
           ├─ ServerQueueShowMsg → キュー詳細取得
           │  └─ config["git-repo"] が null → false（スキップ）
           │
           ├─ ServerTaskListMsg → pending タスク取得
           │  └─ pending タスクなし → false
           │
           ├─ userTokenDao.getGithubToken()  → null → severe ログ、false
           ├─ userClaudeConfigDao.get()      → null → severe ログ、false
           │
           ├─ ServerTaskUpdateMsg(status=running) 送信
           │
           └─ K8sJobExecutor.executeJob()
                ├─ Res.Ok  → true（実行済み）
                └─ Res.Err → ServerTaskUpdateMsg(status=failed) 送信、false

戻り値: いずれか1キューで true → 次ループまで1秒待機
        全キューで false      → 30秒待機
```

### 3-3. K8s ジョブ実行詳細（K8sJobExecutor）

```
executeJob(taskId, title, gitRepoUrl, githubToken, ...)
  │
  ├─ [1] PVC 生成
  │       "keruta-task-{taskId}-pvc" を ReadWriteOnce で作成
  │       409 (既存) → 再利用
  │
  ├─ [2] Job 作成
  │       job-template.yaml を読み込み、パラメータを注入
  │       batchApi.createNamespacedJob()
  │       409 (既存) → 旧 Job を Foreground 削除（最大60秒待機）後、再作成
  │
  ├─ [3] 並行: Pod ログ監視（K8sJobWatcher.watchLogs）
  │       ストリーミングでリアルタイムログ出力
  │
  └─ [4] 完了ポーリング（5秒間隔）
           ├─ succeeded == 1  → JobStatus.SUCCEEDED
           ├─ failed    == 1  → JobStatus.FAILED
           ├─ elapsed > timeout → JobStatus.TIMEOUT
           ├─ ApiException(5xx) → 次のポーリングへ（リトライ）
           └─ その他 → 継続

結果:
  ├─ SUCCEEDED → Res.Ok(Unit)
  └─ FAILED / TIMEOUT → Res.Err(K8sErr.JobWatchErr)
```

---

## 4. KICP クロスドメインID登録フロー

異なるドメインの idServer 間でユーザーIDをフェデレーションする。実装: `kicp-usecase`。

### 4-1. idServerA でのログイン

```
LoginUseCase.login(oidcToken, oidcJwksUrl, providerToken, providerJwksUrl)
  │
  ├─ JwksRepository.get(providerJwksUrl)
  │  └─ 失敗 → JwksFetchErr
  ├─ JwtVerifier.verify(providerToken, providerJwks)
  │  └─ 失敗 → TokenVerificationErr
  │
  ├─ JwksRepository.get(oidcJwksUrl)
  ├─ JwtVerifier.verify(oidcToken, oidcJwks)
  │
  └─ IdentityId("{issuer}:{subject}") 返却
```

### 4-2. 登録トークン発行（idServerA）

```
GetRegisterTokenUseCase.getRegisterToken(identityId, validForMs=5分)
  │
  ├─ RegisterTokenGenerator.generate() → ランダムトークン
  ├─ RegisterTokenRecord { token, creatorIdentityId, expiresAt } 生成
  └─ RegisterTokenRepository.save(record)
     → RegisterToken 返却（クライアントへ渡す）
```

### 4-3. idServerB でのクロスドメイン登録

```
RegisterUseCase.register(oidcToken, oidcJwksUrl, providerToken, providerJwksUrl, registerToken)
  │
  ├─ providerToken + oidcToken を同様に検証（4-1 と同じ手順）
  │
  ├─ RegisterId("{issuer}:{subject}") 生成（検証済み OIDC クレームから）
  │
  └─ PeerServerClient.verifyRegister(registerId, registerToken)
       ↓（idServerA を呼び出す）
       VerifyRegisterTokenUseCase.verify(registerId, registerToken, currentTimeMs)
         ├─ RegisterTokenRepository.find(token)
         │  └─ null → RegisterTokenNotFoundErr
         ├─ expiresAt < now → RegisterTokenExpiredErr
         │  └─ トークンを削除（期限切れも削除）
         ├─ RegisterTokenRepository.delete(token)（リプレイ防止）
         └─ creatorIdentityId 返却（idServerA での元 IdentityId）
       ↓
  idServerB: IdentityId("{issuer}:{subject}") 返却
```

---

## エラーハンドリング早見表

| エラー型 | 発生フロー | 対処 |
|---|---|---|
| `InvalidTypeDecodeFrameErr` | WS ルーティング | フレームが Text 以外 |
| `DeserializeDecodeFrameErr` | WS ルーティング | JSON/type フィールド不正 |
| `EntrypointNotFoundErr` | WS ルーティング | 未知のメッセージタイプ |
| `UnauthenticatedErr` | 全エントリーポイント | 認証前の操作 → 警告ログ |
| `VerifyFailErr` | 認証 | JWT 署名・クレーム不一致 |
| `VerifyUnsupportedKeyErr` | 認証 | 未対応のアルゴリズム |
| `TokenRefreshException` | K8s 接続 | リフレッシュ失敗 → トークン削除 |
| `K8sErr.JobWatchErr` | K8s 実行 | Job 失敗・タイムアウト |
| `JwksFetchErr` | KICP | JWKS 取得失敗 |
| `TokenVerificationErr` | KICP | JWT 検証失敗 |
| `RegisterTokenNotFoundErr` | KICP | トークン未発見・使用済み |
| `RegisterTokenExpiredErr` | KICP | トークン期限切れ |
