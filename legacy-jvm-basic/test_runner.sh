#!/bin/bash
# Test runner for JVM BASIC - Runs ALL tests automatically

echo "=== JVM BASIC Complete Test Suite ==="
echo ""

passed=0
failed=0
skipped=0

# No tests should be skipped - we have input files for all stdin tests
skip_tests=()

run_test() {
    local test_file=$1
    local test_name=$(basename "$test_file" .bas)
    local input_file="${test_file%.bas}.input"

    # Check if this test should be skipped
    for skip_test in "${skip_tests[@]}"; do
        if [[ "$test_file" == *"$skip_test" ]]; then
            printf "%-50s ⊘ SKIP (requires stdin)\n" "$test_name..."
            ((skipped++))
            return
        fi
    done

    printf "%-50s" "$test_name..."

    # Compile the test
    if ! ./jvmbasic < "$test_file" > /dev/null 2>&1; then
        echo "✗ FAIL (compile)"
        ((failed++))
        return
    fi

    # Run the test - use input file if it exists
    if [ -f "$input_file" ]; then
        if java -cp ".:lib/*:basicrt" BasicProgram < "$input_file" > /dev/null 2>&1; then
            echo "✓ PASS"
            ((passed++))
        else
            echo "✗ FAIL (run)"
            ((failed++))
        fi
    else
        if java -cp ".:lib/*:basicrt" BasicProgram > /dev/null 2>&1; then
            echo "✓ PASS"
            ((passed++))
        else
            echo "✗ FAIL (run)"
            ((failed++))
        fi
    fi
}

# Run all tests in tests/ directory
for test_file in tests/*.bas; do
    if [ -f "$test_file" ]; then
        run_test "$test_file"
    fi
done

echo ""
echo "=== Results ==="
echo "Passed:  $passed"
echo "Failed:  $failed"
if [ $skipped -gt 0 ]; then
    echo "Skipped: $skipped (require stdin)"
fi
echo "Total:   $((passed + failed + skipped))"

if [ $failed -eq 0 ]; then
    echo "✓ All automated tests passed!"
    exit 0
else
    echo "⚠ Some tests failed"
    exit 1
fi
