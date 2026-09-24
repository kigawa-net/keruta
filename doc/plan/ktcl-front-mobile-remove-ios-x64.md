# ktcl-front-mobile: iosX64 ターゲット廃止と components-resources 1.11.0 アップデート

## 実装方針

compose-multiplatform 1.11.0 で `components-resources` の `iosX64` ターゲットサポートが廃止された。
`buildSrc/src/main/kotlin/compose-mobile-lib.gradle.kts` の `iosX64()` ターゲット定義を削除し、
`components-resources` を 1.11.0 にアップデートする。

## ファイル・クラス構成

変更ファイル:
- `buildSrc/src/main/kotlin/compose-mobile-lib.gradle.kts`
  - `iosX64()` ターゲット定義を削除
  - `components-resources` バージョンを `1.10.3` → `1.11.0` に変更

## 実装順序

1. `compose-mobile-lib.gradle.kts` から `iosX64()` を削除
2. `components-resources` を 1.11.0 に更新
3. ビルド確認
4. Renovate PR #375 をクローズ

## テスト方針

- CI の `gradle-test` が通過することを確認
- `iosArm64` および `iosSimulatorArm64` ターゲットは引き続きビルド可能であることを確認

## 関連

- Issue: #390
- Renovate PR: #375
