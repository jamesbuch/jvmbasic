#!/bin/bash
# Test runner for JVM BASIC

echo "=== JVM BASIC Test Suite ==="
echo ""

passed=0
failed=0

run_test() {
    local test_file=$1
    local test_name=$(basename "$test_file" .bas)
    
    printf "%-40s" "$test_name..."
    
    if ./jvmbasic < "$test_file" > /dev/null 2>&1 && java -cp . BasicProgram > /dev/null 2>&1; then
        echo "✓ PASS"
        ((passed++))
    else
        echo "✗ FAIL"
        ((failed++))
    fi
}

# Phase 5 tests
echo "Phase 5: User-Defined Functions"
run_test "tests/test_function_simple.bas"
run_test "tests/test_func_single_param.bas"
run_test "tests/test_func_multi_param.bas"
run_test "tests/test_func_minimal.bas"
run_test "tests/test_func_expression_only.bas"

echo ""
echo "Phase 1-4: Core Features"
run_test "tests/test_array_int.bas"
run_test "tests/test_functions.bas"
run_test "tests/test_advanced.bas"
run_test "tests/test_math.bas"
run_test "tests/test_bool.bas"

echo ""
echo "=== Results ==="
echo "Passed: $passed"
echo "Failed: $failed"
echo "Total:  $((passed + failed))"

if [ $failed -eq 0 ]; then
    echo "✓ All tests passed!"
    exit 0
else
    echo "⚠ Some tests failed"
    exit 1
fi
