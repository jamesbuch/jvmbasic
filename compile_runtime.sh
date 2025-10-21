#!/bin/bash
# Compile BasicRuntime.java with all required libraries
echo "Compiling BasicRuntime.java with libraries..."
javac -cp "lib/*" BasicRuntime.java
if [ $? -eq 0 ]; then
    cp BasicRuntime.class basicrt/
    echo "✓ BasicRuntime compiled successfully"
    echo "✓ Copied to basicrt/ directory"
else
    echo "✗ Compilation failed"
    exit 1
fi

