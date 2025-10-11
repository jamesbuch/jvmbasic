#pragma once

#include "ast.h"
#include "lexer.h"
#include <map>

// Parser - builds AST from tokens (NO type checking, pure structural parsing)
class Parser {
private:
    Lexer& lex;
    Token tok;

    void next();
    Token expect(TokenType tt);
    void error(const string& msg);
    string tokenTypeName(TokenType tt);
    
    // Expression parsing (no type checking)
    ExprPtr parsePrimary();
    ExprPtr parseMul();
    ExprPtr parseAdd();
    ExprPtr parseEq();
    ExprPtr parseExpr();
    
    // Statement parsing
    StmtPtr parseStmt();
    
    // Declaration parsing
    DeclPtr parseDecl();

public:
    Parser(Lexer& l);
    Program parse();
};

