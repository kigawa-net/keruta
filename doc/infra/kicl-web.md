# kicl-web: ユーザー管理インターフェース

## 概要

`kicl-web` は、ユーザー管理を行うWebアプリケーション。**全ページでログインが必須**であり、kiseサーバーにkicpプロトコルで認証する。React Router v7 をベースにSSR対応で構築されており、Kotlin Multiplatform (KMP) で共有ロジックを管理する。

## 認証要件

**kicl-web はすべてのページにおいてログインが必須。**

- 未認証ユーザーはログインページにリダイレクトされる
- 認証先: **kiseサーバー**（kicpプロトコルで通信）
- ログインフロー: [usecase/kise-oidc-login.md](../usecase/kise-oidc-login.md) を参照

## アーキテクチャ

### 全体構成

```
kicl (Kotlin Multiplatform - 共有ロジック層)
├── kicl-domain/    ドメイン層（ビジネスルール、エンティティ定義）
│   └── jsBrowserProductionLibraryDistribution → JSライブラリ出力
└── kicl-usecase/   ユースケース層（アプリケーションサービス）
    └── jsBrowserProductionLibraryDistribution → JSライブラリ出力

kicl-web (TypeScript + React Router v7 - プレゼンテーション層)
    ├── 上記JSライブラリを依存関係として使用
    └── Dockerfile_kicl_web でコンテナ化
```

### 技術スタック

| 層 | 技術 | 用途 |
|---|---|---|
| **フロントエンド** | React Router v7 | SSR対応のルーティング・アプリケーションフレームワーク |
| | React 19 | UIライブラリ |
| | TypeScript ~6.0 | 型安全な開発 |
| | Vite 8 | ビルドツール |
| | Tailwind CSS 4 | スタイリング |
| **共有ロジック** | Kotlin Multiplatform | `kicl-domain`, `kicl-usecase` のJS版ライブラリとして出力 |
| **テスト** | Vitest | ユニットテスト |
| | Testing Library | コンポーネントテスト |

## ディレクトリ構造

```
kicl-web/
├── app/
│   ├── components/    共通コンポーネント（Nav等）
│   ├── routes/        ページルート（index.tsx, settings.tsx）
│   ├── types/         型定義
│   ├── root.tsx       ルートレイアウト
│   ├── routes.tsx     ルート定義
│   └── index.css      グローバルスタイル
├── package.json       依存関係定義
├── vite.config.ts     Vite設定
├── tsconfig.json      TypeScript設定
└── react-router.config.ts  React Router設定
```

## 依存関係

### npm依存関係（package.jsonより）

```json
{
  "dependencies": {
    "keruta-kicl-kicl-domain": "file:../kicl/kicl-domain/build/dist/js/productionLibrary",
    "keruta-kicl-kicl-usecase": "file:../kicl/kicl-usecase/build/dist/js/productionLibrary",
    "@react-router/dev": "^7.11.0",
    "react": "^19.2.0",
    "react-router": "^7.11.0"
  }
}
```

KMPモジュールのビルド出力をローカルファイルとして参照しています。

## ビルドと開発

### 前提条件

Kotlin MultiplatformモジュールのJS版ライブラリを事前にビルドしておく必要があります：

```bash
./gradlew :kicl:kicl-domain:jsBrowserProductionLibraryDistribution \
           :kicl:kicl-usecase:jsBrowserProductionLibraryDistribution
```

### 開発サーバー起動

```bash
cd kicl-web
npm install
npm run dev
```

### 本番ビルド

```bash
npm run build  # React Router のビルド（SSR対応）
npm run start  # 本番サーバー起動
```

## Dockerデプロイ

`Dockerfile_kicl_web` を使用してマルチステージビルドを行います：

```bash
docker build -f Dockerfile_kicl_web -t harbor.kigawa.net/private/kicl-web:latest .
```

### Dockerfileの構成

1. **development-dependencies-env** - npm依存関係のインストール
2. **production-dependencies-env** - 本番用依存関係のインストール
3. **build-env** - アプリケーションのビルド
4. **最終ステージ** - 本番実行環境

KMPモジュールのビルド出力（`kicl-domain`, `kicl-usecase`のproductionLibrary）を各ステージにコピーして使用します。

## CI/CD

GitHub Actions ワークフローが設定されています：

| ワークフロー | トリガー | 用途 |
|---|---|---|
| `kicl-web-check.yml` | PR to develop | ビルド検証 |
| `kicl-web-dev.yml` | developブランチpush | dev環境デプロイ |
| `kicl-web-main.yml` | mainブランチpush | 本番環境デプロイ |

### ビルドパイプライン

CIでは以下の順序でビルドされます：
1. Kotlin MultiplatformモジュールのJSライブラリビルド
2. kicl-webのDockerイメージビルド
3. Harbor Registryへのプッシュ
4. kigawa-net-k8s マニフェスト更新（自動デプロイ）

## 機能一覧

### ルーティング

React Router v7 のファイルベースルーティングを採用。`app/routes.tsx` でルート定義。

| パス | コンポーネント | 説明 |
|------|---------------|------|
| `/` | `routes/index.tsx` | ホーム画面。KMP共有ロジックからバージョン情報を取得して表示。 |
| `/settings` | `routes/settings.tsx` | 設定画面。接続設定と表示設定をlocalStorageに保存。 |

### ホーム画面（`/`）

Kotlin Multiplatformの `kicl-domain` モジュールからエクスポートされたJSライブラリを使用。

```tsx
import {KiclDomain} from "keruta-kicl-kicl-domain";

export default function Index() {
    const version = KiclDomain.getInstance().VERSION;
    return (
        <div>
            <h1>kicl</h1>
            <p>version: {version}</p>
        </div>
    );
}
```

- `KiclDomain` オブジェクト（Kotlin `object` → JS Singleton）から `VERSION` 定数を取得
- KMP共有ロジックが正常にビルド・インテグレートされていることを検証する役割

### 設定画面（`/settings`）

ユーザーがアプリケーションの設定を管理できる画面。`localStorage` に設定を永続化。

#### 設定項目

| 分類 | 項目 | 説明 |
|------|------|------|
| **接続設定** | IDサーバー Issuer URL | 自作IDサーバーのOIDC Issuer URL |
| | OIDCプロバイダー URL | 外部OIDCプロバイダーのURL |
| | タスクサーバー URL | KTSE（Ktorタスクサーバー）のURL |
| **表示設定** | 言語 | 日本語（ja）またはEnglish（en） |

#### 型定義（`types/settings.ts`）

```typescript
export type Language = 'ja' | 'en';

export interface AppSettings {
    ownIssuerUrl: string;
    userIssuerUrl: string;
    ktseUrl: string;
    language: Language;
}

export const SETTINGS_STORAGE_KEY = 'kicl-settings';
```

#### データフロー

```
clientLoader → localStorageから設定読み込み → フォームに反映
     ↓
user submits form
     ↓
clientAction → formData取得 → localStorageに保存 → 成功メッセージ表示
```

- **`clientLoader`**: `localStorage` から設定を読み込み、デフォルト値とマージして返す
- **`clientAction`**: フォーム送信を受け付け、`localStorage` に保存。保存完了後に成功メッセージを表示
- **`HydrateFallback`**: ハイドレーション中のローディング状態を表示

### ナビゲーション（`components/Nav.tsx`）

全画面共通のナビゲーションバー。`root.tsx` の `Layout` 関数で各ページに挿入。

- ブランド名「kicl」を表示
- ホーム（`/`）と設定（`/settings`）へのリンク
- `NavLink` の `isActive` プロパティでアクティブリンクを青色・太字で強調表示

### ルートレイアウト（`root.tsx`）

```tsx
export function Layout({children}: { children: React.ReactNode }) {
    return (
        <html lang="ja">
            <head>
                <Meta/>
                <Links/>
                <title>kicl</title>
            </head>
            <body className="min-h-screen min-w-screen">
                <Nav/>
                {children}
                <ScrollRestoration/>
                <Scripts/>
            </body>
        </html>
    );
}
```

- 言語属性 `lang="ja"` を設定
- `ScrollRestoration` でページ遷移時のスクロール位置を復元
- Tailwind CSS のユーティリティクラスで最小画面サイズを保証

## 共有ロジック（kicl）

### kicl-domain

Kotlin Multiplatform のドメイン層。現在はバージョン情報のみ提供。

```kotlin
@JsExport
object KiclDomain {
    const val VERSION = "0.0.1"
}
```

- `@JsExport` アノテーションによりJSからアクセス可能
- `object` 宣言によりシングルトンとしてエクスポート（JS側では `getInstance()` でアクセス）

### kicl-usecase

ユースケース層。現在はスケルトンのみ（実装ファイルなし）。

今後は以下の機能を配置予定:
- タスク管理のユースケース
- 認証関連のユースケース
- 設定管理のユースケース

## 既存のktcl-frontとの比較

| 項目 | ktcl-front | kicl-web |
|---|---|---|
| フレームワーク | React + Vite | React Router v7 (SSR) |
| 認証 | Keycloak.js | **ログイン必須**（kise + kicp） |
| 共有ロジック | なし | Kotlin Multiplatform (kicl-domain, kicl-usecase) |
| 設定管理 | なし | localStorageベースの実装済み |
| SSR | なし（SPA） | 対応（React Router v7） |
| ステータス | AGENTS.mdに記載あり（本番運用中） | 新規開発中 |

## 今後の展望

- Kotlin Multiplatformを活用したクロスプラットフォーム対応（今後Web以外のプラットフォームも検討）
- 共有ロジックの拡充（kicl-usecaseの実装）
- 認証フローの実装（Keycloak.jsまたはKMP共有認証ロジック）
- タスク管理画面の実装
- ktcl-frontからの移行検討
