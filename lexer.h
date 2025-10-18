#pragma once

#include <iostream>
#include <string>
#include <stdexcept>

using namespace std;

// Token types
enum class TokenType { 
    END, NUMBER, STRING, ID, 
    PLUS, MINUS, MUL, DIV, MOD, 
    ASSIGN, SEMI, COMMA, LPAREN, RPAREN, DOT,
    PRINT, LET, INPUT, DIM, 
    LT, GT, LE, GE, EQ, NE, 
    TRUE, FALSE, 
    IF, THEN, ELSE, ENDIF, ELSEIF,
    FOR, TO, STEP, NEXT, 
    WHILE, ENDWHILE, WEND, 
    DO, UNTIL,
    FUNCTION, ENDFUNCTION, SUB, ENDSUB, RETURN, CALL,
    TYPE, ENDTYPE, AS, REM,
    // Phase 7: OOP keywords
    CLASS, ENDCLASS, PUBLIC, PRIVATE, NEW, ME, INTEGER,
    // Phase 8: Logical operators and control flow
    AND, OR, NOT, XOR,
    EXIT, CONTINUE, SELECT, CASE
};

// Token structure
struct Token {
    TokenType type;
    string val;
    double num = 0.0;
    int line = 1;
};

// Lexer/Scanner
class Lexer {
private:
    istream& in;
    char ch = 0;
    bool eof = false;
    int line = 1;

    void read();
    void skipWhite();
    void error(const string& msg);

public:
    Lexer(istream& i);
    Token nextToken();
    int getLine() const { return line; }
};

