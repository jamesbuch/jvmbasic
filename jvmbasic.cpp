#include <bits/stdc++.h>
#include "ast.h"
#include "builtin_functions.h"
#include "codegen.h"

using namespace std;

using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// TokenType remains here (Lexer-specific, not in AST)
enum class TokenType { END, NUMBER, STRING, ID, PLUS, MINUS, MUL, DIV, MOD, ASSIGN, SEMI, COMMA, LPAREN, RPAREN, 
                       PRINT, LET, INPUT, DIM, LT, GT, LE, GE, EQ, NE, 
                       TRUE, FALSE, IF, THEN, ELSE, ENDIF, ELSEIF,
                       FOR, TO, STEP, NEXT, WHILE, ENDWHILE, WEND, DO, UNTIL,
                       FUNCTION, ENDFUNCTION, SUB, ENDSUB, RETURN, CALL };

// Token (Lexer-specific)
struct Token {
    TokenType type;
    string val;
    double num = 0.0;
    int line = 1;
};

// CallSite for type inference (Parser-specific)
struct CallSite {
    string funcName;
    vector<Type> argTypes;
    int line;
};


// Lexer
class Lexer {
private:
    istream& in;
    char ch = 0;
    bool eof = false;
    int line = 1;  // Track current line number

    void read() {
        if (!in.get(ch)) {
            eof = true;
            ch = 0;
        } else if (ch == '\n') {
            line++;
        }
    }

    void skipWhite() {
        while (!eof && isspace(ch)) {
            read();
        }
    }

public:
    Lexer(istream& i) : in(i) { read(); skipWhite(); }
    
    int getLine() const { return line; }

    Token nextToken() {
        skipWhite();
        int tokenLine = line;  // Capture line at start of token
        if (eof) return {TokenType::END, "", 0.0, tokenLine};

        if (isdigit(ch) || ch == '.') {
            string s;
            bool hasDot = false;
            if (ch == '.') {
                hasDot = true;
                s += ch;
                read();
            }
            while (!eof && isdigit(ch)) {
                s += ch;
                read();
            }
            if (!eof && ch == '.') {
                if (hasDot) error("Invalid number: multiple decimal points");
                hasDot = true;
                s += ch;
                read();
                while (!eof && isdigit(ch)) {
                    s += ch;
                    read();
                }
            }
            Token t{TokenType::NUMBER, s, s.empty() ? 0.0 : stod(s), tokenLine};
            return t;
        } else if (ch == '"') {
            read();
            string s;
            while (!eof && ch != '"') {
                s += ch;
                read();
            }
            if (!eof && ch == '"') read();
            else if (eof) error("Unterminated string at line " + to_string(tokenLine));
            return {TokenType::STRING, s, 0.0, tokenLine};
        } else if (!eof && isalpha(ch)) {
            string s;
            while (!eof && isalnum(ch)) {
                s += ch;
                read();
            }
            // Convert to uppercase for keyword matching
            string upper = s;
            for (auto& c : upper) c = toupper(c);
            
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
            if (upper == "FUNCTION") return {TokenType::FUNCTION, s, 0.0, tokenLine};
            if (upper == "ENDFUNCTION") return {TokenType::ENDFUNCTION, s, 0.0, tokenLine};
            if (upper == "SUB") return {TokenType::SUB, s, 0.0, tokenLine};
            if (upper == "ENDSUB") return {TokenType::ENDSUB, s, 0.0, tokenLine};
            if (upper == "RETURN") return {TokenType::RETURN, s, 0.0, tokenLine};
            if (upper == "CALL") return {TokenType::CALL, s, 0.0, tokenLine};
            if (upper == "END") {
                // Check if next token is IF
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
                    // Not "END IF", so this is an error or undefined ID
                    error("Expected IF after END at line " + to_string(tokenLine));
                }
                error("Expected IF after END at line " + to_string(tokenLine));
            }
            // Boolean literals (case-insensitive, normalized to lowercase)
            if (upper == "TRUE") return {TokenType::TRUE, "true", 0.0, tokenLine};
            if (upper == "FALSE") return {TokenType::FALSE, "false", 0.0, tokenLine};
            
            return {TokenType::ID, s, 0.0, tokenLine};
        } else if (!eof) {
            if (ch == '+') { read(); return {TokenType::PLUS, "+", 0.0, tokenLine}; }
            else if (ch == '-') { read(); return {TokenType::MINUS, "-", 0.0, tokenLine}; }
            else if (ch == '*') { read(); return {TokenType::MUL, "*", 0.0, tokenLine}; }
            else if (ch == '/') { read(); return {TokenType::DIV, "/", 0.0, tokenLine}; }
            else if (ch == '%') { read(); return {TokenType::MOD, "%", 0.0, tokenLine}; }
            else if (ch == '=') { 
                read(); 
                if (!eof && ch == '=') { read(); return {TokenType::EQ, "==", 0.0, tokenLine}; }
                return {TokenType::ASSIGN, "=", 0.0, tokenLine}; 
            }
            else if (ch == '<') {
                read();
                if (!eof && ch == '=') { read(); return {TokenType::LE, "<=", 0.0, tokenLine}; }
                if (!eof && ch == '>') { read(); return {TokenType::NE, "<>", 0.0, tokenLine}; }
                return {TokenType::LT, "<", 0.0, tokenLine};
            }
            else if (ch == '>') {
                read();
                if (!eof && ch == '=') { read(); return {TokenType::GE, ">=", 0.0, tokenLine}; }
                return {TokenType::GT, ">", 0.0, tokenLine};
            }
            else if (ch == ';') { read(); return {TokenType::SEMI, ";", 0.0, tokenLine}; }
            else if (ch == ',') { read(); return {TokenType::COMMA, ",", 0.0, tokenLine}; }
            else if (ch == '(') { read(); return {TokenType::LPAREN, "(", 0.0, tokenLine}; }
            else if (ch == ')') { read(); return {TokenType::RPAREN, ")", 0.0, tokenLine}; }
            else {
                char c = ch;
                read();
                error("Invalid character '" + string(1, c) + "' at line " + to_string(tokenLine));
            }
        }
        return {TokenType::END, "", 0.0, tokenLine};
    }

private:
    void error(const string& msg) { throw runtime_error(msg); }
};

// Parser
class Parser {
private:
    Lexer lex;
    Token tok;
    map<string, Type> knownTypes;
    map<string, pair<vector<Type>, Type>> userFunctions;  // name -> (param types, return type)
    map<string, vector<Type>> userSubs;  // name -> param types
    vector<CallSite> callSites;  // Collect call sites for type inference

    void next() { tok = lex.nextToken(); }
    Token expect(TokenType tt) {
        if (tok.type == tt) {
            Token res = tok;
            next();
            return res;
        }
        string expected = tokenTypeName(tt);
        string got = tok.val.empty() ? tokenTypeName(tok.type) : "'" + tok.val + "'";
        error("Expected " + expected + " but got " + got);
        return tok;  // Unreachable
    }
    
    void error(const string& msg) { 
        throw runtime_error("Line " + to_string(tok.line) + ": " + msg);
    }
    
    string tokenTypeName(TokenType tt) {
        switch(tt) {
            case TokenType::END: return "end of file";
            case TokenType::NUMBER: return "number";
            case TokenType::STRING: return "string";
            case TokenType::ID: return "identifier";
            case TokenType::LPAREN: return "'('";
            case TokenType::RPAREN: return "')'";
            case TokenType::COMMA: return "','";
            case TokenType::ASSIGN: return "'='";
            case TokenType::THEN: return "THEN";
            case TokenType::ENDIF: return "ENDIF";
            case TokenType::ENDFUNCTION: return "ENDFUNCTION";
            case TokenType::ENDSUB: return "ENDSUB";
            case TokenType::NEXT: return "NEXT";
            case TokenType::ENDWHILE: return "ENDWHILE";
            default: return "token";
        }
    }

    ExprPtr parsePrimary() {
        // Handle unary minus
        if (tok.type == TokenType::MINUS) {
            next();
            auto operand = parsePrimary();
            Type opType = operand->type;  // Capture type before move
            return make_unique<Expr>(ExprKind::Unary, opType, UnaryExpr{UnaryOp::Neg, move(operand)});
        }
        
        if (tok.type == TokenType::NUMBER) {
            Token nt = expect(TokenType::NUMBER);
            bool isFloat = nt.val.find('.') != string::npos;
            Type ty = isFloat ? Type::Float : Type::Int;
            return make_unique<Expr>(ExprKind::Num, ty, NumLit{nt.num});
        } else if (tok.type == TokenType::STRING) {
            Token st = expect(TokenType::STRING);
            return make_unique<Expr>(ExprKind::Str, Type::String, StrLit{st.val});
        } else if (tok.type == TokenType::TRUE) {
            next();
            return make_unique<Expr>(ExprKind::BoolLit, Type::Bool, BoolLit{true});
        } else if (tok.type == TokenType::FALSE) {
            next();
            return make_unique<Expr>(ExprKind::BoolLit, Type::Bool, BoolLit{false});
        } else if (tok.type == TokenType::ID) {
            string name = tok.val;
            string nameUpper = name;
            for (auto& c : nameUpper) c = toupper(c);
            next();
            
            // Check if it's a function call (check user functions first, then built-in)
            auto userFuncIt = userFunctions.find(name);
            if (userFuncIt != userFunctions.end()) {
                // User-defined function call
                const auto& [paramTypes, returnType] = userFuncIt->second;
                vector<ExprPtr> args;
                
                expect(TokenType::LPAREN);
                if (tok.type != TokenType::RPAREN) {
                    args.push_back(parseExpr());
                    while (tok.type == TokenType::COMMA) {
                        next();
                        args.push_back(parseExpr());
                    }
                }
                expect(TokenType::RPAREN);
                
                // Record call site for type inference
                vector<Type> argTypes;
                for (const auto& arg : args) {
                    argTypes.push_back(arg->type);
                }
                callSites.push_back(CallSite{name, argTypes, tok.line});
                
                // Type check arguments
                if (!paramTypes.empty() && args.size() != paramTypes.size()) {
                    error("Wrong number of arguments for function " + name);
                }
                
                return make_unique<Expr>(ExprKind::Call, returnType, CallExpr{name, move(args)});
            }
            
            auto funcIt = builtinFunctions.find(nameUpper);
            if (funcIt != builtinFunctions.end()) {
                // Built-in function call
                const FunctionSig& sig = funcIt->second;
                vector<ExprPtr> args;
                
                if (sig.paramTypes.empty()) {
                    // No-arg function, but might have empty parens
                    if (tok.type == TokenType::LPAREN) {
                        next();
                        expect(TokenType::RPAREN);
                    }
                } else {
                    // Parse arguments
                    expect(TokenType::LPAREN);
                    for (size_t i = 0; i < sig.paramTypes.size(); ++i) {
                        args.push_back(parseExpr());
                        
                        // Check argument type (allow Int->Float promotion)
                        Type expectedType = sig.paramTypes[i];
                        Type actualType = args.back()->type;
                        
                        if (actualType != expectedType) {
                            // Allow Int->Float promotion
                            if (!(expectedType == Type::Float && actualType == Type::Int)) {
                                error("Type mismatch in function argument for " + nameUpper);
                            }
                        }
                        
                        if (i < sig.paramTypes.size() - 1) {
                            expect(TokenType::COMMA);
                        }
                    }
                    expect(TokenType::RPAREN);
                }
                
                return make_unique<Expr>(ExprKind::Call, sig.returnType, CallExpr{nameUpper, move(args)});
            }
            
            // Not a function, check for array indexing or variable
            ExprPtr index = nullptr;
            Type varType;
            
            if (tok.type == TokenType::LPAREN) {
                // Array access: name(index)
                next();
                index = parseExpr();
                expect(TokenType::RPAREN);
                
                // Check variable is defined
                auto it = knownTypes.find(name);
                if (it == knownTypes.end()) error("Undefined variable: " + name);
                
                Type arrType = it->second;
                // Get element type from array type
                if (arrType == Type::IntArray) varType = Type::Int;
                else if (arrType == Type::FloatArray) varType = Type::Float;
                else if (arrType == Type::StringArray) varType = Type::String;
                else if (arrType == Type::BoolArray) varType = Type::Bool;
                else if (arrType == Type::Float) {
                    // Parameter typed as Float during parsing, might be array
                    // Assume Float element, will be refined by type inference
                    varType = Type::Float;
                }
                else if (arrType == Type::Int) {
                    varType = Type::Int;  // Might be array parameter
                }
                else error("Variable is not an array: " + name);
            } else {
                // Scalar variable access (or array reference for function calls)
                auto it = knownTypes.find(name);
                if (it == knownTypes.end()) error("Undefined variable: " + name);
                varType = it->second;
                
                // Arrays without indices are allowed (for passing to functions)
                // The type stays as IntArray, FloatArray, etc.
            }
            
            return make_unique<Expr>(ExprKind::Var, varType, VarRef{name, move(index)});
        } else if (tok.type == TokenType::LPAREN) {
            next();
            auto e = parseExpr();
            expect(TokenType::RPAREN);
            return e;
        }
        error("Unexpected token in expression: '" + tok.val + "'");
        return nullptr;
    }

    ExprPtr parseMul() {
        auto left = parsePrimary();
        while (tok.type == TokenType::MUL || tok.type == TokenType::DIV || tok.type == TokenType::MOD) {
            Op op = (tok.type == TokenType::MUL) ? Op::Mul : (tok.type == TokenType::DIV ? Op::Div : Op::Mod);
            next();
            auto right = parsePrimary();
            if (left->type == Type::String || right->type == Type::String) {
                error("String not allowed for * / MOD");
            }
            // Promote Int to Float if needed
            Type resultType = (left->type == Type::Float || right->type == Type::Float) ? Type::Float : Type::Int;
            auto bin = make_unique<Expr>(ExprKind::Bin, resultType, BinOp{op, move(left), move(right)});
            left = move(bin);
        }
        return left;
    }
    
    ExprPtr parseAdd() {
        auto left = parseMul();
        while (tok.type == TokenType::PLUS || tok.type == TokenType::MINUS) {
            Op op = (tok.type == TokenType::PLUS) ? Op::Add : Op::Sub;
            next();
            auto right = parseMul();
            if (left->type == Type::String || right->type == Type::String) {
                error("String not allowed for + -");
            }
            // Promote Int to Float if needed
            Type resultType = (left->type == Type::Float || right->type == Type::Float) ? Type::Float : Type::Int;
            auto bin = make_unique<Expr>(ExprKind::Bin, resultType, BinOp{op, move(left), move(right)});
            left = move(bin);
        }
        return left;
    }

    ExprPtr parseRel() {
        auto left = parseAdd();
        while (tok.type == TokenType::LT || tok.type == TokenType::GT || 
               tok.type == TokenType::LE || tok.type == TokenType::GE) {
            Op op;
            if (tok.type == TokenType::LT) op = Op::Lt;
            else if (tok.type == TokenType::GT) op = Op::Gt;
            else if (tok.type == TokenType::LE) op = Op::Le;
            else op = Op::Ge;
            next();
            auto right = parseAdd();
            // Comparisons always produce Bool type
            auto cmp = make_unique<Expr>(ExprKind::Cmp, Type::Bool, CmpOp{op, move(left), move(right)});
            left = move(cmp);
        }
        return left;
    }

    ExprPtr parseEq() {
        auto left = parseRel();
        while (tok.type == TokenType::EQ || tok.type == TokenType::NE) {
            Op op = (tok.type == TokenType::EQ) ? Op::Eq : Op::Ne;
            next();
            auto right = parseRel();
            // Comparisons always produce Bool type
            auto cmp = make_unique<Expr>(ExprKind::Cmp, Type::Bool, CmpOp{op, move(left), move(right)});
            left = move(cmp);
        }
        return left;
    }

    ExprPtr parseExpr() { return parseEq(); }

    DeclPtr parseDecl() {
        if (tok.type == TokenType::FUNCTION) {
            next();
            string name = expect(TokenType::ID).val;
            expect(TokenType::LPAREN);
            
            // Parse parameters
            vector<Param> params;
            if (tok.type != TokenType::RPAREN) {
                string paramName = expect(TokenType::ID).val;
                params.push_back(Param{paramName, Type::Int});  // Type will be inferred
                while (tok.type == TokenType::COMMA) {
                    next();
                    paramName = expect(TokenType::ID).val;
                    params.push_back(Param{paramName, Type::Int});
                }
            }
            expect(TokenType::RPAREN);
            
            // Register function BEFORE parsing body (enables recursion)
            // We'll use Float as default return type, will be refined later
            userFunctions[name] = {vector<Type>(), Type::Float};
            
            // Save current known types and create new scope
            map<string, Type> savedTypes = knownTypes;
            // Register parameters in function scope as Float (scalar default)
            for (const auto& param : params) {
                knownTypes[param.name] = Type::Float;  // Will be inferred correctly later
            }
            
            // Parse body
            vector<StmtPtr> body;
            while (tok.type != TokenType::ENDFUNCTION && tok.type != TokenType::END) {
                body.push_back(parseStmt());
            }
            expect(TokenType::ENDFUNCTION);
            
            // Infer return type from RETURN statements (default to Float for now)
            Type returnType = Type::Float;
            for (const auto& stmt : body) {
                if (stmt->kind == StmtKind::Return) {
                    const ReturnStmt& rs = get<ReturnStmt>(stmt->data);
                    if (rs.expr) {
                        returnType = rs.expr->type;
                        break;
                    }
                }
            }
            
            // Restore known types
            knownTypes = savedTypes;
            
            return make_unique<Decl>(DeclKind::Function, FunctionDecl{name, params, returnType, move(body)});
        } else if (tok.type == TokenType::SUB) {
            next();
            string name = expect(TokenType::ID).val;
            expect(TokenType::LPAREN);
            
            // Parse parameters
            vector<Param> params;
            if (tok.type != TokenType::RPAREN) {
                string paramName = expect(TokenType::ID).val;
                params.push_back(Param{paramName, Type::Int});
                while (tok.type == TokenType::COMMA) {
                    next();
                    paramName = expect(TokenType::ID).val;
                    params.push_back(Param{paramName, Type::Int});
                }
            }
            expect(TokenType::RPAREN);
            
            // Register SUB BEFORE parsing body (enables recursion)
            userSubs[name] = vector<Type>();
            
            // Save current known types and create new scope
            map<string, Type> savedTypes = knownTypes;
            // Register parameters in function scope as Float (scalar default)
            for (const auto& param : params) {
                knownTypes[param.name] = Type::Float;  // Will be inferred correctly later
            }
            
            // Parse body
            vector<StmtPtr> body;
            while (tok.type != TokenType::ENDSUB && tok.type != TokenType::END) {
                body.push_back(parseStmt());
            }
            expect(TokenType::ENDSUB);
            
            // Restore known types
            knownTypes = savedTypes;
            
            return make_unique<Decl>(DeclKind::Sub, SubDecl{name, params, move(body)});
        }
        error("Expected FUNCTION or SUB");
        return nullptr;
    }

    StmtPtr parseStmt() {
        if (tok.type == TokenType::PRINT) {
            next();
            vector<ExprPtr> exprs;
            vector<PrintSep> seps;
            bool addNewline = true;
            
            // Parse first expression
            exprs.push_back(parseExpr());
            
            // Parse additional expressions with separators
            while (tok.type == TokenType::COMMA || tok.type == TokenType::SEMI) {
                PrintSep sep = (tok.type == TokenType::COMMA) ? PrintSep::Comma : PrintSep::Semi;
                seps.push_back(sep);
                next();
                
                // Check if this is a trailing separator (no expression follows)
                if (tok.type == TokenType::END || tok.type == TokenType::PRINT || 
                    tok.type == TokenType::LET || tok.type == TokenType::INPUT || 
                    tok.type == TokenType::DIM || tok.type == TokenType::IF ||
                    tok.type == TokenType::FOR || tok.type == TokenType::WHILE || tok.type == TokenType::DO ||
                    tok.type == TokenType::NEXT || tok.type == TokenType::ENDWHILE || tok.type == TokenType::WEND) {
                    addNewline = false;
                    break;
                }
                
                exprs.push_back(parseExpr());
            }
            
            return make_unique<Stmt>(StmtKind::Print, PrintStmt{move(exprs), move(seps), addNewline});
        } else if (tok.type == TokenType::LET) {
            next();
            string var = expect(TokenType::ID).val;
            
            // Check for array assignment: LET arr(index) = value
            ExprPtr index = nullptr;
            if (tok.type == TokenType::LPAREN) {
                next();
                index = parseExpr();
                expect(TokenType::RPAREN);
            }
            
            expect(TokenType::ASSIGN);
            auto e = parseExpr();
            Type ty = e->type;
            
            if (index) {
                // Array element assignment
                auto it = knownTypes.find(var);
                if (it == knownTypes.end()) error("Undefined array: " + var);
                
                Type arrType = it->second;
                Type elemType;
                if (arrType == Type::IntArray) elemType = Type::Int;
                else if (arrType == Type::FloatArray) elemType = Type::Float;
                else if (arrType == Type::StringArray) elemType = Type::String;
                else if (arrType == Type::BoolArray) elemType = Type::Bool;
                else error("Variable is not an array: " + var);
                
                if (ty != elemType) error("Type mismatch in array assignment");
            } else {
                // Scalar assignment
                if (knownTypes.count(var) && knownTypes[var] != ty) error("Type mismatch reassign");
                knownTypes[var] = ty;
            }
            
            return make_unique<Stmt>(StmtKind::Let, LetStmt{var, move(e), move(index)});
        } else if (tok.type == TokenType::INPUT) {
            next();
            string var = expect(TokenType::ID).val;
            // Variable must already be defined to know its type
            if (knownTypes.find(var) == knownTypes.end()) {
                error("INPUT variable must be defined first with LET");
            }
            return make_unique<Stmt>(StmtKind::Input, InputStmt{var, nullptr});
        } else if (tok.type == TokenType::DIM) {
            next();
            string var = expect(TokenType::ID).val;
            expect(TokenType::LPAREN);
            auto size = parseExpr();
            expect(TokenType::RPAREN);
            expect(TokenType::ASSIGN);
            auto initVal = parseExpr();
            
            // Infer array type from init value
            Type elemType = initVal->type;
            Type arrType;
            if (elemType == Type::Int) arrType = Type::IntArray;
            else if (elemType == Type::Float) arrType = Type::FloatArray;
            else if (elemType == Type::String) arrType = Type::StringArray;
            else if (elemType == Type::Bool) arrType = Type::BoolArray;
            else error("Invalid array element type");
            
            if (knownTypes.count(var)) error("Variable already defined: " + var);
            knownTypes[var] = arrType;
            
            return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, move(size), move(initVal)});
        } else if (tok.type == TokenType::FOR) {
            next();
            string var = expect(TokenType::ID).val;
            expect(TokenType::ASSIGN);
            auto start = parseExpr();
            expect(TokenType::TO);
            auto end = parseExpr();
            
            ExprPtr step = nullptr;
            if (tok.type == TokenType::STEP) {
                next();
                step = parseExpr();
            }
            
            // Register loop variable type BEFORE parsing body
            Type loopType = start->type;
            if (knownTypes.count(var) && knownTypes[var] != loopType) {
                error("FOR loop variable type mismatch");
            }
            knownTypes[var] = loopType;
            
            // Parse body
            vector<StmtPtr> body;
            while (tok.type != TokenType::NEXT && tok.type != TokenType::END) {
                body.push_back(parseStmt());
            }
            
            expect(TokenType::NEXT);
            // Optional: consume variable name after NEXT
            if (tok.type == TokenType::ID) {
                next();
            }
            
            return make_unique<Stmt>(StmtKind::For, ForStmt{var, move(start), move(end), move(step), move(body)});
        } else if (tok.type == TokenType::WHILE) {
            next();
            auto cond = parseExpr();
            
            // Parse body
            vector<StmtPtr> body;
            while (tok.type != TokenType::ENDWHILE && tok.type != TokenType::WEND && tok.type != TokenType::END) {
                body.push_back(parseStmt());
            }
            
            if (tok.type == TokenType::ENDWHILE || tok.type == TokenType::WEND) {
                next();
            } else {
                error("Expected ENDWHILE or WEND");
            }
            
            return make_unique<Stmt>(StmtKind::While, WhileStmt{move(cond), move(body)});
        } else if (tok.type == TokenType::DO) {
            next();
            
            // Parse body
            vector<StmtPtr> body;
            while (tok.type != TokenType::WHILE && tok.type != TokenType::UNTIL && tok.type != TokenType::END) {
                body.push_back(parseStmt());
            }
            
            bool isUntil = false;
            if (tok.type == TokenType::WHILE) {
                next();
            } else if (tok.type == TokenType::UNTIL) {
                isUntil = true;
                next();
            } else {
                error("Expected WHILE or UNTIL after DO");
            }
            
            auto cond = parseExpr();
            
            return make_unique<Stmt>(StmtKind::DoWhile, DoWhileStmt{move(cond), move(body), isUntil});
        } else if (tok.type == TokenType::IF) {
            next();
            auto cond = parseExpr();
            if (cond->type != Type::Bool && cond->type != Type::Int) {
                error("IF condition must be boolean or integer");
            }
            expect(TokenType::THEN);
            
            // Parse THEN body
            vector<StmtPtr> thenBody;
            while (tok.type != TokenType::ELSEIF && tok.type != TokenType::ELSE && 
                   tok.type != TokenType::ENDIF && tok.type != TokenType::END) {
                thenBody.push_back(parseStmt());
            }
            
            // Parse ELSE IF clauses
            vector<ElseIfClause> elseIfs;
            while (tok.type == TokenType::ELSEIF) {
                next();
                auto elseIfCond = parseExpr();
                if (elseIfCond->type != Type::Bool && elseIfCond->type != Type::Int) {
                    error("ELSE IF condition must be boolean or integer");
                }
                expect(TokenType::THEN);
                vector<StmtPtr> elseIfBody;
                while (tok.type != TokenType::ELSEIF && tok.type != TokenType::ELSE && 
                       tok.type != TokenType::ENDIF && tok.type != TokenType::END) {
                    elseIfBody.push_back(parseStmt());
                }
                elseIfs.push_back(ElseIfClause{move(elseIfCond), move(elseIfBody)});
            }
            
            // Parse ELSE body
            vector<StmtPtr> elseBody;
            if (tok.type == TokenType::ELSE) {
                next();
                while (tok.type != TokenType::ENDIF && tok.type != TokenType::END) {
                    elseBody.push_back(parseStmt());
                }
            }
            
            // Expect ENDIF or END IF
            if (tok.type == TokenType::ENDIF) {
                next();
            } else if (tok.type == TokenType::END) {
                // END IF was already handled in lexer
                error("Expected ENDIF");
            } else {
                error("Expected ENDIF");
            }
            
            return make_unique<Stmt>(StmtKind::If, IfStmt{move(cond), move(thenBody), 
                                                           move(elseIfs), move(elseBody)});
        } else if (tok.type == TokenType::RETURN) {
            next();
            // Check if there's an expression to return
            if (tok.type == TokenType::END || tok.type == TokenType::ENDFUNCTION || tok.type == TokenType::ENDSUB) {
                // Empty return (for SUBs)
                return make_unique<Stmt>(StmtKind::Return, ReturnStmt{nullptr});
            } else {
                auto expr = parseExpr();
                return make_unique<Stmt>(StmtKind::Return, ReturnStmt{move(expr)});
            }
        } else if (tok.type == TokenType::CALL) {
            next();
            string name = expect(TokenType::ID).val;
            expect(TokenType::LPAREN);
            vector<ExprPtr> args;
            if (tok.type != TokenType::RPAREN) {
                args.push_back(parseExpr());
                while (tok.type == TokenType::COMMA) {
                    next();
                    args.push_back(parseExpr());
                }
            }
            expect(TokenType::RPAREN);
            
            // Record call site for type inference
            vector<Type> argTypes;
            for (const auto& arg : args) {
                argTypes.push_back(arg->type);
            }
            callSites.push_back(CallSite{name, argTypes, tok.line});
            
            return make_unique<Stmt>(StmtKind::CallStmt, CallStmtNode{name, move(args)});
        }
        error("Unexpected token in statement: '" + tok.val + "'");
        return nullptr;
    }

public:
    vector<DeclPtr> declarations;  // Function and sub declarations
    vector<StmtPtr> program;       // Main program statements
    
    const map<string, Type>& getKnownTypes() const { return knownTypes; }

    // Fix types in AST after parameter type inference
    void fixParameterTypesInAST() {
        for (auto& decl : declarations) {
            map<string, Type> paramTypes;
            vector<StmtPtr>* body = nullptr;
            
            if (decl->kind == DeclKind::Function) {
                FunctionDecl& fd = get<FunctionDecl>(decl->data);
                for (const auto& p : fd.params) {
                    paramTypes[p.name] = p.type;
                }
                body = &fd.body;
            } else if (decl->kind == DeclKind::Sub) {
                SubDecl& sd = get<SubDecl>(decl->data);
                for (const auto& p : sd.params) {
                    paramTypes[p.name] = p.type;
                }
                body = &sd.body;
            }
            
            if (!body) continue;
            
            // Fix expression types in function/sub body
            function<void(Expr&)> fixExpr = [&](Expr& e) {
                if (e.kind == ExprKind::Var && !get<VarRef>(e.data).index) {
                    // Scalar variable reference - check if it's a parameter
                    const VarRef& vr = get<VarRef>(e.data);
                    auto it = paramTypes.find(vr.name);
                    if (it != paramTypes.end()) {
                        e.type = it->second;  // Update to inferred type
                    }
                } else if (e.kind == ExprKind::Call) {
                    CallExpr& c = get<CallExpr>(e.data);
                    for (auto& arg : c.args) fixExpr(*arg);
                } else if (e.kind == ExprKind::Bin) {
                    BinOp& b = get<BinOp>(e.data);
                    fixExpr(*b.left);
                    fixExpr(*b.right);
                } else if (e.kind == ExprKind::Cmp) {
                    CmpOp& c = get<CmpOp>(e.data);
                    fixExpr(*c.left);
                    fixExpr(*c.right);
                } else if (e.kind == ExprKind::Unary) {
                    UnaryExpr& u = get<UnaryExpr>(e.data);
                    fixExpr(*u.operand);
                }
            };
            
            function<void(Stmt&)> fixStmt = [&](Stmt& s) {
                if (s.kind == StmtKind::Print) {
                    PrintStmt& ps = get<PrintStmt>(s.data);
                    for (auto& e : ps.exprs) fixExpr(*e);
                } else if (s.kind == StmtKind::Let) {
                    LetStmt& ls = get<LetStmt>(s.data);
                    if (ls.index) fixExpr(*ls.index);
                    fixExpr(*ls.expr);
                } else if (s.kind == StmtKind::Return) {
                    ReturnStmt& rs = get<ReturnStmt>(s.data);
                    if (rs.expr) fixExpr(*rs.expr);
                } else if (s.kind == StmtKind::CallStmt) {
                    CallStmtNode& cs = get<CallStmtNode>(s.data);
                    for (auto& arg : cs.args) fixExpr(*arg);
                } else if (s.kind == StmtKind::If) {
                    IfStmt& ifs = get<IfStmt>(s.data);
                    fixExpr(*ifs.cond);
                    for (auto& stmt : ifs.thenBody) fixStmt(*stmt);
                    for (auto& elif : ifs.elseIfs) {
                        fixExpr(*elif.cond);
                        for (auto& stmt : elif.body) fixStmt(*stmt);
                    }
                    for (auto& stmt : ifs.elseBody) fixStmt(*stmt);
                } else if (s.kind == StmtKind::For) {
                    ForStmt& fs = get<ForStmt>(s.data);
                    fixExpr(*fs.start);
                    fixExpr(*fs.end);
                    if (fs.step) fixExpr(*fs.step);
                    for (auto& stmt : fs.body) fixStmt(*stmt);
                } else if (s.kind == StmtKind::While) {
                    WhileStmt& ws = get<WhileStmt>(s.data);
                    fixExpr(*ws.cond);
                    for (auto& stmt : ws.body) fixStmt(*stmt);
                } else if (s.kind == StmtKind::DoWhile) {
                    DoWhileStmt& dws = get<DoWhileStmt>(s.data);
                    fixExpr(*dws.cond);
                    for (auto& stmt : dws.body) fixStmt(*stmt);
                }
            };
            
            for (auto& stmt : *body) {
                fixStmt(*stmt);
            }
        }
    }
 
    void inferParameterTypes() {
        // Group call sites by function name
        map<string, vector<vector<Type>>> callsByFunc;
        for (const auto& call : callSites) {
            callsByFunc[call.funcName].push_back(call.argTypes);
        }
        
        // Infer parameter types for each function/sub
        for (auto& decl : declarations) {
            string funcName;
            vector<Param>* params = nullptr;
            
            if (decl->kind == DeclKind::Function) {
                FunctionDecl& fd = get<FunctionDecl>(decl->data);
                funcName = fd.name;
                params = &fd.params;
            } else if (decl->kind == DeclKind::Sub) {
                SubDecl& sd = get<SubDecl>(decl->data);
                funcName = sd.name;
                params = &sd.params;
            }
            
            if (!params || params->empty()) continue;
            
            auto callsIt = callsByFunc.find(funcName);
            if (callsIt == callsByFunc.end() || callsIt->second.empty()) {
                // No calls found, use Float as default
                for (auto& param : *params) {
                    param.type = Type::Float;
                }
                continue;
            }
            
            // Infer parameter types by examining all calls
            const vector<Type>& firstCallArgs = callsIt->second[0];
            if (firstCallArgs.size() != params->size()) {
                error("Inconsistent number of arguments for " + funcName);
            }
            
            // Initialize with first call
            for (size_t i = 0; i < params->size(); ++i) {
                (*params)[i].type = firstCallArgs[i];
            }
            
            // Refine types by examining all calls
            for (size_t callIdx = 1; callIdx < callsIt->second.size(); ++callIdx) {
                const vector<Type>& callArgs = callsIt->second[callIdx];
                if (callArgs.size() != params->size()) {
                    error("Inconsistent number of arguments for " + funcName);
                }
                for (size_t i = 0; i < params->size(); ++i) {
                    Type paramType = (*params)[i].type;
                    Type argType = callArgs[i];
                    
                    if (paramType != argType) {
                        // If we have Int and Float, promote to Float
                        if ((paramType == Type::Int && argType == Type::Float) ||
                            (paramType == Type::Float && argType == Type::Int)) {
                            (*params)[i].type = Type::Float;
                        } else {
                            error("Type mismatch in arguments for " + funcName + " at parameter " + 
                                  to_string(i+1) + " (expected " + typeToString(paramType) + 
                                  " but got " + typeToString(argType) + ")");
                        }
                    }
                }
            }
        }
        
        // Update userFunctions and userSubs with inferred types
        for (const auto& decl : declarations) {
            if (decl->kind == DeclKind::Function) {
                const FunctionDecl& fd = get<FunctionDecl>(decl->data);
                vector<Type> paramTypes;
                for (const auto& p : fd.params) {
                    paramTypes.push_back(p.type);
                }
                userFunctions[fd.name] = {paramTypes, fd.returnType};
            } else if (decl->kind == DeclKind::Sub) {
                const SubDecl& sd = get<SubDecl>(decl->data);
                vector<Type> paramTypes;
                for (const auto& p : sd.params) {
                    paramTypes.push_back(p.type);
                }
                userSubs[sd.name] = paramTypes;
            }
        }
    }
    
    string typeToString(Type t) {
        switch(t) {
            case Type::Int: return "Int";
            case Type::Float: return "Float";
            case Type::String: return "String";
            case Type::Bool: return "Bool";
            case Type::IntArray: return "IntArray";
            case Type::FloatArray: return "FloatArray";
            case Type::StringArray: return "StringArray";
            case Type::BoolArray: return "BoolArray";
            default: return "Unknown";
        }
    }

    Parser(istream& i) : lex(i) { next(); }
    void parse() {
        // First, parse all function/sub declarations (without fixed parameter types)
        while (tok.type == TokenType::FUNCTION || tok.type == TokenType::SUB) {
            auto decl = parseDecl();
            
            // Register the function/sub with empty parameter types initially
            if (decl->kind == DeclKind::Function) {
                const FunctionDecl& fd = get<FunctionDecl>(decl->data);
                userFunctions[fd.name] = {vector<Type>(), fd.returnType};
            } else if (decl->kind == DeclKind::Sub) {
                const SubDecl& sd = get<SubDecl>(decl->data);
                userSubs[sd.name] = vector<Type>();
            }
            
            declarations.push_back(move(decl));
        }
        // Then parse main program statements
        while (tok.type != TokenType::END) {
            program.push_back(parseStmt());
        }
        
        // Infer parameter types from call sites (first pass)
        inferParameterTypes();
        
        // Fix parameter types in function/sub body ASTs
        fixParameterTypesInAST();
        
        // Re-infer to catch nested function calls with corrected types
        callSites.clear();  // Clear old call sites
        rebuildCallSites();  // Rebuild with corrected AST
        inferParameterTypes();  // Re-infer (should converge)
    }
    
    // Rebuild call sites after AST type fixing
    void rebuildCallSites() {
        function<void(const Expr&)> scanExpr = [&](const Expr& e) {
            if (e.kind == ExprKind::Call) {
                const CallExpr& ce = get<CallExpr>(e.data);
                vector<Type> argTypes;
                for (const auto& arg : ce.args) {
                    argTypes.push_back(arg->type);
                }
                callSites.push_back(CallSite{ce.name, argTypes, 0});
                for (const auto& arg : ce.args) scanExpr(*arg);
            } else if (e.kind == ExprKind::Bin) {
                const BinOp& b = get<BinOp>(e.data);
                scanExpr(*b.left);
                scanExpr(*b.right);
            } else if (e.kind == ExprKind::Cmp) {
                const CmpOp& c = get<CmpOp>(e.data);
                scanExpr(*c.left);
                scanExpr(*c.right);
            } else if (e.kind == ExprKind::Unary) {
                const UnaryExpr& u = get<UnaryExpr>(e.data);
                scanExpr(*u.operand);
            }
        };
        
        function<void(const Stmt&)> scanStmt = [&](const Stmt& s) {
            if (s.kind == StmtKind::Print) {
                const PrintStmt& ps = get<PrintStmt>(s.data);
                for (const auto& e : ps.exprs) scanExpr(*e);
            } else if (s.kind == StmtKind::Let) {
                const LetStmt& ls = get<LetStmt>(s.data);
                if (ls.index) scanExpr(*ls.index);
                scanExpr(*ls.expr);
            } else if (s.kind == StmtKind::Return) {
                const ReturnStmt& rs = get<ReturnStmt>(s.data);
                if (rs.expr) scanExpr(*rs.expr);
            } else if (s.kind == StmtKind::CallStmt) {
                const CallStmtNode& cs = get<CallStmtNode>(s.data);
                vector<Type> argTypes;
                for (const auto& arg : cs.args) {
                    argTypes.push_back(arg->type);
                }
                callSites.push_back(CallSite{cs.name, argTypes, 0});
                for (const auto& arg : cs.args) scanExpr(*arg);
            } else if (s.kind == StmtKind::If) {
                const IfStmt& ifs = get<IfStmt>(s.data);
                scanExpr(*ifs.cond);
                for (const auto& stmt : ifs.thenBody) scanStmt(*stmt);
                for (const auto& elif : ifs.elseIfs) {
                    scanExpr(*elif.cond);
                    for (const auto& stmt : elif.body) scanStmt(*stmt);
                }
                for (const auto& stmt : ifs.elseBody) scanStmt(*stmt);
            } else if (s.kind == StmtKind::For) {
                const ForStmt& fs = get<ForStmt>(s.data);
                scanExpr(*fs.start);
                scanExpr(*fs.end);
                if (fs.step) scanExpr(*fs.step);
                for (const auto& stmt : fs.body) scanStmt(*stmt);
            } else if (s.kind == StmtKind::While) {
                const WhileStmt& ws = get<WhileStmt>(s.data);
                scanExpr(*ws.cond);
                for (const auto& stmt : ws.body) scanStmt(*stmt);
            } else if (s.kind == StmtKind::DoWhile) {
                const DoWhileStmt& dws = get<DoWhileStmt>(s.data);
                scanExpr(*dws.cond);
                for (const auto& stmt : dws.body) scanStmt(*stmt);
            }
        };
        
        // Scan all function/sub bodies
        for (const auto& decl : declarations) {
            if (decl->kind == DeclKind::Function) {
                const FunctionDecl& fd = get<FunctionDecl>(decl->data);
                for (const auto& stmt : fd.body) {
                    scanStmt(*stmt);
                }
            } else if (decl->kind == DeclKind::Sub) {
                const SubDecl& sd = get<SubDecl>(decl->data);
                for (const auto& stmt : sd.body) {
                    scanStmt(*stmt);
                }
            }
        }
        
        // Scan main program
        for (const auto& stmt : program) {
            scanStmt(*stmt);
        }
    }
};
class BasicCompiler {
public:
    void compile(istream& input, ostream& output) {
        Parser p(input);
        p.parse();
        cf.buildConstantPool();
        cf.generate(p.declarations, p.program, p.getKnownTypes());
        cf.write(output);
    }

private:
    ClassFile cf;
};

int main() {
    BasicCompiler bc;
    try {
        ofstream out("BasicProgram.class", ios::binary);
        bc.compile(cin, out);
        cout << "Compiled to BasicProgram.class" << endl;
    } catch (const exception& e) {
        cerr << "Error: " << e.what() << endl;
        return 1;
    }
    return 0;
}
