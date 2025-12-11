#!/bin/bash
# example-runner.sh - Compile and optionally run JVM BASIC 2.0 example programs
#
# Usage:
#   ./example-runner.sh              # Compile all examples
#   ./example-runner.sh hello        # Compile and run hello.jvmb
#   ./example-runner.sh -c           # Compile only (all examples)
#   ./example-runner.sh -c hello     # Compile only hello.jvmb
#   ./example-runner.sh -l           # List all available examples

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Paths
COMPILER_DIR="src/java"
JAR="$SCRIPT_DIR/$COMPILER_DIR/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar"
EXAMPLES_DIR="examples"
LIB_DIR="lib"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Classpath for running programs
CLASSPATH=".:$JAR:$LIB_DIR/*"

# Parse arguments
COMPILE_ONLY=false
LIST_ONLY=false
EXAMPLE=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        -c|--compile)
            COMPILE_ONLY=true
            shift
            ;;
        -l|--list)
            LIST_ONLY=true
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [options] [example_name]"
            echo ""
            echo "Options:"
            echo "  -c, --compile    Compile only (don't run)"
            echo "  -l, --list       List available examples"
            echo "  -h, --help       Show this help"
            echo ""
            echo "Examples:"
            echo "  $0               Compile all examples"
            echo "  $0 hello         Compile and run hello.jvmb"
            echo "  $0 -c algo_bst   Compile algo_bst.jvmb only"
            exit 0
            ;;
        *)
            EXAMPLE="$1"
            shift
            ;;
    esac
done

# List examples
if [ "$LIST_ONLY" = true ]; then
    echo "Available examples in $EXAMPLES_DIR/:"
    echo "========================================"
    for file in "$EXAMPLES_DIR"/*.jvmb; do
        if [ -f "$file" ]; then
            name=$(basename "$file" .jvmb)
            echo "  $name"
        fi
    done
    exit 0
fi

# Build compiler first
echo -n "Building compiler... "
if (cd "$COMPILER_DIR" && ./gradlew build -q 2>/dev/null); then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAILED${NC}"
    echo "Build failed. Cannot continue."
    exit 1
fi
echo ""

# Compile a single example
compile_example() {
    local name="$1"
    local source="$EXAMPLES_DIR/$name.jvmb"

    if [ ! -f "$source" ]; then
        echo -e "${RED}Error: $source not found${NC}"
        return 1
    fi

    echo -n "Compiling $name.jvmb... "
    if java -jar "$JAR" "$source" 2>&1; then
        echo -e "${GREEN}OK${NC}"
        return 0
    else
        echo -e "${RED}FAILED${NC}"
        return 1
    fi
}

# Run an example
run_example() {
    local name="$1"
    echo ""
    echo -e "${BLUE}Running $name:${NC}"
    echo "----------------------------------------"
    java -cp "$CLASSPATH" "$name"
    echo "----------------------------------------"
}

# Single example mode
if [ -n "$EXAMPLE" ]; then
    compile_example "$EXAMPLE" || exit 1

    if [ "$COMPILE_ONLY" = false ]; then
        run_example "$EXAMPLE"
    fi
    exit 0
fi

# Compile all examples
echo -e "${BLUE}Compiling all examples:${NC}"
echo "========================================"
COMPILED=0
FAILED=0

for file in "$EXAMPLES_DIR"/*.jvmb; do
    if [ -f "$file" ]; then
        name=$(basename "$file" .jvmb)
        if compile_example "$name"; then
            COMPILED=$((COMPILED + 1))
        else
            FAILED=$((FAILED + 1))
        fi
    fi
done

echo ""
echo "========================================"
echo -e "Results: ${GREEN}$COMPILED compiled${NC}, ${RED}$FAILED failed${NC}"
echo "========================================"

if [ $FAILED -gt 0 ]; then
    exit 1
fi

echo ""
echo "To run an example: $0 <example_name>"
echo "Example: $0 hello"
