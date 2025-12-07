#!/bin/bash
# build-examples.sh - Build all JVM BASIC 2.0 example programs
# Compiles all examples to .class files ready to run

set -e  # Exit on first error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Paths
COMPILER_DIR="src/java"
JAR="$SCRIPT_DIR/$COMPILER_DIR/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar"
EXAMPLES_DIR="examples"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Track results
COMPILED=0
FAILED=0
FAILED_EXAMPLES=""

echo "========================================"
echo "JVM BASIC 2.0 Example Builder"
echo "========================================"
echo ""

# Build compiler first
echo -n "Building compiler... "
if (cd "$COMPILER_DIR" && ./gradlew build -q 2>/dev/null); then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAILED${NC}"
    echo "Build failed. Cannot compile examples."
    exit 1
fi

echo ""
echo -e "${BLUE}Building examples:${NC}"
echo "----------------------------------------"

# Function to compile an example
compile_example() {
    local source="$1"
    local classname="${source%.jvmb}"
    local full_path="$SCRIPT_DIR/$EXAMPLES_DIR/$source"

    if [ ! -f "$full_path" ]; then
        echo -e "  ${YELLOW}SKIP${NC} $source (file not found)"
        return 2
    fi

    echo -n "  Compiling $source... "

    # Compile - output class file to the examples directory
    if java -jar "$JAR" -outdir "$SCRIPT_DIR/$EXAMPLES_DIR" "$full_path" 2>/dev/null >/dev/null; then
        echo -e "${GREEN}OK${NC}"
        COMPILED=$((COMPILED + 1))
        return 0
    else
        echo -e "${RED}FAILED${NC}"
        FAILED=$((FAILED + 1))
        FAILED_EXAMPLES="$FAILED_EXAMPLES $source"
        return 1
    fi
}

# List of example programs (excluding test programs that may be in examples/)
EXAMPLES=(
    "hello.jvmb"
    "demo.jvmb"
    "simple_for.jvmb"
    "simple_while.jvmb"
    "simple_if.jvmb"
    "calculator.jvmb"
    "algo_fibonacci.jvmb"
    "oop_shapes.jvmb"
    "oop_linked_list.jvmb"
    "password_generator.jvmb"
    "financial_calc.jvmb"
    "hash_tool.jvmb"
)

# Compile each example
for example in "${EXAMPLES[@]}"; do
    compile_example "$example"
done

echo ""
echo "========================================"
echo -e "Results: ${GREEN}$COMPILED compiled${NC}, ${RED}$FAILED failed${NC}"
echo "========================================"

if [ -n "$FAILED_EXAMPLES" ]; then
    echo -e "${RED}Failed examples:${NC}$FAILED_EXAMPLES"
    echo ""
fi

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All examples built successfully!${NC}"
    echo ""
    echo "To run an example:"
    echo "  cd examples"
    echo "  java -cp \".:../src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar:../lib/*\" <classname>"
    echo ""
    echo "Example:"
    echo "  java -cp \".:../src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar:../lib/*\" password_generator"
    exit 0
else
    echo -e "${RED}Some examples failed to build.${NC}"
    exit 1
fi
