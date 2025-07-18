#!/bin/bash

# Keruta project test script

echo "=== Keruta project test start ==="

# Move to project root directory
cd "$(dirname "$0")" || exit 2

# Variable to track test results
test_failed=0

# Set up test results file
test_results_file="latest.log"
echo "=== Keruta project test results ===" > "$test_results_file"
echo "Test started at: $(date)" >> "$test_results_file"
echo "" >> "$test_results_file"

# 1. Gradle build test (keruta-api)
echo "1. Gradle build test (keruta-api)..."
echo "1. Gradle build test (keruta-api)..." >> "$test_results_file"
if [ -d "keruta-api" ]; then
    (
        cd keruta-api || exit 2
        if ./gradlew clean build --no-daemon 2>&1 | tee -a "../$test_results_file"; then
            echo "✅ Gradle build success"
            echo "✅ Gradle build success" >> "../$test_results_file"
        else
            echo "❌ Gradle build failed"
            echo "❌ Gradle build failed" >> "../$test_results_file"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-api directory not found"
    echo "⚠️ keruta-api directory not found" >> "$test_results_file"
fi
echo "" >> "$test_results_file"

# 2. Kotlin code style check (keruta-api)
echo "2. Kotlin code style check (keruta-api)..."
echo "2. Kotlin code style check (keruta-api)..." >> "$test_results_file"
if [ -d "keruta-api" ]; then
    (
        cd keruta-api || exit 2
        if ./gradlew ktlintFormat --no-daemon 2>&1 | tee -a "../$test_results_file" && ./gradlew ktlintCheck --no-daemon 2>&1 | tee -a "../$test_results_file"; then
            echo "✅ Kotlin code style check success"
            echo "✅ Kotlin code style check success" >> "../$test_results_file"
        else
            echo "❌ Kotlin code style check failed"
            echo "❌ Kotlin code style check failed" >> "../$test_results_file"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-api directory not found"
    echo "⚠️ keruta-api directory not found" >> "$test_results_file"
fi
echo "" >> "$test_results_file"

# 3. Go project test (keruta-agent)
echo "3. Go project (keruta-agent) test..."
echo "3. Go project (keruta-agent) test..." >> "$test_results_file"
if [ -d "keruta-agent" ]; then
    (
        cd keruta-agent || exit 2
        if go test ./... 2>&1 | tee -a "../$test_results_file"; then
            echo "✅ Go test success"
            echo "✅ Go test success" >> "../$test_results_file"
        else
            echo "❌ Go test failed"
            echo "❌ Go test failed" >> "../$test_results_file"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-agent directory not found"
    echo "⚠️ keruta-agent directory not found" >> "$test_results_file"
fi
echo "" >> "$test_results_file"

# 4. TypeScript/React project check (keruta-admin)
echo "4. TypeScript/React project (keruta-admin) check..."
echo "4. TypeScript/React project (keruta-admin) check..." >> "$test_results_file"
if [ -d "keruta-admin" ]; then
    (
        cd keruta-admin || exit 2
        if [ -f "package.json" ]; then
            if npm install --silent 2>&1 | tee -a "../$test_results_file" && npm run build --silent 2>&1 | tee -a "../$test_results_file"; then
                echo "✅ React project build success"
                echo "✅ React project build success" >> "../$test_results_file"
            else
                echo "❌ React project build failed"
                echo "❌ React project build failed" >> "../$test_results_file"
                exit 2
            fi
        else
            echo "⚠️ package.json not found"
            echo "⚠️ package.json not found" >> "../$test_results_file"
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-admin directory not found"
    echo "⚠️ keruta-admin directory not found" >> "$test_results_file"
fi
echo "" >> "$test_results_file"

# 5. Gradle build test (keruta-executor)
echo "5. Gradle build test (keruta-executor)..."
echo "5. Gradle build test (keruta-executor)..." >> "$test_results_file"
if [ -d "keruta-executor" ]; then
    (
        cd keruta-executor || exit 2
        if ./gradlew clean build --no-daemon 2>&1 | tee -a "../$test_results_file"; then
            echo "✅ Gradle build success (keruta-executor)"
            echo "✅ Gradle build success (keruta-executor)" >> "../$test_results_file"
        else
            echo "❌ Gradle build failed (keruta-executor)"
            echo "❌ Gradle build failed (keruta-executor)" >> "../$test_results_file"
            exit 2
        fi
    ) || test_failed=1
else
    echo "⚠️ keruta-executor directory not found"
    echo "⚠️ keruta-executor directory not found" >> "$test_results_file"
fi
echo "" >> "$test_results_file"

# Display test results
echo "=== Test results ==="
echo "=== Test results ===" >> "$test_results_file"
echo "Test completed at: $(date)" >> "$test_results_file"
if [ $test_failed -eq 0 ]; then
    echo "🎉 All tests passed!"
    echo "🎉 All tests passed!" >> "$test_results_file"
    echo "Test results saved to: $test_results_file"
    exit 0
else
    echo "💥 Some tests failed"
    echo "💥 Some tests failed" >> "$test_results_file"
    echo "Test results saved to: $test_results_file"
    exit 2
fi