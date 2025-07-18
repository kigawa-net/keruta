#!/bin/bash

# Keruta project test script

echo "=== Keruta project test start ==="

# Move to project root directory
cd "$(dirname "$0")" || exit 2

# Variable to track test results
test_failed=0

# 1. Gradle build test (keruta-api)
echo "1. Gradle build test (keruta-api)..."
if [ -d "keruta-api" ]; then
    (
        cd keruta-api || exit 2
        if ./gradlew clean build --no-daemon; then
            echo "✅ Gradle build success"
        else
            echo "❌ Gradle build failed"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-api directory not found"
fi

# 2. Kotlin code style check (keruta-api)
echo "2. Kotlin code style check (keruta-api)..."
if [ -d "keruta-api" ]; then
    (
        cd keruta-api || exit 2
        if ./gradlew ktlintFormat --no-daemon && ./gradlew ktlintCheck --no-daemon; then
            echo "✅ Kotlin code style check success"
        else
            echo "❌ Kotlin code style check failed"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-api directory not found"
fi

# 3. Go project test (keruta-agent)
echo "3. Go project (keruta-agent) test..."
if [ -d "keruta-agent" ]; then
    (
        cd keruta-agent || exit 2
        if go test ./...; then
            echo "✅ Go test success"
        else
            echo "❌ Go test failed"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-agent directory not found"
fi

# 4. TypeScript/React project check (keruta-admin)
echo "4. TypeScript/React project (keruta-admin) check..."
if [ -d "keruta-admin" ]; then
    (
        cd keruta-admin || exit 2
        if [ -f "package.json" ]; then
            if npm install --silent && npm run build --silent 2>/dev/null; then
                echo "✅ React project build success"
            else
                echo "❌ React project build failed"
                exit 2
            fi
        else
            echo "⚠️ package.json not found"
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-admin directory not found"
fi

# Display test results
echo "=== Test results ==="
if [ $test_failed -eq 0 ]; then
    echo "🎉 All tests passed!"
    exit 0
else
    echo "💥 Some tests failed"
    exit 2
fi