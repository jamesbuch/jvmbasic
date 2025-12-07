#!/bin/bash
# Dump AST and bytecode for all test programs

OUTPUT_DIR="test_output"
mkdir -p "$OUTPUT_DIR"

echo "=== Dumping AST and Bytecode for All Tests ==="
echo ""

for test_file in tests/test_*.bas; do
    test_name=$(basename "$test_file" .bas)
    echo "Processing: $test_name"
    
    # Dump AST
    ./jvmbasic --dump-ast < "$test_file" > "$OUTPUT_DIR/${test_name}_ast.txt" 2>&1
    
    # Generate bytecode
    ./jvmbasic < "$test_file" > /dev/null 2>&1
    
    if [ -f BasicProgram.class ]; then
        # Dump main class bytecode
        javap -v -c -private BasicProgram > "$OUTPUT_DIR/${test_name}_bytecode.txt" 2>&1
        
        # Dump nested class bytecode if exists
        for nested_class in BasicProgram\$*.class; do
            if [ -f "$nested_class" ]; then
                class_name=$(basename "$nested_class" .class)
                javap -v -c -private "$class_name" >> "$OUTPUT_DIR/${test_name}_bytecode.txt" 2>&1
                echo "" >> "$OUTPUT_DIR/${test_name}_bytecode.txt"
            fi
        done
        
        # Clean up generated files
        rm -f BasicProgram.class BasicProgram\$*.class
    fi
done

echo ""
echo "✓ Complete! Output in $OUTPUT_DIR/"
echo "  Total files: $(ls $OUTPUT_DIR | wc -l)"

