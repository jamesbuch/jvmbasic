#!/bin/bash
# rebuild.sh - Clean and rebuild JVM BASIC compiler and runtime from scratch
# Usage: ./rebuild.sh [--clean-only] [--runtime-only] [--compiler-only] [--test]

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project directories
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_DIR="$PROJECT_DIR/lib"
RUNTIME_DIR="$PROJECT_DIR/basicrt"
RUNTIME_SRC="$PROJECT_DIR/BasicRuntime.java"

# Parse arguments
CLEAN_ONLY=false
RUNTIME_ONLY=false
COMPILER_ONLY=false
RUN_TESTS=false

for arg in "$@"; do
    case $arg in
        --clean-only)
            CLEAN_ONLY=true
            ;;
        --runtime-only)
            RUNTIME_ONLY=true
            ;;
        --compiler-only)
            COMPILER_ONLY=true
            ;;
        --test)
            RUN_TESTS=true
            ;;
        --help|-h)
            echo "Usage: $0 [options]"
            echo "Options:"
            echo "  --clean-only     Only clean, don't rebuild"
            echo "  --runtime-only   Only rebuild the Java runtime"
            echo "  --compiler-only  Only rebuild the C++ compiler"
            echo "  --test           Run tests after building"
            echo "  --help, -h       Show this help"
            exit 0
            ;;
    esac
done

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}  JVM BASIC Build System${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""

# Check Java version
echo -e "${YELLOW}Checking Java version...${NC}"
JAVA_VERSION=$(java -version 2>&1 | head -1)
JAVAC_VERSION=$(javac -version 2>&1)
echo "  Java:  $JAVA_VERSION"
echo "  Javac: $JAVAC_VERSION"
echo ""

# Check C++ compiler
echo -e "${YELLOW}Checking C++ compiler...${NC}"
if [ -f "$PROJECT_DIR/g++-15-wrapper" ]; then
    CXX="$PROJECT_DIR/g++-15-wrapper"
    echo "  Using: g++-15-wrapper"
else
    CXX="g++"
    echo "  Using: system g++"
fi
$CXX --version | head -1
echo ""

# ============================================
# CLEAN
# ============================================
echo -e "${YELLOW}Cleaning build artifacts...${NC}"

# Clean compiler objects
echo "  Removing compiler object files..."
rm -f "$PROJECT_DIR"/*.o
rm -f "$PROJECT_DIR/jvmbasic"

# Clean runtime
echo "  Removing runtime class files..."
rm -f "$RUNTIME_DIR/BasicRuntime.class"
rm -rf "$RUNTIME_DIR/basicrt"  # Remove nested package dir if exists

# Clean test artifacts
echo "  Removing test class files..."
rm -f "$PROJECT_DIR"/*.class
rm -f "$PROJECT_DIR"/Test_*.class

echo -e "${GREEN}  Clean complete!${NC}"
echo ""

if [ "$CLEAN_ONLY" = true ]; then
    echo -e "${GREEN}Clean-only mode, exiting.${NC}"
    exit 0
fi

# ============================================
# BUILD RUNTIME (Java)
# ============================================
if [ "$COMPILER_ONLY" = false ]; then
    echo -e "${YELLOW}Building Java runtime library...${NC}"

    # Create runtime directory if needed
    mkdir -p "$RUNTIME_DIR"

    # Build classpath from lib jars
    CLASSPATH="$LIB_DIR/*:."

    echo "  Compiling BasicRuntime.java..."
    javac -cp "$CLASSPATH" -d "$RUNTIME_DIR" "$RUNTIME_SRC" 2>&1 | grep -v "^Note:" || true

    # The package is 'basicrt', so class ends up in basicrt/basicrt/
    # Move it to the expected location
    if [ -f "$RUNTIME_DIR/basicrt/BasicRuntime.class" ]; then
        mv "$RUNTIME_DIR/basicrt/BasicRuntime.class" "$RUNTIME_DIR/"
        rmdir "$RUNTIME_DIR/basicrt" 2>/dev/null || true
    fi

    if [ -f "$RUNTIME_DIR/BasicRuntime.class" ]; then
        echo -e "${GREEN}  Runtime built successfully!${NC}"
        ls -lh "$RUNTIME_DIR/BasicRuntime.class"
    else
        echo -e "${RED}  ERROR: Runtime build failed!${NC}"
        exit 1
    fi
    echo ""
fi

# ============================================
# BUILD COMPILER (C++)
# ============================================
if [ "$RUNTIME_ONLY" = false ]; then
    echo -e "${YELLOW}Building C++ compiler...${NC}"

    cd "$PROJECT_DIR"

    # Build using make
    echo "  Running make..."
    make all 2>&1 | while read line; do echo "  $line"; done

    if [ -f "$PROJECT_DIR/jvmbasic" ]; then
        echo -e "${GREEN}  Compiler built successfully!${NC}"
        ls -lh "$PROJECT_DIR/jvmbasic"
    else
        echo -e "${RED}  ERROR: Compiler build failed!${NC}"
        exit 1
    fi
    echo ""
fi

# ============================================
# VERIFICATION
# ============================================
echo -e "${YELLOW}Verifying build...${NC}"

# Test compile a simple program
TEST_PROG='Console.WriteLine("Hello from JVM BASIC!")'
echo "$TEST_PROG" | ./jvmbasic -o TestBuild 2>/dev/null

if [ -f "TestBuild.class" ]; then
    echo "  Compiler test: OK"

    # Try to run it
    OUTPUT=$(java -cp ".:basicrt:lib/*" TestBuild 2>&1)
    if [ "$OUTPUT" = "Hello from JVM BASIC!" ]; then
        echo "  Runtime test:  OK"
        echo -e "${GREEN}  Build verification passed!${NC}"
    else
        echo -e "${RED}  Runtime test:  FAILED${NC}"
        echo "  Expected: Hello from JVM BASIC!"
        echo "  Got: $OUTPUT"
    fi
    rm -f TestBuild.class
else
    echo -e "${RED}  Compiler test: FAILED${NC}"
fi
echo ""

# ============================================
# RUN TESTS (optional)
# ============================================
if [ "$RUN_TESTS" = true ]; then
    echo -e "${YELLOW}Running test suite...${NC}"
    echo ""

    PASS=0
    FAIL=0
    SKIP=0

    for f in tests/*.bas; do
        base=$(basename "$f" .bas)

        # Skip tests that need external resources
        case "$base" in
            *input*|*readkey*|*postgres*|*mariadb*|*http*|*db_namespace*|*stdin*)
                echo -e "  ${YELLOW}SKIP${NC}: $base"
                ((SKIP++))
                continue
                ;;
        esac

        if ./jvmbasic -o "Test_$base" < "$f" 2>/dev/null; then
            if timeout 5s java -cp ".:basicrt:lib/*" "Test_$base" > /dev/null 2>&1; then
                echo -e "  ${GREEN}PASS${NC}: $base"
                ((PASS++))
            else
                echo -e "  ${RED}FAIL${NC}: $base (runtime)"
                ((FAIL++))
            fi
        else
            echo -e "  ${RED}FAIL${NC}: $base (compile)"
            ((FAIL++))
        fi
        rm -f "Test_$base.class"
    done

    echo ""
    echo -e "${BLUE}Test Results:${NC}"
    echo -e "  ${GREEN}Passed${NC}: $PASS"
    echo -e "  ${RED}Failed${NC}: $FAIL"
    echo -e "  ${YELLOW}Skipped${NC}: $SKIP"
    echo ""
fi

# ============================================
# SUMMARY
# ============================================
echo -e "${BLUE}======================================${NC}"
echo -e "${GREEN}  Build Complete!${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""
echo "To compile a BASIC program:"
echo "  ./jvmbasic -o MyProgram < myprogram.bas"
echo ""
echo "To run a compiled program:"
echo "  java -cp '.:basicrt:lib/*' MyProgram"
echo ""
