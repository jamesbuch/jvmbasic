#include "lexer.h"
#include "parser.h"
#include "semantic.h"
#include "ast_printer.h"
#include <iostream>
#include <fstream>
#include <cstring>

using namespace std;

void printUsage(const char* progName) {
    cerr << "Usage: " << progName << " [options] < input.bas\n";
    cerr << "Options:\n";
    cerr << "  --dump-ast      Print AST and exit\n";
    cerr << "  --check-only    Parse and type-check, don't generate code\n";
    cerr << "  --help          Show this help\n";
}

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
            printUsage(argv[0]);
            return 0;
        } else {
            cerr << "Unknown option: " << argv[i] << "\n";
            printUsage(argv[0]);
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
        // TODO: Extract CodeGen from jvmbasic.cpp
        cerr << "Code generation not yet implemented in modular version\n";
        cerr << "Use old jvmbasic.cpp for now\n";
        return 1;
        
    } catch (const exception& e) {
        cerr << "Error: " << e.what() << "\n";
        return 1;
    }
}

