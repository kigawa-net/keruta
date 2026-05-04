#!/bin/bash
# ブランチ命名規約チェックスクリプト
# CONVENTION.md の 1-2 に準拠しているかチェック

set -euo pipefail

# 有効なプレフィックス（CONVENTION.md 1-2より）
VALID_PREFIXES="feature/ feat/ fix/ fixes/ doc/ docs/ refactor/ changes/ renovate/"

# メインブランチと特殊ブランチは除外
EXCLUDED_BRANCHES="main develop HEAD"

# エラーカウント
ERROR_COUNT=0

echo "Checking branch naming conventions..."
echo ""

# チェック対象のブランチを取得
# 引数でブランチ名が指定された場合はそれをチェック、そうでなければ現在のブランチをチェック
if [ $# -eq 0 ]; then
    # 現在のブランチを取得
    BRANCHES=$(git branch --show-current)
else
    BRANCHES="$@"
fi

for branch in $BRANCHES; do
    # 除外ブランチのチェック
    if echo "$EXCLUDED_BRANCHES" | grep -qw "$branch"; then
        echo "⚪ $branch (excluded: main branch)"
        continue
    fi
    
    # リモート追跡ブランチの場合はローカル部分のみ抽出
    branch=$(echo "$branch" | sed 's|^origin/||')
    
    # プレフィックスチェック
    valid=false
    for prefix in $VALID_PREFIXES; do
        if [[ "$branch" == "$prefix"* ]]; then
            valid=true
            break
        fi
    done
    
    if [ "$valid" = false ]; then
        echo "❌ $branch"
        echo "   Error: Branch name does not follow naming convention."
        echo "   Valid prefixes: $VALID_PREFIXES"
        ERROR_COUNT=$((ERROR_COUNT + 1))
    else
        # 追加のチェック: スラッシュ後の説明部分が空でないか
        description=$(echo "$branch" | sed 's|^[^/]*/||')
        if [ -z "$description" ]; then
            echo "❌ $branch"
            echo "   Error: Branch name must include a description after prefix."
            ERROR_COUNT=$((ERROR_COUNT + 1))
        else
            echo "✅ $branch"
        fi
    fi
done

echo ""
echo "----------------------------------------"

if [ $ERROR_COUNT -eq 0 ]; then
    echo "✅ All branch names follow naming convention!"
    exit 0
else
    echo "❌ Found $ERROR_COUNT branch(es) that violate the naming convention."
    echo ""
    echo "Valid branch name examples:"
    echo "  feature/kicl-web-user-settings"
    echo "  fix/kicl-web-nav-on-index"
    echo "  docs/update-readme"
    exit 1
fi
