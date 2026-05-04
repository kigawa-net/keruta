#!/bin/bash
# doc/ ディレクトリ内のファイル命名規則（ケバブケース）をチェックするスクリプト

set -euo pipefail

# ケバブケースの正規表現: 小文字、数字、ハイフンのみ（連続するハイフンや末尾・先頭のハイフンは不可）
# 例: kicl-web.md, database.md, processing-flows.md
KEBAB_CASE_REGEX='^[a-z0-9]+(-[a-z0-9]+)*\.md$'

DOC_DIR="doc"
ERROR_COUNT=0

echo "Checking doc/ filename conventions (kebab-case)..."
echo ""

if [ ! -d "$DOC_DIR" ]; then
    echo "Error: $DOC_DIR directory not found."
    exit 1
fi

# doc/ 配下の .md ファイルを再帰的にチェック
while IFS= read -r -d '' file; do
    # ファイル名のみを抽出（パスを除く）
    filename=$(basename "$file")
    
    # README.md は標準ファイルのためチェックから除外
    if [[ "$filename" == "README.md" ]]; then
        echo "⚪ $file (excluded: standard filename)"
        continue
    fi
    
    # 正規表現にマッチしない場合はエラー
    if [[ ! "$filename" =~ $KEBAB_CASE_REGEX ]]; then
        echo "❌ $file"
        echo "   Error: Filename '$filename' does not follow kebab-case convention."
        echo "   Expected: lowercase letters, numbers, hyphens (e.g., kicl-web.md)"
        ERROR_COUNT=$((ERROR_COUNT + 1))
    else
        echo "✅ $file"
    fi
done < <(find "$DOC_DIR" -type f -name "*.md" -print0 | sort -z)

echo ""
echo "----------------------------------------"

if [ $ERROR_COUNT -eq 0 ]; then
    echo "✅ All doc/ filenames follow kebab-case convention!"
    exit 0
else
    echo "❌ Found $ERROR_COUNT filename(s) that violate the convention."
    echo "Please rename files to follow kebab-case: lowercase with hyphens."
    exit 1
fi
