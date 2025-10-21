#pragma once

#include "ast.h"
#include "lexer.h"
#include <map>
#include <set>

// Parser - builds AST from tokens (NO type checking, pure structural parsing)
class Parser {
private:
    Lexer& lex;
    Token tok;
    
    // Type tracking (minimal for parsing)
    map<string, TypeDefDecl> userTypes;  // User-defined TYPE declarations
    set<string> userClassNames;          // Phase 7: Track CLASS names (can't store full ClassDecl due to unique_ptr)
    map<string, Type> knownTypes;        // Track variable types during parsing

    void next();
    Token expect(TokenType tt);
    void error(const string& msg);
    string tokenTypeName(TokenType tt);
    
    // Type name resolution
    Type resolveTypeName(const string& typeName);
    
    // Expression parsing (no type checking)
    ExprPtr parsePrimary();
    ExprPtr parseMul();
    ExprPtr parseShift();    // Phase 9: Bitwise shift (<< >>)
    ExprPtr parseAdd();
    ExprPtr parseEq();
    ExprPtr parseNot();      // Phase 8: Logical NOT
    ExprPtr parseAnd();      // Phase 8: Logical AND
    ExprPtr parseXor();      // Phase 8: Logical XOR
    ExprPtr parseOr();       // Phase 8: Logical OR
    ExprPtr parseExpr();
    
    // Statement parsing
    StmtPtr parseStmt();
    
    // Declaration parsing
    DeclPtr parseDecl();
    DeclPtr parseTypeDecl();   // Parse TYPE...ENDTYPE
    DeclPtr parseClassDecl();  // Phase 7: Parse CLASS...END CLASS
    MethodDecl parseMethodDecl(bool isPublic);  // Phase 7: Parse method within class

public:
    Parser(Lexer& l);
    Program parse();
    
    // Accessors for code generation
    const map<string, TypeDefDecl>& getUserTypes() const { return userTypes; }
    const set<string>& getUserClassNames() const { return userClassNames; }
    const map<string, Type>& getKnownTypes() const { return knownTypes; }
};

