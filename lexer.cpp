#include "lexer.h"
#include <cctype>

void Lexer::read() {
    if (!in.get(ch)) {
        eof = true;
        ch = 0;
    } else if (ch == '\n') {
        line++;
    }
}

void Lexer::skipWhite() {
    while (!eof && isspace(ch)) {
        read();
    }
}

void Lexer::error(const string& msg) {
    throw runtime_error(msg);
}

Lexer::Lexer(istream& i) : in(i) {
    read();
    skipWhite();
}

Token Lexer::nextToken() {
    skipWhite();
    int tokenLine = line;
    if (eof) return {TokenType::END, "", 0.0, tokenLine};

    // Number literals
    if (isdigit(ch)) {
        string s;
        while (!eof && isdigit(ch)) {
            s += ch;
            read();
        }
        if (!eof && ch == '.') {
            s += ch;
            read();
            while (!eof && isdigit(ch)) {
                s += ch;
                read();
            }
        }
        Token t{TokenType::NUMBER, s, stod(s), tokenLine};
        return t;
    }
    
    // String literals
    if (ch == '"') {
        read();
        string s;
        while (!eof && ch != '"') {
            s += ch;
            read();
        }
        if (!eof && ch == '"') read();
        else if (eof) error("Unterminated string at line " + to_string(tokenLine));
        return {TokenType::STRING, s, 0.0, tokenLine};
    }
    
    // Keywords and identifiers
    if (!eof && isalpha(ch)) {
        string s;
        while (!eof && isalnum(ch)) {
            s += ch;
            read();
        }
        
        // Convert to uppercase for keyword matching
        string upper = s;
        for (auto& c : upper) c = toupper(c);
        
        // Keywords
        if (upper == "PRINT") return {TokenType::PRINT, s, 0.0, tokenLine};
        if (upper == "LET") return {TokenType::LET, s, 0.0, tokenLine};
        if (upper == "INPUT") return {TokenType::INPUT, s, 0.0, tokenLine};
        if (upper == "DIM") return {TokenType::DIM, s, 0.0, tokenLine};
        if (upper == "MOD") return {TokenType::MOD, s, 0.0, tokenLine};
        if (upper == "IF") return {TokenType::IF, s, 0.0, tokenLine};
        if (upper == "THEN") return {TokenType::THEN, s, 0.0, tokenLine};
        if (upper == "ELSE") return {TokenType::ELSE, s, 0.0, tokenLine};
        if (upper == "ELSEIF") return {TokenType::ELSEIF, s, 0.0, tokenLine};
        if (upper == "ENDIF") return {TokenType::ENDIF, s, 0.0, tokenLine};
        if (upper == "FOR") return {TokenType::FOR, s, 0.0, tokenLine};
        if (upper == "TO") return {TokenType::TO, s, 0.0, tokenLine};
        if (upper == "STEP") return {TokenType::STEP, s, 0.0, tokenLine};
        if (upper == "NEXT") return {TokenType::NEXT, s, 0.0, tokenLine};
        if (upper == "WHILE") return {TokenType::WHILE, s, 0.0, tokenLine};
        if (upper == "ENDWHILE") return {TokenType::ENDWHILE, s, 0.0, tokenLine};
        if (upper == "WEND") return {TokenType::WEND, s, 0.0, tokenLine};
        if (upper == "DO") return {TokenType::DO, s, 0.0, tokenLine};
        if (upper == "UNTIL") return {TokenType::UNTIL, s, 0.0, tokenLine};
        
        // Check for END* keywords BEFORE checking for standalone END
        if (upper == "ENDFUNCTION") return {TokenType::ENDFUNCTION, s, 0.0, tokenLine};
        if (upper == "ENDSUB") return {TokenType::ENDSUB, s, 0.0, tokenLine};
        if (upper == "ENDTYPE") return {TokenType::ENDTYPE, s, 0.0, tokenLine};
        if (upper == "ENDCLASS") return {TokenType::ENDCLASS, s, 0.0, tokenLine};
        if (upper == "ENDIF") return {TokenType::ENDIF, s, 0.0, tokenLine};
        
        if (upper == "FUNCTION") return {TokenType::FUNCTION, s, 0.0, tokenLine};
        if (upper == "SUB") return {TokenType::SUB, s, 0.0, tokenLine};
        if (upper == "RETURN") return {TokenType::RETURN, s, 0.0, tokenLine};
        if (upper == "CALL") return {TokenType::CALL, s, 0.0, tokenLine};
        if (upper == "TYPE") return {TokenType::TYPE, s, 0.0, tokenLine};
        if (upper == "AS") return {TokenType::AS, s, 0.0, tokenLine};
        
        // Phase 7: OOP keywords
        if (upper == "CLASS") return {TokenType::CLASS, s, 0.0, tokenLine};
        if (upper == "ENDCLASS") return {TokenType::ENDCLASS, s, 0.0, tokenLine};
        if (upper == "PUBLIC") return {TokenType::PUBLIC, s, 0.0, tokenLine};
        if (upper == "PRIVATE") return {TokenType::PRIVATE, s, 0.0, tokenLine};
        if (upper == "NEW") return {TokenType::NEW, s, 0.0, tokenLine};
        if (upper == "ME") return {TokenType::ME, s, 0.0, tokenLine};
        if (upper == "INTEGER") return {TokenType::INTEGER, s, 0.0, tokenLine};
        
        // Phase 8: Logical operators and control flow
        if (upper == "AND") return {TokenType::AND, s, 0.0, tokenLine};
        if (upper == "OR") return {TokenType::OR, s, 0.0, tokenLine};
        if (upper == "NOT") return {TokenType::NOT, s, 0.0, tokenLine};
        if (upper == "XOR") return {TokenType::XOR, s, 0.0, tokenLine};
        if (upper == "EXIT") return {TokenType::EXIT, s, 0.0, tokenLine};
        if (upper == "CONTINUE") return {TokenType::CONTINUE, s, 0.0, tokenLine};
        if (upper == "SELECT") return {TokenType::SELECT, s, 0.0, tokenLine};
        if (upper == "CASE") return {TokenType::CASE, s, 0.0, tokenLine};
        
        // Phase 9: Modern VB-style type keywords
        if (upper == "SINGLE") return {TokenType::SINGLE, s, 0.0, tokenLine};
        if (upper == "DOUBLE") return {TokenType::DOUBLE, s, 0.0, tokenLine};
        if (upper == "LONG") return {TokenType::LONG, s, 0.0, tokenLine};
        if (upper == "BOOLEAN") return {TokenType::BOOLEAN, s, 0.0, tokenLine};
        if (upper == "STRING") return {TokenType::STRINGTYPE, s, 0.0, tokenLine};
        if (upper == "DECIMAL") return {TokenType::DECIMAL, s, 0.0, tokenLine};
        if (upper == "BIGINT") return {TokenType::BIGINT, s, 0.0, tokenLine};
        
        // Phase 9: Modern keywords
        if (upper == "CONSOLE") return {TokenType::CONSOLE, s, 0.0, tokenLine};
        if (upper == "IMPORT") return {TokenType::IMPORT, s, 0.0, tokenLine};
        if (upper == "IMPORTS") return {TokenType::IMPORTS, s, 0.0, tokenLine};
        if (upper == "SHARED") return {TokenType::SHARED, s, 0.0, tokenLine};
        if (upper == "STATIC") return {TokenType::STATIC, s, 0.0, tokenLine};
        if (upper == "BYVAL") return {TokenType::BYVAL, s, 0.0, tokenLine};
        if (upper == "BYREF") return {TokenType::BYREF, s, 0.0, tokenLine};
        
        if (upper == "REM") {
            // Comment - consume rest of line and return next token
            while (!eof && ch != '\n') {
                read();
            }
            return nextToken();
        }
        
        // Handle END + keyword (VB-style: "END SUB", "END FUNCTION", etc.)
        if (upper == "END") {
            skipWhite();
            if (!eof && isalpha(ch)) {
                string next;
                while (!eof && isalnum(ch)) {
                    next += ch;
                    read();
                }
                string nextUpper = next;
                for (auto& c : nextUpper) c = toupper(c);
                
                if (nextUpper == "IF") return {TokenType::ENDIF, "ENDIF", 0.0, tokenLine};
                if (nextUpper == "SUB") return {TokenType::ENDSUB, "ENDSUB", 0.0, tokenLine};
                if (nextUpper == "FUNCTION") return {TokenType::ENDFUNCTION, "ENDFUNCTION", 0.0, tokenLine};
                if (nextUpper == "TYPE") return {TokenType::ENDTYPE, "ENDTYPE", 0.0, tokenLine};
                if (nextUpper == "CLASS") return {TokenType::ENDCLASS, "ENDCLASS", 0.0, tokenLine};
                if (nextUpper == "WHILE") return {TokenType::ENDWHILE, "ENDWHILE", 0.0, tokenLine};
                
                error("Unknown END keyword: END " + next + " at line " + to_string(tokenLine));
            }
            // Bare END is also used to mark end of file in some tests
            return {TokenType::END, "END", 0.0, tokenLine};
        }
        
        // Boolean literals
        if (upper == "TRUE") return {TokenType::TRUE, "true", 0.0, tokenLine};
        if (upper == "FALSE") return {TokenType::FALSE, "false", 0.0, tokenLine};
        
        // Identifier
        return {TokenType::ID, s, 0.0, tokenLine};
    }
    
    // Operators and punctuation
    if (!eof) {
        if (ch == '+') { read(); return {TokenType::PLUS, "+", 0.0, tokenLine}; }
        if (ch == '-') { read(); return {TokenType::MINUS, "-", 0.0, tokenLine}; }
        if (ch == '*') { read(); return {TokenType::MUL, "*", 0.0, tokenLine}; }
        if (ch == '/') { read(); return {TokenType::DIV, "/", 0.0, tokenLine}; }
        if (ch == '%') { read(); return {TokenType::MOD, "%", 0.0, tokenLine}; }
        if (ch == '=') { 
            read(); 
            if (!eof && ch == '=') { read(); return {TokenType::EQ, "==", 0.0, tokenLine}; }
            return {TokenType::ASSIGN, "=", 0.0, tokenLine}; 
        }
        if (ch == '<') {
            read();
            if (!eof && ch == '=') { read(); return {TokenType::LE, "<=", 0.0, tokenLine}; }
            if (!eof && ch == '>') { read(); return {TokenType::NE, "<>", 0.0, tokenLine}; }
            if (!eof && ch == '<') { read(); return {TokenType::SHL, "<<", 0.0, tokenLine}; }
            return {TokenType::LT, "<", 0.0, tokenLine};
        }
        if (ch == '>') {
            read();
            if (!eof && ch == '=') { read(); return {TokenType::GE, ">=", 0.0, tokenLine}; }
            if (!eof && ch == '>') { read(); return {TokenType::SHR, ">>", 0.0, tokenLine}; }
            return {TokenType::GT, ">", 0.0, tokenLine};
        }
        if (ch == ';') { read(); return {TokenType::SEMI, ";", 0.0, tokenLine}; }
        if (ch == ',') { read(); return {TokenType::COMMA, ",", 0.0, tokenLine}; }
        if (ch == '(') { read(); return {TokenType::LPAREN, "(", 0.0, tokenLine}; }
        if (ch == ')') { read(); return {TokenType::RPAREN, ")", 0.0, tokenLine}; }
        if (ch == '.') { read(); return {TokenType::DOT, ".", 0.0, tokenLine}; }
        if (ch == '&') { read(); return {TokenType::AMPERSAND, "&", 0.0, tokenLine}; }
        
        // Phase 7: Apostrophe comment (VB-style)
        if (ch == '\'') {
            // Comment - consume rest of line and return next token
            while (!eof && ch != '\n') {
                read();
            }
            return nextToken();
        }
        
        char c = ch;
        read();
        error("Invalid character '" + string(1, c) + "' at line " + to_string(tokenLine));
    }
    
    return {TokenType::END, "", 0.0, tokenLine};
}

