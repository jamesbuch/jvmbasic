#!/bin/zsh
# Use g++-15 wrapper to avoid Cursor AppImage environment issues
if [ -x ./g++-15-wrapper ]; then
    ./g++-15-wrapper -std=gnu++20 -O2 -g jvmbasic.cpp -o jvmbasic
else
    g++ -std=gnu++20 -O2 -g jvmbasic.cpp -o jvmbasic
fi
./jvmbasic < input.bas
javap -c BasicProgram
java BasicProgram

