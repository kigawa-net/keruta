# dev/stg/prodデプロイCIトリガー修正 実装計画

## 実装方針

kicl-web / ktcl-front / ktcl-k8s / ktse / ktcl-claudecode の5モジュールについて、
`-dev.yml` / `-stg.yml` / `-main.yml` のトリガー条件を以下の設計に統一する。

- `-dev.yml`: `pull_request` イベントでトリガー（develop向けPRの作成・更新時にビルド検証）
- `-stg.yml`: `main` ブランチへの `push` でトリガー（現状は存在しない `staging` ブランチへの
  pushを待っており、リポジトリ作成以来一度も実行されていない）
- `-main.yml`(本番デプロイ): `main` ブランチに対する手動 `workflow_dispatch` でのみトリガー
  （現状は `main` へのpushで自動デプロイされる設定になっており、意図しない自動本番デプロイの
  リスクがある）

`paths` フィルタと呼び出し先の `release.yml`/`build-check.yml` への `with:`/`secrets:` 引数は
変更しない。トリガー条件（`on:` ブロック）のみを変更する。

kise-dev.yml のみ `-stg`/`-main` が存在せず対象外（devのみのモジュールのため今回は変更しない）。

## ファイル・クラス構成

変更対象（計15ファイル、`on:` ブロックのみ）:

- `.github/workflows/kicl-web-dev.yml`
- `.github/workflows/kicl-web-stg.yml`
- `.github/workflows/kicl-web-main.yml`
- `.github/workflows/ktcl-front-dev.yml`
- `.github/workflows/ktcl-front-stg.yml`
- `.github/workflows/ktcl-front-main.yml`
- `.github/workflows/ktcl-k8s-dev.yml`
- `.github/workflows/ktcl-k8s-stg.yml`
- `.github/workflows/ktcl-k8s-main.yml`
- `.github/workflows/ktse-dev.yml`
- `.github/workflows/ktse-stg.yml`
- `.github/workflows/ktse-main.yml`
- `.github/workflows/ktcl-claudecode-dev.yml`
- `.github/workflows/ktcl-claudecode-stg.yml`
- `.github/workflows/ktcl-claudecode-main.yml`

## 実装順序

1. 各 `-dev.yml` の `on:` を `push: branches: [develop]` から `pull_request:` (`paths` は現状維持)
   に変更する。
2. 各 `-stg.yml` の `on:` を `push: branches: [staging]` から `push: branches: [main]` に変更する
   （`paths` は現状維持）。
3. 各 `-main.yml` の `on:` を `push: branches: [main]` から `workflow_dispatch:` に変更する。
4. `doc/convention/ci.md`（存在する場合）に記載のトリガー説明が古くなる場合は合わせて更新する。

## テスト方針

- `actionlint`（リポジトリのCIに組み込まれていれば）またはYAML構文チェックで各ファイルの妥当性を
  確認する。
- 実装PRマージ後、`main` への通常のマージ操作で `-stg.yml` が実際に起動し、Harborへ
  `stg-latest` タグがpushされることを確認する。
- `workflow_dispatch` で `-main.yml` を手動実行し、正常に本番デプロイされることを確認する。
- `develop` 向けPRを作成し、`-dev.yml` が起動することを確認する。
- 上記確認後、`kigawa-net-keruta-stg` namespaceの4 Deploymentが `ImagePullBackOff` から
  `Running` に復旧することを確認する。
