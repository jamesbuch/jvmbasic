#pragma once

#include "ast.h"
#include "semantic.h"
#include <iostream>
#include <vector>
#include <map>

using namespace std;
using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Code Generator - generates JVM bytecode from analyzed AST
class CodeGenerator {
public:
    CodeGenerator() = default;
    
    // Generate bytecode for a program
    bool generate(const Program& program, 
                  const SymbolTable& symbols,
                  const map<string, FuncSignature>& userFuncs);
    
    // Write class file
    void writeClassFile(ostream& out);
    
    // Get errors
    const vector<string>& getErrors() const { return errors; }
    bool hasErrors() const { return !errors.empty(); }

private:
    class ClassFile;
    unique_ptr<ClassFile> classFile;
    vector<string> errors;
    
    void error(const string& msg);
};

