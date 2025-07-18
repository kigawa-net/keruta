#!/bin/bash

# エラートラップを設定
trap 'echo "❌ エラーが発生しました。処理を中止します。" >&2; exit 2' ERR

# エラーが発生した場合に処理を停止する
set -e

# このスクリプトを実行可能にするには以下のコマンドを実行してください:
# chmod +x git.sh

# コミットメッセージを取得（指定がなければデフォルトメッセージを使用）
if [ -z "$1" ]; then
  COMMIT_MESSAGE="Update all projects"
else
  COMMIT_MESSAGE="$1"
fi

# メイン処理を行う関数
commit_and_push() {
  local dir=$1
  local message=$2

  echo "=========================================="
  echo "📁 Processing directory: $dir"

    # ディレクトリが存在しない場合はエラー
    if [ ! -d "$dir" ]; then
      echo "❌ ディレクトリが存在しません: $dir" >&2
      return 2
    fi

  # 指定されたディレクトリに移動
  cd "$dir"

  # 変更があるかチェック
  if git status --porcelain | grep -q .; then
    echo "🔍 Changes detected in $dir"

    # 変更を全て追加
    echo "➕ Adding all changes..."
    if ! git add .; then
      echo "❌ git add コマンドが失敗しました" >&2
      return 2
    fi

    # コミット
    echo "✅ Committing changes..."
    if ! git commit -m "$message"; then
      echo "❌ git commit コマンドが失敗しました" >&2
      return 2
    fi

    # プッシュ
    echo "⬆️ Pushing changes..."
    if ! git push; then
      echo "❌ git push コマンドが失敗しました" >&2
      return 2
    fi

    echo "✨ Successfully committed and pushed changes in $dir"
  else
    echo "👍 No changes detected in $dir"
  fi

  # 元のディレクトリに戻る
  cd - > /dev/null
}

# スクリプトのディレクトリを取得し、絶対パスに変換
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# ルートディレクトリの絶対パスを設定
ROOT_DIR="$SCRIPT_DIR"

echo "🚀 Starting git operations for all projects from $ROOT_DIR"

# メインプロジェクトの変更をコミット＆プッシュ
if ! commit_and_push "$ROOT_DIR" "$COMMIT_MESSAGE"; then
  echo "❌ メインプロジェクトの処理に失敗しました" >&2
  exit 2
fi

# サブモジュールを取得 (スクリプトのディレクトリに移動してから)
cd "$ROOT_DIR" || { echo "❌ ルートディレクトリに移動できません: $ROOT_DIR" >&2; exit 2; }

# .gitmodulesファイルが存在するか確認
if [ ! -f ".gitmodules" ]; then
  echo "⚠️ .gitmodulesファイルが見つかりません。サブモジュールは処理されません。" >&2
  SUBMODULES=""
else
  SUBMODULES=$(git config --file .gitmodules --get-regexp path | awk '{ print $2 }') || { echo "❌ サブモジュール情報の取得に失敗しました" >&2; exit 2; }
fi

# 各サブモジュールの変更をコミット＆プッシュ
if [ -n "$SUBMODULES" ]; then
  for submodule in $SUBMODULES; do
    if [ -d "$ROOT_DIR/$submodule" ]; then
      if ! commit_and_push "$ROOT_DIR/$submodule" "$COMMIT_MESSAGE"; then
        echo "❌ サブモジュール '$submodule' の処理に失敗しました" >&2
        exit 2
      fi
    else
      echo "⚠️ サブモジュールのディレクトリが見つかりません: $submodule" >&2
    fi
  done
fi

echo "=========================================="
echo "🎉 All git operations completed successfully!"
