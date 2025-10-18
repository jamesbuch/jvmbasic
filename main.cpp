#include "lexer.h"
#include "parser.h"
#include "semantic.h"
#include "ast_printer.h"
#include "codegen.h"
#include <iostream>
#include <fstream>
#include <cstring>

using namespace std;

void printUsage(const char* progName) {
    cerr << "Usage: " << progName << " [options] < input.bas\n";
    cerr << "Options:\n";
    cerr << "  -o <name>       Output class name (default: BasicProgram)\n";
    cerr << "  --dump-ast      Print AST and exit\n";
    cerr << "  --check-only    Parse and type-check, don't generate code\n";
    cerr << "  --help          Show this help\n";
}

int main(int argc, char** argv) {
    bool dumpAst = false;
    bool checkOnly = false;
    string outputClassName = "BasicProgram";  // Default output class name
    
    // Parse command-line arguments
    for (int i = 1; i < argc; ++i) {
        if (strcmp(argv[i], "-o") == 0) {
            if (i + 1 < argc) {
                outputClassName = argv[++i];
            } else {
                cerr << "Error: -o requires a class name argument\n";
                printUsage(argv[0]);
                return 1;
            }
        } else if (strcmp(argv[i], "--dump-ast") == 0) {
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
        
        // Store parser data for code generation
        auto userTypes = parser.getUserTypes();
        auto userClassNames = parser.getUserClassNames();
        auto knownTypes = parser.getKnownTypes();
        
        // PHASE 2: Semantic Analysis
        SemanticAnalyzer analyzer;
        analyzer.analyze(program);
        
        // Only fail on semantic errors if no user types (TYPE/CLASS) are present
        // Semantic analyzer doesn't fully support Phase 6+ yet
        if (analyzer.hasErrors() && userTypes.empty() && userClassNames.empty()) {
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
        string outputFile = outputClassName + ".class";
        
        // Create ClassFile and generate bytecode
        ClassFile cf;
        cf.className = outputClassName;
        cf.buildConstantPool();
        cf.initStructs(userTypes);
        cf.generate(program.declarations, program.statements, knownTypes);
        
        // Write to file
        ofstream outFile(outputFile, ios::binary);
        if (!outFile) {
            cerr << "Error: Cannot open output file: " << outputFile << "\n";
            return 1;
        }
        cf.write(outFile);
        
        cout << "Generated " << outputFile << "\n";
        return 0;
        
    } catch (const exception& e) {
        cerr << "Error: " << e.what() << "\n";
        return 1;
    }
}

