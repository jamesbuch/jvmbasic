CXX = ./g++-15-wrapper
CXXFLAGS = -std=gnu++20 -O2
OBJECTS = ast.o lexer.o parser.o semantic.o ast_printer.o builtin_functions.o

# Targets
all: jvmbasic jvmbasic-new

# New modular version (with --dump-ast, --check-only)
jvmbasic-new: $(OBJECTS) main.o
	$(CXX) $(CXXFLAGS) $(OBJECTS) main.o -o jvmbasic-new

# Modular version with extracted codegen (now default)
jvmbasic: jvmbasic.cpp builtin_functions.o
	$(CXX) $(CXXFLAGS) jvmbasic.cpp builtin_functions.o -o jvmbasic

# Object files
ast.o: ast.cpp ast.h
	$(CXX) $(CXXFLAGS) -c ast.cpp

lexer.o: lexer.cpp lexer.h
	$(CXX) $(CXXFLAGS) -c lexer.cpp

parser.o: parser.cpp parser.h ast.h lexer.h builtin_functions.h
	$(CXX) $(CXXFLAGS) -c parser.cpp

semantic.o: semantic.cpp semantic.h ast.h builtin_functions.h
	$(CXX) $(CXXFLAGS) -c semantic.cpp

ast_printer.o: ast_printer.cpp ast_printer.h ast.h
	$(CXX) $(CXXFLAGS) -c ast_printer.cpp

builtin_functions.o: builtin_functions.cpp builtin_functions.h ast.h
	$(CXX) $(CXXFLAGS) -c builtin_functions.cpp

main.o: main.cpp lexer.h parser.h semantic.h ast_printer.h
	$(CXX) $(CXXFLAGS) -c main.cpp

# Utility targets
clean:
	rm -f *.o jvmbasic jvmbasic-new BasicProgram.class

test: jvmbasic
	@echo "Running basic tests..."
	@./jvmbasic < tests/test_function_simple.bas && java -cp . BasicProgram

dump-ast: jvmbasic-new
	./jvmbasic-new --dump-ast

check: jvmbasic-new
	./jvmbasic-new --check-only

.PHONY: all clean test dump-ast check

