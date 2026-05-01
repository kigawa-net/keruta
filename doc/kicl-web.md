# kicl-web: 次世代フロントエンド

## 概要

`kicl-web` は、Kotlin Multiplatform (KMP) で共有ロジックを管理する新しいフロントエンドアプリケーションです。React Router v7 をベースにサー�バーサイドレンダリング (SSR) 対応のモダンなWebアプリケーションとして構築されています。

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

## 既存のktcl-frontとの比較

| 項目 | ktcl-front | kicl-web |
|---|---|---|
| フレームワーク | React + Vite | React Router v7 (SSR) |
| 認証 | Keycloak.js | 未実装（今後の予定） |
| 共有ロジック | なし | Kotlin Multiplatform (kicl-domain, kicl-usecase) |
| ステータス | AGENTS.mdに記載あり（本番運用中） | 新規開発中 |

## 今後の展望

- Kotlin Multiplatformを活用したクロスプラットフォーム対応（今後Web以外のプラットフォームも検討）
- 共有ロジックの拡充（現在は基本構造のみ）
- 認証フローの実装
- ktcl-frontからの移行検討
