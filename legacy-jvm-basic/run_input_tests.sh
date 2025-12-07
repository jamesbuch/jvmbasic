#!/bin/bash
cd /home/james/Downloads/jvmbasic
./jvmbasic < tests/test_input.bas && java -cp . BasicProgram < tests/test_input_data2.txt && echo "✓ test_input PASS"
./jvmbasic < tests/test_input_simple.bas && java -cp . BasicProgram < tests/test_input_simple_data.txt && echo "✓ test_input_simple PASS"
