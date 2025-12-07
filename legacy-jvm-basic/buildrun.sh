#!/bin/bash
# Build and run JVM BASIC programs with library support

# Compile BasicRuntime with libraries if needed
if [ ! -f basicrt/BasicRuntime.class ] || [ BasicRuntime.java -nt basicrt/BasicRuntime.class ]; then
    echo "Compiling BasicRuntime with libraries..."
    javac -cp "lib/*" BasicRuntime.java
    cp BasicRuntime.class basicrt/
fi

# Compile BASIC program
./jvmbasic < $1

# Run with full classpath
java -cp ".:lib/*:basicrt" BasicProgram

