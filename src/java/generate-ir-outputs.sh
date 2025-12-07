#!/bin/bash
# generate-ir-outputs.sh - Generate IR and sIR outputs for all example programs
# This creates .ir and .sir files alongside .class files for debugging/analysis

set -e  # Exit on first error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

JAR="build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar"
OUTPUT_DIR="examples/output"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Ensure compiler is built
echo "Building compiler..."
./gradlew build -q 2>/dev/null || { echo -e "${RED}Build failed${NC}"; exit 1; }
echo -e "${GREEN}OK${NC}"

# Create output directory
mkdir -p "$OUTPUT_DIR"

echo ""
echo "========================================"
echo "Generating IR and sIR Outputs"
echo "========================================"
echo ""

# Count generated files
GENERATED=0
FAILED=0

# Generate outputs for each .jvmb file
for source in examples/*.jvmb; do
    if [ ! -f "$source" ]; then
        continue
    fi

    basename=$(basename "$source" .jvmb)
    echo -n "  Processing $basename... "

    # Generate IR and sIR files (parse-only mode, no class generation)
    if java -jar "$JAR" --output-ir --output-sir -parse-only "$source" >/dev/null 2>&1; then
        # Move generated files to output directory
        mv -f "${source}.ir" "$OUTPUT_DIR/${basename}.ir" 2>/dev/null || true
        mv -f "${source}.sir" "$OUTPUT_DIR/${basename}.sir" 2>/dev/null || true
        echo -e "${GREEN}OK${NC}"
        GENERATED=$((GENERATED + 1))
    else
        echo -e "${RED}FAILED${NC}"
        FAILED=$((FAILED + 1))
    fi
done

echo ""
echo "========================================"
echo "Generated: $GENERATED, Failed: $FAILED"
echo "Output directory: $OUTPUT_DIR/"
echo "========================================"

# List generated files
if [ $GENERATED -gt 0 ]; then
    echo ""
    echo "Files generated:"
    ls -la "$OUTPUT_DIR"/*.ir "$OUTPUT_DIR"/*.sir 2>/dev/null | head -20
fi
