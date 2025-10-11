// Hybrid version: New modular frontend + old CodeGen backend
// This allows us to test and use the new architecture while completing the refactor

#include "lexer.h"
#include "parser.h"
#include "semantic.h"
#include "ast_printer.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <cstring>

// TODO: Extract this to codegen.cpp (keeping it here temporarily for faster iteration)
#include <bits/stdc++.h>

using namespace std;
using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Include the CodeGen from old jvmbasic.cpp temporarily
// (Lines 1148-2408 from jvmbasic.cpp)

// We'll paste the ClassFile and related code here

int main(int argc, char** argv) {
    bool dumpAst = false;
    bool checkOnly = false;
    
    // Parse command-line arguments
    for (int i = 1; i < argc; ++i) {
        if (strcmp(argv[i], "--dump-ast") == 0) {
            dumpAst = true;
        } else if (strcmp(argv[i], "--check-only") == 0) {
            checkOnly = true;
        } else if (strcmp(argv[i], "--help") == 0) {
            cerr << "Usage: " << argv[0] << " [options] < input.bas\n";
            cerr << "Options:\n";
            cerr << "  --dump-ast      Print AST and exit\n";
            cerr << "  --check-only    Parse and type-check only\n";
            cerr << "  --help          Show this help\n";
            return 0;
        } else {
            cerr << "Unknown option: " << argv[i] << "\n";
            return 1;
        }
    }
    
    try {
        // PHASE 1: Lexical Analysis and Parsing
        Lexer lexer(cin);
        Parser parser(lexer);
        Program program = parser.parse();
        
        // PHASE 2: Semantic Analysis
        SemanticAnalyzer analyzer;
        bool semanticOk = analyzer.analyze(program);
        
        if (analyzer.hasErrors()) {
            cerr << "Semantic errors:\n";
            for (const auto& err : analyzer.getErrors()) {
                cerr << "  " << err << "\n";
            }
            return 1;
        }
        
        // Dump AST if requested
        if (dumpAst) {
            ASTPrinter printer(cout);
            printer.print(program);
            return 0;
        }
        
        if (checkOnly) {
            cout << "✓ Syntax and semantics OK\n";
            return 0;
        }
        
        // PHASE 3: Code Generation
        // TODO: Use extracted CodeGen module
        cerr << "Code generation not yet integrated\n";
        cerr << "Semantic analysis complete - use old jvmbasic for codegen\n";
        return 1;
        
    } catch (const exception& e) {
        cerr << "Error: " << e.what() << "\n";
        return 1;
    }
}

