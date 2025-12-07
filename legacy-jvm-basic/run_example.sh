#!/bin/bash
# Run JVM BASIC Example Program
# Usage: ./run_example.sh <example_name>
# Example: ./run_example.sh comprehensive_demo

EXAMPLE=$1
BASENAME=$(basename "$EXAMPLE" .bas)

if [ -z "$EXAMPLE" ]; then
    echo "Usage: $0 <example_name>"
    echo "Example: $0 comprehensive_demo"
    echo ""
    echo "Available examples:"
    ls examples/*.bas | sed 's|examples/||;s|\.bas||' | sed 's/^/  - /'
    exit 1
fi

# Build classpath
CLASSPATH="."
for jar in lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

echo "=== Compiling and Running: $BASENAME ==="
echo ""

# Compile
./jvmbasic < "examples/$BASENAME.bas" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
    
    # Check if class file was generated (always named BasicProgram.class)
    if [ -f "BasicProgram.class" ]; then
        echo "✓ Class file ready: BasicProgram.class"
        
        # Run with classpath
        echo ""
        echo "=== Running Program ==="
        java -cp "$CLASSPATH" BasicProgram
    else
        echo "✗ Class file not found: ${BASENAME}.class"
    fi
else
    echo "✗ Compilation failed"
    cat /tmp/${BASENAME}.bas
    exit 1
fi
