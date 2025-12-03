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
    
    // Phase 6/7: Struct and class field type information
    map<string, map<string, Type>> structFieldTypes;  // (typeName, fieldName) -> fieldType
    map<string, map<string, Type>> classFieldTypes;   // (className, fieldName) -> fieldType
    map<string, map<string, bool>> classFieldAccess;  // (className, fieldName) -> isPublic
    map<string, string> varTypeNames;  // (varName) -> typeName (for struct/class variables)
    string currentClassContext = "";  // Current class name when analyzing class methods (for access control)
    
    // Analysis methods
    void analyzeDecl(Decl& decl);
    void analyzeFunctionDecl(FunctionDecl& fd);
    void analyzeSubDecl(SubDecl& sd);
    void analyzeClassDecl(ClassDecl& cd);  // Phase 7: Analyze class methods
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
    
    // Phase 10: AST flattening for string concatenation
    ExprPtr flattenStringConcat(ExprPtr expr);
    void flattenStmt(Stmt& stmt);
    void flattenStringConcats(Program& program);

public:
    SemanticAnalyzer() = default;
    
    // Initialize struct/class field type information (called from main.cpp)
    void initStructTypes(const map<string, TypeDefDecl>& userTypes);
    void initClassTypesFromProgram(const Program& program);  // Extract classes from program declarations
    
    // Main analysis entry point
    bool analyze(Program& program);
    
    // Get analysis results
    const SymbolTable& getGlobalSymbols() const { return globalSymbols; }
    const map<string, FuncSignature>& getUserFunctions() const { return userFunctions; }
    const vector<string>& getErrors() const { return errors; }
    bool hasErrors() const { return !errors.empty(); }
    
    // Phase 10: Expose class field types and var type names for codegen
    const map<string, map<string, Type>>& getClassFieldTypes() const { return classFieldTypes; }
    const map<string, string>& getVarTypeNames() const { return varTypeNames; }
};

