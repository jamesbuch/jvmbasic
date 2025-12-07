#!/bin/bash
# test-examples.sh - Test all JVM BASIC 2.0 example programs
# Run this before committing to ensure all examples compile and run correctly

set -e  # Exit on first error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Paths
COMPILER_DIR="src/java"
JAR="$COMPILER_DIR/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar"
EXAMPLES_DIR="examples"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track results
PASSED=0
FAILED=0
FAILED_TESTS=""

echo "========================================"
echo "JVM BASIC 2.0 Test Suite"
echo "========================================"
echo ""

# Build first
echo -n "Building compiler... "
if (cd "$COMPILER_DIR" && ./gradlew build -q 2>/dev/null); then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAILED${NC}"
    echo "Build failed. Cannot run tests."
    exit 1
fi

echo ""
echo "Testing examples:"
echo "----------------------------------------"

# List of test examples (add new tests here)
TESTS=(
    "hello.jvmb:hello"
    "demo.jvmb:demo"
    "array_test.jvmb:array_test"
    "foreach_test.jvmb:foreach_test"
    "function_test.jvmb:function_test"
    "do_loop_test.jvmb:do_loop_test"
    "simple_for.jvmb:simple_for"
    "simple_while.jvmb:simple_while"
    "simple_if.jvmb:simple_if"
    "float_long_test.jvmb:float_long_test"
    "double_test.jvmb:double_test"
    "interpolation_test.jvmb:interpolation_test"
    "exit_continue_test.jvmb:exit_continue_test"
    "select_case_test.jvmb:select_case_test"
    "math_test.jvmb:math_test"
    "str_test.jvmb:str_test"
    "regex_test.jvmb:regex_test"
    "file_test.jvmb:file_test"
    "comparison_test.jvmb:comparison_test"
    "string_plus_test.jvmb:string_plus_test"
    "for_in_function_test.jvmb:for_in_function_test"
    "array_param_test.jvmb:array_param_test"
    "scope_test.jvmb:scope_test"
    "json_test.jvmb:json_test"
    "bigint_test.jvmb:bigint_test"
)

for test in "${TESTS[@]}"; do
    IFS=':' read -r source classname <<< "$test"

    if [ ! -f "$EXAMPLES_DIR/$source" ]; then
        echo -e "  ${YELLOW}SKIP${NC} $source (file not found)"
        continue
    fi

    echo -n "  Testing $source... "

    # Compile
    if ! java -jar "$JAR" "$EXAMPLES_DIR/$source" 2>/dev/null; then
        echo -e "${RED}COMPILE FAILED${NC}"
        FAILED=$((FAILED + 1))
        FAILED_TESTS="$FAILED_TESTS $source(compile)"
        continue
    fi

    # Run (include runtime library on classpath)
    if java -cp ".:$JAR" "$classname" >/dev/null 2>&1; then
        echo -e "${GREEN}OK${NC}"
        PASSED=$((PASSED + 1))
    else
        echo -e "${RED}RUN FAILED${NC}"
        FAILED=$((FAILED + 1))
        FAILED_TESTS="$FAILED_TESTS $source(run)"
    fi

    # Cleanup
    rm -f "${classname}.class" 2>/dev/null
done

echo ""
echo "========================================"
echo "Results: ${GREEN}$PASSED passed${NC}, ${RED}$FAILED failed${NC}"
echo "========================================"

if [ $FAILED -gt 0 ]; then
    echo -e "${RED}Failed tests:${NC}$FAILED_TESTS"
    exit 1
else
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
fi
