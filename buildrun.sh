#!/bin/zsh
# Use g++-15 wrapper to avoid Cursor AppImage environment issues
if [ -x ./g++-15-wrapper ]; then
    ./g++-15-wrapper -std=gnu++20 -O2 -g jvmbasic.cpp -o jvmbasic
else
    g++ -std=gnu++20 -O2 -g jvmbasic.cpp -o jvmbasic
fi

# Compile BasicRuntime if needed
if [ ! -f basicrt/BasicRuntime.class ] || [ BasicRuntime.java -nt basicrt/BasicRuntime.class ]; then
    echo "Compiling BasicRuntime..."
    javac -d . BasicRuntime.java
fi

./jvmbasic < input.bas
javap -c BasicProgram
java -cp . BasicProgram

