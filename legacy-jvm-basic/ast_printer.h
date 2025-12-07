#pragma once

#include "ast.h"
#include <iostream>
#include <string>

using namespace std;

// AST Pretty Printer - for debugging and --dump-ast option
class ASTPrinter {
private:
    ostream& out;
    int indent = 0;
    
    void printIndent();
    void printExpr(const Expr& expr);
    void printStmt(const Stmt& stmt);
    void printDecl(const Decl& decl);

public:
    ASTPrinter(ostream& o) : out(o) {}
    void print(const Program& program);
};

