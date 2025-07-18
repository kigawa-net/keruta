#!/bin/bash

# Keruta project test script

echo "=== Keruta project test start ==="

# Move to project root directory
cd "$(dirname "$0")" || exit 2

# Note: Using fail-fast mode - script exits immediately on any test failure

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
        ./gradlew clean build --no-daemon 2>&1 | tee -a "../$test_results_file"
        if [ ${PIPESTATUS[0]} -eq 0 ]; then
            echo "✅ Gradle build success"
            echo "✅ Gradle build success" >> "../$test_results_file"
        else
            echo "❌ Gradle build failed" >&2
            echo "❌ Gradle build failed" >> "../$test_results_file"
            exit 2
        fi
    ) || {
        echo "💥 Test failed - stopping execution" >&2
        echo "💥 Test failed - stopping execution" >> "$test_results_file"
        echo "Test results saved to: $test_results_file" >&2
        exit 2
    }
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
        ./gradlew ktlintFormat --no-daemon 2>&1 | tee -a "../$test_results_file"
        format_result=${PIPESTATUS[0]}
        ./gradlew ktlintCheck --no-daemon 2>&1 | tee -a "../$test_results_file"
        check_result=${PIPESTATUS[0]}
        if [ "$format_result" -eq 0 ] && [ "$check_result" -eq 0 ]; then
            echo "✅ Kotlin code style check success"
            echo "✅ Kotlin code style check success" >> "../$test_results_file"
        else
            echo "❌ Kotlin code style check failed" >&2
            echo "❌ Kotlin code style check failed" >> "../$test_results_file"
            exit 2
        fi
    ) || {
        echo "💥 Test failed - stopping execution" >&2
        echo "💥 Test failed - stopping execution" >> "$test_results_file"
        echo "Test results saved to: $test_results_file" >&2
        exit 2
    }
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
        go test ./... 2>&1 | tee -a "../$test_results_file"
        if [ "${PIPESTATUS[0]}" -eq 0 ]; then
            echo "✅ Go test success"
            echo "✅ Go test success" >> "../$test_results_file"
        else
            echo "❌ Go test failed" >&2
            echo "❌ Go test failed" >> "../$test_results_file"
            exit 2
        fi
    ) || {
        echo "💥 Test failed - stopping execution" >&2
        echo "💥 Test failed - stopping execution" >> "$test_results_file"
        echo "Test results saved to: $test_results_file" >&2
        exit 2
    }
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
            npm install --silent 2>&1 | tee -a "../$test_results_file"
            install_result=${PIPESTATUS[0]}
            npm run build --silent 2>&1 | tee -a "../$test_results_file"
            build_result=${PIPESTATUS[0]}
            if [ "$install_result" -eq 0 ] && [ "$build_result" -eq 0 ]; then
                echo "✅ React project build success"
                echo "✅ React project build success" >> "../$test_results_file"
            else
                echo "❌ React project build failed" >&2
                echo "❌ React project build failed" >> "../$test_results_file"
                exit 2
            fi
        else
            echo "⚠️ package.json not found"
            echo "⚠️ package.json not found" >> "../$test_results_file"
        fi
    ) || {
        echo "💥 Test failed - stopping execution" >&2
        echo "💥 Test failed - stopping execution" >> "$test_results_file"
        echo "Test results saved to: $test_results_file" >&2
        exit 2
    }
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
        ./gradlew clean build --no-daemon 2>&1 | tee -a "../$test_results_file"
        if [ "${PIPESTATUS[0]}" -eq 0 ]; then
            echo "✅ Gradle build success (keruta-executor)"
            echo "✅ Gradle build success (keruta-executor)" >> "../$test_results_file"
        else
            echo "❌ Gradle build failed (keruta-executor)" >&2
            echo "❌ Gradle build failed (keruta-executor)" >> "../$test_results_file"
            exit 2
        fi
    ) || {
        echo "💥 Test failed - stopping execution" >&2
        echo "💥 Test failed - stopping execution" >> "$test_results_file"
        echo "Test results saved to: $test_results_file" >&2
        exit 2
    }
else
    echo "⚠️ keruta-executor directory not found"
    echo "⚠️ keruta-executor directory not found" >> "$test_results_file"
fi
echo "" >> "$test_results_file"

# Display test results (only reached if all tests pass)
echo "=== Test results ==="
echo "=== Test results ===" >> "$test_results_file"
echo "Test completed at: $(date)" >> "$test_results_file"
echo "🎉 All tests passed!"
echo "🎉 All tests passed!" >> "$test_results_file"
echo "Test results saved to: $test_results_file"
exit 0