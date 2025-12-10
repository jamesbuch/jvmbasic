#!/bin/bash
# test-runner.sh - Test all JVM BASIC 2.0 programs
# Run this before committing to ensure all tests pass and examples compile/run correctly

set -e  # Exit on first error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Paths
COMPILER_DIR="src/java"
JAR="$SCRIPT_DIR/$COMPILER_DIR/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar"
TESTS_DIR="tests"
EXAMPLES_DIR="examples"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

# Function to run a test/example from a directory
# Args: directory, source_file, classname
run_test() {
    local dir="$1"
    local source="$2"
    local classname="$3"
    local full_path="$SCRIPT_DIR/$dir/$source"

    if [ ! -f "$full_path" ]; then
        echo -e "  ${YELLOW}SKIP${NC} $source (file not found)"
        return 2
    fi

    echo -n "  Testing $source... "

    # Compile - output class file to the same directory as source
    if ! java -jar "$JAR" -outdir "$SCRIPT_DIR/$dir" "$full_path" 2>/dev/null; then
        echo -e "${RED}COMPILE FAILED${NC}"
        FAILED=$((FAILED + 1))
        FAILED_TESTS="$FAILED_TESTS $source(compile)"
        return 1
    fi

    # Run (include runtime library and output directory on classpath)
    if java -cp "$SCRIPT_DIR/$dir:$JAR" "$classname" >/dev/null 2>&1; then
        echo -e "${GREEN}OK${NC}"
        PASSED=$((PASSED + 1))
        return 0
    else
        echo -e "${RED}RUN FAILED${NC}"
        FAILED=$((FAILED + 1))
        FAILED_TESTS="$FAILED_TESTS $source(run)"
        return 1
    fi
}

echo ""
echo -e "${BLUE}Running tests from tests/:${NC}"
echo "----------------------------------------"

# Tests (from tests/ directory)
TESTS=(
    "array_test.jvmb:array_test"
    "array_param_test.jvmb:array_param_test"
    "bigint_test.jvmb:bigint_test"
    "class_test.jvmb:class_test"
    "comparison_test.jvmb:comparison_test"
    "crypto_test.jvmb:crypto_test"
    "date_test.jvmb:date_test"
    "do_loop_test.jvmb:do_loop_test"
    "double_test.jvmb:double_test"
    "exit_continue_test.jvmb:exit_continue_test"
    "file_test.jvmb:file_test"
    "float_long_test.jvmb:float_long_test"
    "foreach_test.jvmb:foreach_test"
    "for_in_function_test.jvmb:for_in_function_test"
    "function_test.jvmb:function_test"
    "interpolation_test.jvmb:interpolation_test"
    "json_test.jvmb:json_test"
    "math_test.jvmb:math_test"
    "regex_test.jvmb:regex_test"
    "scope_test.jvmb:scope_test"
    "select_case_test.jvmb:select_case_test"
    "string_plus_test.jvmb:string_plus_test"
    "str_test.jvmb:str_test"
)

for test in "${TESTS[@]}"; do
    IFS=':' read -r source classname <<< "$test"
    run_test "$TESTS_DIR" "$source" "$classname"
done

echo ""
echo -e "${BLUE}Running examples from examples/:${NC}"
echo "----------------------------------------"

# Examples - dynamically find all .jvmb files in examples/
# Skip files that require external services (jetty, db demos, etc.)
SKIP_EXAMPLES="jetty_api jetty_hello task_app db_mariadb_demo db_postgresql_demo http_demo wikipedia_demo types enums lambdas nullables oop_abstract_shapes"

for file in "$SCRIPT_DIR/$EXAMPLES_DIR"/*.jvmb; do
    if [ -f "$file" ]; then
        source=$(basename "$file")
        classname="${source%.jvmb}"

        # Check if this example should be skipped
        skip=false
        for skip_name in $SKIP_EXAMPLES; do
            if [ "$classname" = "$skip_name" ]; then
                skip=true
                break
            fi
        done

        if [ "$skip" = true ]; then
            echo -e "  ${YELLOW}SKIP${NC} $source (requires external service)"
            continue
        fi

        run_test "$EXAMPLES_DIR" "$source" "$classname"
    fi
done

echo ""
echo "========================================"
echo -e "Results: ${GREEN}$PASSED passed${NC}, ${RED}$FAILED failed${NC}"
echo "========================================"

if [ $FAILED -gt 0 ]; then
    echo -e "${RED}Failed tests:${NC}$FAILED_TESTS"
    exit 1
else
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
fi
