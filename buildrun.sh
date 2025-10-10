#!/bin/zsh
g++-15 -std=c++20 jvmbasic.cpp -O2 -g -o jvmbasic
./jvmbasic < input.bas
javap -c BasicProgram
java BasicProgram

