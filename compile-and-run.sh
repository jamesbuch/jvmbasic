#!/bin/sh

# Fail if no argument
if [ -z "$1" ]; then
    echo "Usage: $0 <file_without_extension>"
    exit 1
fi

FILE="$1"
COMPILER_JAR="src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar"
EXAMPLES_DIR="examples"
CLASSPATH=".:$COMPILER_JAR:lib/*"

# Compile
echo "Compiling $EXAMPLES_DIR/$FILE.jvmb..."
java -jar "$COMPILER_JAR" "$EXAMPLES_DIR/$FILE.jvmb"
STATUS=$?

if [ $STATUS -ne 0 ]; then
    echo "❌ Compilation failed."
    exit $STATUS
fi

echo "✅ Compilation successful."

# Run
echo "Running class: $FILE"
java -cp "$CLASSPATH" "$FILE"

