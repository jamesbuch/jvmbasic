#pragma once

#include "ast.h"
#include <map>
#include <string>
#include <vector>

using namespace std;

// Symbol table for tracking variable and function types
class SymbolTable {
private:
    map<string, Type> symbols;
    SymbolTable* parent = nullptr;  // For nested scopes

public:
    SymbolTable() = default;
    SymbolTable(SymbolTable* p) : parent(p) {}
    
    void define(const string& name, Type type);
    bool isDefined(const string& name) const;
    Type getType(const string& name) const;
    void clear() { symbols.clear(); }
    
    const map<string, Type>& getSymbols() const { return symbols; }
};

// Function/Sub signature for type checking
struct FuncSignature {
    string name;
    vector<Type> paramTypes;
    Type returnType;  // Type::Int for void (subs)
    bool isVoid;
};

// Semantic Analyzer - performs type checking and inference
class SemanticAnalyzer {
private:
    SymbolTable globalSymbols;
    map<string, FuncSignature> userFunctions;
    vector<string> errors;
    
    // Type inference support
    struct CallSite {
        string funcName;
        vector<Type> argTypes;
        int line;
    };
    vector<CallSite> callSites;
    
    // Analysis methods
    void analyzeDecl(Decl& decl);
    void analyzeFunctionDecl(FunctionDecl& fd);
    void analyzeSubDecl(SubDecl& sd);
    void analyzeStmt(Stmt& stmt, SymbolTable& symbols);
    void analyzeExpr(Expr& expr, const SymbolTable& symbols);
    
    // Type inference
    void inferParameterTypes();
    void inferReturnType(FunctionDecl& fd);
    Type inferExprType(const Expr& expr, const SymbolTable& symbols);
    
    // Helper methods
    void error(const string& msg);
    bool isNumericType(Type t);
    Type promoteTypes(Type a, Type b);
    Type getArrayElementType(Type arrayType);
    Type makeArrayType(Type elemType);

public:
    SemanticAnalyzer() = default;
    
    // Main analysis entry point
    bool analyze(Program& program);
    
    // Get analysis results
    const SymbolTable& getGlobalSymbols() const { return globalSymbols; }
    const map<string, FuncSignature>& getUserFunctions() const { return userFunctions; }
    const vector<string>& getErrors() const { return errors; }
    bool hasErrors() const { return !errors.empty(); }
};

