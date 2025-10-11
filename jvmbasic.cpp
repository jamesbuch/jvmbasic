#include <bits/stdc++.h>

using namespace std;

using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Enums
enum class Type { Int, Float, String, Bool, IntArray, FloatArray, StringArray, BoolArray };
enum class Op { Add, Sub, Mul, Div, Mod, Lt, Gt, Le, Ge, Eq, Ne };
enum class TokenType { END, NUMBER, STRING, ID, PLUS, MINUS, MUL, DIV, MOD, ASSIGN, SEMI, COMMA, LPAREN, RPAREN, 
                       PRINT, LET, INPUT, DIM, LT, GT, LE, GE, EQ, NE, 
                       TRUE, FALSE, IF, THEN, ELSE, ENDIF, ELSEIF,
                       FOR, TO, STEP, NEXT, WHILE, ENDWHILE, WEND, DO, UNTIL,
                       FUNCTION, ENDFUNCTION, SUB, ENDSUB, RETURN, CALL };
enum class ExprKind { Num, Str, Var, Bin, BoolLit, Cmp, Call, Unary };
enum class UnaryOp { Neg };
enum class StmtKind { Print, Let, Input, Dim, If, For, While, DoWhile, Return, CallStmt };
enum class DeclKind { Function, Sub };

// Forward declarations
struct Expr;
struct Stmt;
struct Decl;
using ExprPtr = unique_ptr<Expr>;
using StmtPtr = unique_ptr<Stmt>;
using DeclPtr = unique_ptr<Decl>;

// Expr structures
struct NumLit { double value; };
struct StrLit { string value; };
struct VarRef { 
    string name;
    ExprPtr index; // nullptr for scalar, non-null for array access
};
struct BinOp { Op op; ExprPtr left, right; };
struct BoolLit { bool value; };
struct CmpOp { Op op; ExprPtr left, right; };
struct CallExpr { 
    string name; 
    vector<ExprPtr> args;
};

struct UnaryExpr {
    UnaryOp op;
    ExprPtr operand;
};

struct Expr {
    ExprKind kind;
    Type type;
    variant<NumLit, StrLit, VarRef, BinOp, BoolLit, CmpOp, CallExpr, UnaryExpr> data;

    Expr(ExprKind k, Type t, NumLit n) : kind(k), type(t), data(n) {}
    Expr(ExprKind k, Type t, StrLit s) : kind(k), type(t), data(s) {}
    Expr(ExprKind k, Type t, VarRef v) : kind(k), type(t), data(std::move(v)) {}
    Expr(ExprKind k, Type t, BinOp b) : kind(k), type(t), data(std::move(b)) {}
    Expr(ExprKind k, Type t, BoolLit bl) : kind(k), type(t), data(bl) {}
    Expr(ExprKind k, Type t, CmpOp c) : kind(k), type(t), data(std::move(c)) {}
    Expr(ExprKind k, Type t, CallExpr c) : kind(k), type(t), data(std::move(c)) {}
    Expr(ExprKind k, Type t, UnaryExpr u) : kind(k), type(t), data(std::move(u)) {}
};

// Stmt structures
enum class PrintSep { Comma, Semi };
struct PrintStmt { 
    vector<ExprPtr> exprs;
    vector<PrintSep> seps; // separators between expressions (size = exprs.size() - 1)
    bool addNewline; // false if statement ends with , or ;
};
struct LetStmt { 
    string var; 
    ExprPtr expr;
    ExprPtr index; // nullptr for scalar, non-null for array assignment
};
struct InputStmt { 
    string var;
    ExprPtr index; // nullptr for scalar, non-null for array input
};
struct DimStmt {
    string var;
    ExprPtr size;     // Array size expression
    ExprPtr initVal;  // Initial value for all elements
};
struct ElseIfClause { ExprPtr cond; vector<StmtPtr> body; };
struct IfStmt { 
    ExprPtr cond; 
    vector<StmtPtr> thenBody;
    vector<ElseIfClause> elseIfs;
    vector<StmtPtr> elseBody;
};
struct ForStmt {
    string var;
    ExprPtr start;
    ExprPtr end;
    ExprPtr step;  // nullptr means default 1
    vector<StmtPtr> body;
};
struct WhileStmt {
    ExprPtr cond;
    vector<StmtPtr> body;
};
struct DoWhileStmt {
    ExprPtr cond;
    vector<StmtPtr> body;
    bool isUntil;  // true for UNTIL, false for WHILE
};
struct ReturnStmt {
    ExprPtr expr;  // nullptr for void SUBs
};
struct CallStmtNode {
    string name;
    vector<ExprPtr> args;
};

struct Stmt {
    StmtKind kind;
    variant<PrintStmt, LetStmt, InputStmt, DimStmt, IfStmt, ForStmt, WhileStmt, DoWhileStmt, ReturnStmt, CallStmtNode> data;

    Stmt(StmtKind k, PrintStmt p) : kind(k), data(std::move(p)) {}
    Stmt(StmtKind k, LetStmt l) : kind(k), data(std::move(l)) {}
    Stmt(StmtKind k, InputStmt i) : kind(k), data(std::move(i)) {}
    Stmt(StmtKind k, DimStmt d) : kind(k), data(std::move(d)) {}
    Stmt(StmtKind k, IfStmt ifs) : kind(k), data(std::move(ifs)) {}
    Stmt(StmtKind k, ForStmt fs) : kind(k), data(std::move(fs)) {}
    Stmt(StmtKind k, WhileStmt ws) : kind(k), data(std::move(ws)) {}
    Stmt(StmtKind k, DoWhileStmt dws) : kind(k), data(std::move(dws)) {}
    Stmt(StmtKind k, ReturnStmt rs) : kind(k), data(std::move(rs)) {}
    Stmt(StmtKind k, CallStmtNode cs) : kind(k), data(std::move(cs)) {}
};

// Parameter for functions/subs
struct Param {
    string name;
    Type type;  // Inferred from call sites
};

// Call site information for type inference
struct CallSite {
    string funcName;
    vector<Type> argTypes;
    int line;
};

// Function/Sub declarations
struct FunctionDecl {
    string name;
    vector<Param> params;
    Type returnType;  // Inferred from RETURN statement
    vector<StmtPtr> body;
};
struct SubDecl {
    string name;
    vector<Param> params;
    vector<StmtPtr> body;
};

struct Decl {
    DeclKind kind;
    variant<FunctionDecl, SubDecl> data;

    Decl(DeclKind k, FunctionDecl f) : kind(k), data(std::move(f)) {}
    Decl(DeclKind k, SubDecl s) : kind(k), data(std::move(s)) {}
};

// Token
struct Token {
    TokenType type;
    string val;
    double num = 0.0;
    int line = 1;  // Line number for error reporting
};

// Function signature
struct FunctionSig {
    vector<Type> paramTypes;
    Type returnType;
    string javaMethod;  // Method name in BasicRuntime
    string javaDescriptor; // JVM method descriptor
};

// Global function registry
static map<string, FunctionSig> builtinFunctions = {
    // Math functions - single parameter
    {"ABS", {{Type::Float}, Type::Float, "abs_f", "(F)F"}},
    {"SQR", {{Type::Float}, Type::Float, "sqr", "(F)F"}},
    {"SQRT", {{Type::Float}, Type::Float, "sqr", "(F)F"}},  // Alias
    {"INT", {{Type::Float}, Type::Int, "int_f", "(F)I"}},
    {"SGN", {{Type::Float}, Type::Int, "sgn_f", "(F)I"}},
    {"SIN", {{Type::Float}, Type::Float, "sin", "(F)F"}},
    {"COS", {{Type::Float}, Type::Float, "cos", "(F)F"}},
    {"TAN", {{Type::Float}, Type::Float, "tan", "(F)F"}},
    {"ASIN", {{Type::Float}, Type::Float, "asin", "(F)F"}},
    {"ACOS", {{Type::Float}, Type::Float, "acos", "(F)F"}},
    {"ATAN", {{Type::Float}, Type::Float, "atan", "(F)F"}},
    {"EXP", {{Type::Float}, Type::Float, "exp", "(F)F"}},
    {"LOG", {{Type::Float}, Type::Float, "log", "(F)F"}},
    {"LOG10", {{Type::Float}, Type::Float, "log10", "(F)F"}},
    {"ROUND", {{Type::Float}, Type::Int, "round", "(F)I"}},
    {"CEIL", {{Type::Float}, Type::Float, "ceil", "(F)F"}},
    {"FLOOR", {{Type::Float}, Type::Float, "floor", "(F)F"}},
    
    // Math functions - no parameters
    {"RND", {{}, Type::Float, "rnd", "()F"}},
    {"PI", {{}, Type::Float, "pi", "()F"}},
    {"E", {{}, Type::Float, "e", "()F"}},

    // Math functions - one and two parameters, random numbers
    {"RNDI", {{Type::Int}, Type::Int, "rnd_i", "(I)I"}},
    {"RNDINT", {{Type::Int, Type::Int}, Type::Int, "rnd_i_ranged", "(II)I"}},
    
    // Math functions - two parameters
    {"POW", {{Type::Float, Type::Float}, Type::Float, "pow", "(FF)F"}},
    {"ATAN2", {{Type::Float, Type::Float}, Type::Float, "atan2", "(FF)F"}},
    {"MIN", {{Type::Float, Type::Float}, Type::Float, "min_ff", "(FF)F"}},
    {"MAX", {{Type::Float, Type::Float}, Type::Float, "max_ff", "(FF)F"}},
    
    // String functions - single parameter
    {"LEN", {{Type::String}, Type::Int, "len", "(Ljava/lang/String;)I"}},
    {"UPPER", {{Type::String}, Type::String, "upper", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"UCASE", {{Type::String}, Type::String, "upper", "(Ljava/lang/String;)Ljava/lang/String;"}},  // Alias
    {"LOWER", {{Type::String}, Type::String, "lower", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"LCASE", {{Type::String}, Type::String, "lower", "(Ljava/lang/String;)Ljava/lang/String;"}},  // Alias
    {"TRIM", {{Type::String}, Type::String, "trim", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"LTRIM", {{Type::String}, Type::String, "ltrim", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"RTRIM", {{Type::String}, Type::String, "rtrim", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"REVERSE", {{Type::String}, Type::String, "reverse", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"ASC", {{Type::String}, Type::Int, "asc", "(Ljava/lang/String;)I"}},
    
    // String functions - two parameters
    {"LEFT", {{Type::String, Type::Int}, Type::String, "left", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"RIGHT", {{Type::String, Type::Int}, Type::String, "right", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"INSTR", {{Type::String, Type::String}, Type::Int, "instr", "(Ljava/lang/String;Ljava/lang/String;)I"}},
    {"CONTAINS", {{Type::String, Type::String}, Type::Bool, "contains", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"SPACE", {{Type::Int}, Type::String, "space", "(I)Ljava/lang/String;"}},
    
    // String functions - three parameters
    {"MID", {{Type::String, Type::Int, Type::Int}, Type::String, "mid", "(Ljava/lang/String;II)Ljava/lang/String;"}},
    {"SUBSTR", {{Type::String, Type::Int, Type::Int}, Type::String, "mid", "(Ljava/lang/String;II)Ljava/lang/String;"}},  // Alias
    {"STRING", {{Type::Int, Type::String}, Type::String, "string", "(ILjava/lang/String;)Ljava/lang/String;"}},
    
    // Type conversion
    {"CHR", {{Type::Int}, Type::String, "chr", "(I)Ljava/lang/String;"}},
    {"VAL", {{Type::String}, Type::Float, "val_f", "(Ljava/lang/String;)F"}},
    
    // Type checking
    {"ISNUM", {{Type::String}, Type::Bool, "isnum", "(Ljava/lang/String;)Z"}},
    {"ISINT", {{Type::String}, Type::Bool, "isint", "(Ljava/lang/String;)Z"}},
    
    // Array utility functions (that return values)
    {"MINARRAY", {{Type::IntArray}, Type::Int, "min_ia", "([I)I"}},
    {"MAXARRAY", {{Type::IntArray}, Type::Int, "max_ia", "([I)I"}},
    {"SUMARRAY", {{Type::IntArray}, Type::Int, "sum_ia", "([I)I"}},
    {"UBOUND", {{Type::IntArray}, Type::Int, "ubound_ia", "([I)I"}},
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
                
                // Check variable is defined and is an array
                auto it = knownTypes.find(name);
                if (it == knownTypes.end()) error("Undefined array: " + name);
                
                Type arrType = it->second;
                // Get element type from array type
                if (arrType == Type::IntArray) varType = Type::Int;
                else if (arrType == Type::FloatArray) varType = Type::Float;
                else if (arrType == Type::StringArray) varType = Type::String;
                else if (arrType == Type::BoolArray) varType = Type::Bool;
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
            
            // Save current known types and create new scope
            map<string, Type> savedTypes = knownTypes;
            // Register parameters in function scope
            for (const auto& param : params) {
                knownTypes[param.name] = Type::Float;  // Default to Float for flexibility
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
            
            // Save current known types and create new scope
            map<string, Type> savedTypes = knownTypes;
            // Register parameters in function scope
            for (const auto& param : params) {
                knownTypes[param.name] = Type::Float;  // Default to Float for flexibility
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
        
        // Infer parameter types from call sites
        inferParameterTypes();
    }
};

// Constant Pool Entry
using CpEntry = vector<u1>;

// Method information for multi-method generation
struct MethodInfo {
    u2 name_idx;
    u2 descriptor_idx;
    u2 access_flags;
    vector<u1> code;
    u2 max_stack;
    u2 max_locals;
};

struct ConstantPool {
    vector<CpEntry> entries;

    u2 addEntry(CpEntry e) {
        // Simple no dedup for brevity
        entries.push_back(move(e));
        return static_cast<u2>(entries.size());
    }

    u2 addUtf8(const string& s) {
        CpEntry e;
        e.push_back(1); // tag
        u2 len = static_cast<u2>(s.size());
        e.push_back(static_cast<u1>(len >> 8));
        e.push_back(static_cast<u1>(len & 0xFF));
        for (char c : s) e.push_back(static_cast<u1>(c));
        return addEntry(move(e));
    }

    u2 addString(u2 strIdx) {
        CpEntry e{8, static_cast<u1>(strIdx >> 8), static_cast<u1>(strIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addFloat(u4 bits) {
        CpEntry e{4};
        e.push_back(static_cast<u1>((bits >> 24) & 0xFF));
        e.push_back(static_cast<u1>((bits >> 16) & 0xFF));
        e.push_back(static_cast<u1>((bits >> 8) & 0xFF));
        e.push_back(static_cast<u1>(bits & 0xFF));
        return addEntry(move(e));
    }

    u2 addNameAndType(u2 nameIdx, u2 descIdx) {
        CpEntry e{12, static_cast<u1>(nameIdx >> 8), static_cast<u1>(nameIdx & 0xFF),
                  static_cast<u1>(descIdx >> 8), static_cast<u1>(descIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addClass(u2 nameIdx) {
        CpEntry e{7, static_cast<u1>(nameIdx >> 8), static_cast<u1>(nameIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addFieldRef(u2 classIdx, u2 natIdx) {
        CpEntry e{9, static_cast<u1>(classIdx >> 8), static_cast<u1>(classIdx & 0xFF),
                  static_cast<u1>(natIdx >> 8), static_cast<u1>(natIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addMethodRef(u2 classIdx, u2 natIdx) {
        CpEntry e{10, static_cast<u1>(classIdx >> 8), static_cast<u1>(classIdx & 0xFF),
                   static_cast<u1>(natIdx >> 8), static_cast<u1>(natIdx & 0xFF)};
        return addEntry(move(e));
    }
};

// Label for branching
struct Label {
    int pos = -1;
    vector<int> patchSites;
};

// ClassFile
class ClassFile {
public:
    u4 magic = 0xCAFEBABE;
    u2 minor_version = 0;
    u2 major_version = 50; // Java 6 (avoids StackMapTable requirement)
    ConstantPool cp;
    u2 this_class_idx;
    u2 super_class_idx;
    u2 out_field_idx;
    u2 println_int_idx;
    u2 println_float_idx;
    u2 println_str_idx;
    u2 println_bool_idx;
    u2 print_int_idx;
    u2 print_float_idx;
    u2 print_str_idx;
    u2 print_bool_idx;
    u2 println_void_idx;
    u2 print_space_idx;
    u2 string_class_idx;
    u2 string_equals_idx;
    u2 scanner_class_idx;
    u2 scanner_init_idx;
    u2 scanner_nextline_idx;
    u2 system_in_idx;
    u2 integer_class_idx;
    u2 integer_parseint_idx;
    u2 float_class_idx;
    u2 float_parsefloat_idx;
    u2 main_name_idx;
    u2 main_desc_idx;
    u2 code_name_idx;

    vector<u1> code;  // Current method's code (for generation)
    u2 max_stack = 10;
    u2 max_locals = 1;
    u1 scanner_local = 0; // Local variable index for Scanner
    
    int labelCounter = 0;
    
    // Runtime support
    u2 basicruntime_class_idx = 0;
    map<string, u2> functionMethodRefs; // Cache of function name -> method ref index
    
    // Multiple methods support
    vector<MethodInfo> methods;
    map<string, Type> currentLocalTypes;  // Current function's local types (for load to access)

    void buildConstantPool() {
        // Utf8
        u2 simple_class = cp.addUtf8("BasicProgram");
        u2 object_class = cp.addUtf8("java/lang/Object");
        main_name_idx = cp.addUtf8("main");
        main_desc_idx = cp.addUtf8("([Ljava/lang/String;)V");
        u2 nat_main = cp.addNameAndType(main_name_idx, main_desc_idx);
        this_class_idx = cp.addClass(simple_class);
        super_class_idx = cp.addClass(object_class);

        // System.out
        u2 system_class = cp.addUtf8("java/lang/System");
        u2 out_name = cp.addUtf8("out");
        u2 out_desc = cp.addUtf8("Ljava/io/PrintStream;");
        u2 nat_out = cp.addNameAndType(out_name, out_desc);
        u2 sys_cls_idx = cp.addClass(system_class);
        out_field_idx = cp.addFieldRef(sys_cls_idx, nat_out);

        // PrintStream
        u2 ps_class = cp.addUtf8("java/io/PrintStream");
        u2 ps_cls_idx = cp.addClass(ps_class);
        u2 println_name = cp.addUtf8("println");
        u2 print_name = cp.addUtf8("print");

        // println methods (with newline)
        // println (I)V
        u2 pi_desc = cp.addUtf8("(I)V");
        u2 nat_pi = cp.addNameAndType(println_name, pi_desc);
        println_int_idx = cp.addMethodRef(ps_cls_idx, nat_pi);

        // println (F)V
        u2 pf_desc = cp.addUtf8("(F)V");
        u2 nat_pf = cp.addNameAndType(println_name, pf_desc);
        println_float_idx = cp.addMethodRef(ps_cls_idx, nat_pf);

        // println (Ljava/lang/String;)V
        u2 ps_desc = cp.addUtf8("(Ljava/lang/String;)V");
        u2 nat_ps = cp.addNameAndType(println_name, ps_desc);
        println_str_idx = cp.addMethodRef(ps_cls_idx, nat_ps);

        // println (Z)V for boolean
        u2 pb_desc = cp.addUtf8("(Z)V");
        u2 nat_pb = cp.addNameAndType(println_name, pb_desc);
        println_bool_idx = cp.addMethodRef(ps_cls_idx, nat_pb);

        // println ()V for empty newline
        u2 pv_desc = cp.addUtf8("()V");
        u2 nat_pv = cp.addNameAndType(println_name, pv_desc);
        println_void_idx = cp.addMethodRef(ps_cls_idx, nat_pv);

        // print methods (without newline)
        // print (I)V
        u2 nat_pri = cp.addNameAndType(print_name, pi_desc);
        print_int_idx = cp.addMethodRef(ps_cls_idx, nat_pri);

        // print (F)V
        u2 nat_prf = cp.addNameAndType(print_name, pf_desc);
        print_float_idx = cp.addMethodRef(ps_cls_idx, nat_prf);

        // print (Ljava/lang/String;)V
        u2 nat_prs = cp.addNameAndType(print_name, ps_desc);
        print_str_idx = cp.addMethodRef(ps_cls_idx, nat_prs);

        // print (Z)V for boolean
        u2 nat_prb = cp.addNameAndType(print_name, pb_desc);
        print_bool_idx = cp.addMethodRef(ps_cls_idx, nat_prb);

        // For comma separator: print a space
        u2 space_utf = cp.addUtf8(" ");
        print_space_idx = cp.addString(space_utf);

        // String.equals
        u2 string_class_name = cp.addUtf8("java/lang/String");
        string_class_idx = cp.addClass(string_class_name);
        u2 equals_name = cp.addUtf8("equals");
        u2 equals_desc = cp.addUtf8("(Ljava/lang/Object;)Z");
        u2 nat_equals = cp.addNameAndType(equals_name, equals_desc);
        string_equals_idx = cp.addMethodRef(string_class_idx, nat_equals);

        // Scanner for input
        u2 scanner_class_name = cp.addUtf8("java/util/Scanner");
        scanner_class_idx = cp.addClass(scanner_class_name);
        u2 init_name = cp.addUtf8("<init>");
        u2 scanner_init_desc = cp.addUtf8("(Ljava/io/InputStream;)V");
        u2 nat_scanner_init = cp.addNameAndType(init_name, scanner_init_desc);
        scanner_init_idx = cp.addMethodRef(scanner_class_idx, nat_scanner_init);
        u2 nextline_name = cp.addUtf8("nextLine");
        u2 nextline_desc = cp.addUtf8("()Ljava/lang/String;");
        u2 nat_nextline = cp.addNameAndType(nextline_name, nextline_desc);
        scanner_nextline_idx = cp.addMethodRef(scanner_class_idx, nat_nextline);

        // System.in
        u2 in_name = cp.addUtf8("in");
        u2 in_desc = cp.addUtf8("Ljava/io/InputStream;");
        u2 nat_in = cp.addNameAndType(in_name, in_desc);
        system_in_idx = cp.addFieldRef(sys_cls_idx, nat_in);

        // Integer.parseInt
        u2 integer_class_name = cp.addUtf8("java/lang/Integer");
        integer_class_idx = cp.addClass(integer_class_name);
        u2 parseint_name = cp.addUtf8("parseInt");
        u2 parseint_desc = cp.addUtf8("(Ljava/lang/String;)I");
        u2 nat_parseint = cp.addNameAndType(parseint_name, parseint_desc);
        integer_parseint_idx = cp.addMethodRef(integer_class_idx, nat_parseint);

        // Float.parseFloat
        u2 float_class_name = cp.addUtf8("java/lang/Float");
        float_class_idx = cp.addClass(float_class_name);
        u2 parsefloat_name = cp.addUtf8("parseFloat");
        u2 parsefloat_desc = cp.addUtf8("(Ljava/lang/String;)F");
        u2 nat_parsefloat = cp.addNameAndType(parsefloat_name, parsefloat_desc);
        float_parsefloat_idx = cp.addMethodRef(float_class_idx, nat_parsefloat);

        // BasicRuntime for standard library functions
        u2 basicruntime_class_name = cp.addUtf8("basicrt/BasicRuntime");
        basicruntime_class_idx = cp.addClass(basicruntime_class_name);

        code_name_idx = cp.addUtf8("Code");
    }
    
    // Get or create method reference for a function
    u2 getFunctionMethodRef(const string& funcName) {
        // Check cache
        if (functionMethodRefs.count(funcName)) {
            return functionMethodRefs[funcName];
        }
        
        // Look up function signature
        auto it = builtinFunctions.find(funcName);
        if (it == builtinFunctions.end()) {
            throw runtime_error("Unknown function: " + funcName);
        }
        
        const FunctionSig& sig = it->second;
        
        // Add to constant pool
        u2 method_name = cp.addUtf8(sig.javaMethod);
        u2 method_desc = cp.addUtf8(sig.javaDescriptor);
        u2 nat = cp.addNameAndType(method_name, method_desc);
        u2 method_ref = cp.addMethodRef(basicruntime_class_idx, nat);
        
        // Cache it
        functionMethodRefs[funcName] = method_ref;
        return method_ref;
    }

    void emit(u1 byte) { code.push_back(byte); }
    void emit(u1 opc, u1 arg) { emit(opc); emit(arg); }
    void emit(u1 opc, u2 arg) {
        emit(opc);
        emit(static_cast<u1>(arg >> 8));
        emit(static_cast<u1>(arg & 0xFF));
    }
    void ldc(u2 idx) {
        if (idx <= 255) {
            emit(0x12, static_cast<u1>(idx));
        } else {
            emit(0x13, idx);
        }
    }

    void getstatic(u2 idx) { emit(0xB2, idx); }
    void invokevirtual(u2 idx) { emit(0xB6, idx); }
    void iconst(int i) {
        if (i == -1) emit(0x02);
        else if (i >= 0 && i <= 5) emit(0x03 + static_cast<u1>(i));
        else if (i >= -128 && i <= 127) emit(0x10, static_cast<u1>(i));
        else emit(0x11, static_cast<u2>(i));
    }
    void fconst(float f) {
        if (f == 0.0f) emit(0x0B);
        else if (f == 1.0f) emit(0x0C);
        else if (f == 2.0f) emit(0x0D);
        else {
            uint32_t bits = *reinterpret_cast<uint32_t*>(&f);
            u2 fidx = cp.addFloat(bits);
            ldc(fidx);
        }
    }
    void iload(u1 idx) {
        if (idx < 4) emit(0x1A + idx);
        else emit(0x15, idx);
    }
    void fload(u1 idx) {
        if (idx < 4) emit(0x22 + idx);
        else emit(0x17, idx);
    }
    void aload(u1 idx) {
        if (idx < 4) emit(0x2A + idx);
        else emit(0x19, idx);
    }
    void istore(u1 idx) {
        if (idx < 4) emit(0x3B + idx);
        else emit(0x36, idx);
    }
    void fstore(u1 idx) {
        if (idx < 4) emit(0x43 + idx);
        else emit(0x38, idx);
    }
    void astore(u1 idx) {
        if (idx < 4) emit(0x4B + idx);
        else emit(0x3A, idx);
    }
    void iadd() { emit(0x60); }
    void isub() { emit(0x64); }
    void imul() { emit(0x68); }
    void idiv() { emit(0x6C); }
    void fadd() { emit(0x62); }
    void fsub() { emit(0x66); }
    void fmul() { emit(0x6A); }
    void fdiv() { emit(0x6E); }
    void irem() { emit(0x70); }
    void frem() { emit(0x72); }
    void ineg() { emit(0x74); }
    void fneg() { emit(0x76); }
    void i2f() { emit(0x86); } // Added i2f instruction
    void _return() { emit(0xB1); }
    void ireturn() { emit(0xAC); }
    void freturn() { emit(0xAE); }
    void areturn() { emit(0xB0); }
    void new_(u2 idx) { emit(0xBB, idx); }
    void dup() { emit(0x59); }
    
    void ldc_string(const string& s) {
        u2 str_utf8_idx = cp.addUtf8(s);
        u2 str_idx = cp.addString(str_utf8_idx);
        if (str_idx <= 255) {
            emit(0x12, static_cast<u1>(str_idx)); // ldc
        } else {
            emit(0x13, str_idx); // ldc_w
        }
    }
    void invokespecial(u2 idx) { emit(0xB7, idx); }
    void invokestatic(u2 idx) { emit(0xB8, idx); }
    
    // Array instructions
    void newarray_int() { emit(0xBC); emit(10); }      // T_INT = 10
    void newarray_float() { emit(0xBC); emit(6); }     // T_FLOAT = 6
    void newarray_bool() { emit(0xBC); emit(4); }      // T_BOOLEAN = 4
    void anewarray(u2 idx) { emit(0xBD, idx); }        // For String arrays
    void iaload() { emit(0x2E); }
    void faload() { emit(0x30); }
    void aaload() { emit(0x32); }
    void baload() { emit(0x33); }
    void iastore() { emit(0x4F); }
    void fastore() { emit(0x51); }
    void aastore() { emit(0x53); }
    void bastore() { emit(0x54); }
    
    // Branching instructions
    void ifeq(Label& L) { emitBranch(0x99, L); }
    void ifne(Label& L) { emitBranch(0x9A, L); }
    void iflt(Label& L) { emitBranch(0x9B, L); }
    void ifge(Label& L) { emitBranch(0x9C, L); }
    void ifgt(Label& L) { emitBranch(0x9D, L); }
    void ifle(Label& L) { emitBranch(0x9E, L); }
    void if_icmpeq(Label& L) { emitBranch(0x9F, L); }
    void if_icmpne(Label& L) { emitBranch(0xA0, L); }
    void if_icmplt(Label& L) { emitBranch(0xA1, L); }
    void if_icmpge(Label& L) { emitBranch(0xA2, L); }
    void if_icmpgt(Label& L) { emitBranch(0xA3, L); }
    void if_icmple(Label& L) { emitBranch(0xA4, L); }
    void goto_(Label& L) { emitBranch(0xA7, L); }
    
    // Float comparison instructions
    void fcmpl() { emit(0x95); }
    void fcmpg() { emit(0x96); }
    
    // Label management
    int position() const { return static_cast<int>(code.size()); }
    
    void mark(Label& L) {
        L.pos = position();
        for (int site : L.patchSites) {
            patchJump(site, L.pos);
        }
        L.patchSites.clear();
    }
    
    void emitBranch(u1 opcode, Label& L) {
        emit(opcode);
        int site = position();
        emit(static_cast<u1>(0)); // Placeholder
        emit(static_cast<u1>(0)); // Placeholder
        if (L.pos >= 0) {
            patchJump(site, L.pos);
        } else {
            L.patchSites.push_back(site);
        }
    }
    
    void patchJump(int site, int target) {
        int16_t offset = static_cast<int16_t>(target - (site - 1));
        code[site] = static_cast<u1>((offset >> 8) & 0xFF);
        code[site + 1] = static_cast<u1>(offset & 0xFF);
    }
    
    void loadComparison(const CmpOp& co, map<string, u1>& varIdx) {
        // Determine the operand types (promote if needed)
        Type leftType = co.left->type;
        Type rightType = co.right->type;
        
        // Handle string comparisons
        if (leftType == Type::String && rightType == Type::String) {
            load(*co.left, varIdx);
            load(*co.right, varIdx);
            invokevirtual(string_equals_idx); // returns boolean (0 or 1)
            
            // Handle negation for != operator
            if (co.op == Op::Ne) {
                // XOR with 1 to flip the boolean
                iconst(1);
                emit(0x82); // ixor
            } else if (co.op != Op::Eq) {
                throw runtime_error("Only == and <> are supported for string comparisons");
            }
            return;
        }
        
        // Handle float comparisons with epsilon
        if (leftType == Type::Float || rightType == Type::Float) {
            load(*co.left, varIdx);
            if (leftType == Type::Int) i2f();
            load(*co.right, varIdx);
            if (rightType == Type::Int) i2f();
            
            // For float comparisons, we use fcmpg/fcmpl and then conditional branches
            // fcmpg: pushes 1 if left > right, 0 if equal, -1 if left < right (or either NaN)
            fcmpg();
            
            Label trueLabel, endLabel;
            switch (co.op) {
                case Op::Lt: iflt(trueLabel); break;
                case Op::Gt: ifgt(trueLabel); break;
                case Op::Le: ifle(trueLabel); break;
                case Op::Ge: ifge(trueLabel); break;
                case Op::Eq: ifeq(trueLabel); break;
                case Op::Ne: ifne(trueLabel); break;
                default: throw runtime_error("Unknown comparison operator");
            }
            iconst(0);
            goto_(endLabel);
            mark(trueLabel);
            iconst(1);
            mark(endLabel);
            return;
        }
        
        // Handle integer/boolean comparisons
        if ((leftType == Type::Int || leftType == Type::Bool) && 
            (rightType == Type::Int || rightType == Type::Bool)) {
            load(*co.left, varIdx);
            load(*co.right, varIdx);
            
            Label trueLabel, endLabel;
            switch (co.op) {
                case Op::Lt: if_icmplt(trueLabel); break;
                case Op::Gt: if_icmpgt(trueLabel); break;
                case Op::Le: if_icmple(trueLabel); break;
                case Op::Ge: if_icmpge(trueLabel); break;
                case Op::Eq: if_icmpeq(trueLabel); break;
                case Op::Ne: if_icmpne(trueLabel); break;
                default: throw runtime_error("Unknown comparison operator");
            }
            iconst(0);
            goto_(endLabel);
            mark(trueLabel);
            iconst(1);
            mark(endLabel);
            return;
        }
        
        throw runtime_error("Type mismatch in comparison");
    }

    void load(const Expr& e, map<string, u1>& varIdx) {
        if (e.kind == ExprKind::Num) {
            const NumLit& nl = get<NumLit>(e.data);
            if (e.type == Type::Int) {
                iconst(static_cast<int>(nl.value));
                if (e.type != Type::Float) return; // No conversion needed
                i2f(); // Convert int to float for Float-typed expression
            } else {
                fconst(static_cast<float>(nl.value));
            }
        } else if (e.kind == ExprKind::Str) {
            const StrLit& sl = get<StrLit>(e.data);
            u2 utfIdx = cp.addUtf8(sl.value);
            u2 strIdx = cp.addString(utfIdx);
            ldc(strIdx);
        } else if (e.kind == ExprKind::BoolLit) {
            const BoolLit& bl = get<BoolLit>(e.data);
            iconst(bl.value ? 1 : 0);
        } else if (e.kind == ExprKind::Var) {
            const VarRef& vr = get<VarRef>(e.data);
            u1 idx = varIdx.at(vr.name);
            
            if (vr.index) {
                // Array element access: load array, load index, load element
                aload(idx);  // Load array reference
                load(*vr.index, varIdx);  // Load index (should be Int)
                
                // Load element based on type
                if (e.type == Type::Int) iaload();
                else if (e.type == Type::Float) faload();
                else if (e.type == Type::Bool) baload();
                else if (e.type == Type::String) aaload();
            } else {
                // Scalar variable access or array reference
                // Check currentLocalTypes for function parameters
                Type actualType = e.type;
                auto localIt = currentLocalTypes.find(vr.name);
                if (localIt != currentLocalTypes.end()) {
                    actualType = localIt->second;
                }
                
                if (actualType == Type::Int || actualType == Type::Bool) {
                    iload(idx);
                    if (actualType != Type::Float) return; // No conversion needed
                    i2f(); // Convert int to float for Float-typed expression
                } else if (actualType == Type::Float) {
                    fload(idx);
                } else {
                    // String or array reference
                    aload(idx);
                }
            }
        } else if (e.kind == ExprKind::Cmp) {
            const CmpOp& co = get<CmpOp>(e.data);
            loadComparison(co, varIdx);
        } else if (e.kind == ExprKind::Unary) {
            const UnaryExpr& ue = get<UnaryExpr>(e.data);
            load(*ue.operand, varIdx);
            if (e.type == Type::Int) {
                ineg();
            } else if (e.type == Type::Float) {
                fneg();
            }
        } else if (e.kind == ExprKind::Call) {
            const CallExpr& ce = get<CallExpr>(e.data);
            
            // Check if it's a built-in function (uppercase names)
            auto builtinIt = builtinFunctions.find(ce.name);
            if (builtinIt != builtinFunctions.end()) {
                // Built-in function
                const FunctionSig& sig = builtinIt->second;
                
                // Load arguments
                for (size_t i = 0; i < ce.args.size(); ++i) {
                    load(*ce.args[i], varIdx);
                    
                    // Convert Int to Float if needed
                    Type expectedType = sig.paramTypes[i];
                    Type actualType = ce.args[i]->type;
                    if (expectedType == Type::Float && actualType == Type::Int) {
                        i2f();
                    }
                }
                
                // Call built-in function
                u2 methodRef = getFunctionMethodRef(ce.name);
                invokestatic(methodRef);
            } else {
                // User-defined function
                // Load arguments and convert Int to Float if return type is Float
                for (const auto& arg : ce.args) {
                    load(*arg, varIdx);
                    // If return type is Float and arg is Int, convert
                    if (e.type == Type::Float && arg->type == Type::Int) {
                        i2f();
                    }
                }
                
                // Build method descriptor (use return type for param types - simple inference)
                string descriptor = "(";
                for (size_t i = 0; i < ce.args.size(); ++i) {
                    if (e.type == Type::Int || e.type == Type::Bool) descriptor += "I";
                    else if (e.type == Type::Float) descriptor += "F";
                    else if (e.type == Type::String) descriptor += "Ljava/lang/String;";
                }
                descriptor += ")";
                if (e.type == Type::Int || e.type == Type::Bool) descriptor += "I";
                else if (e.type == Type::Float) descriptor += "F";
                else if (e.type == Type::String) descriptor += "Ljava/lang/String;";
                
                // Create method reference if not cached
                string funcKey = ce.name + descriptor;
                if (functionMethodRefs.find(funcKey) == functionMethodRefs.end()) {
                    u2 name_idx = cp.addUtf8(ce.name);
                    u2 desc_idx = cp.addUtf8(descriptor);
                    u2 nat_idx = cp.addNameAndType(name_idx, desc_idx);
                    functionMethodRefs[funcKey] = cp.addMethodRef(this_class_idx, nat_idx);
                }
                
                // Call the user-defined function
                invokestatic(functionMethodRefs[funcKey]);
            }
        } else if (e.kind == ExprKind::Bin) {
            const BinOp& bo = get<BinOp>(e.data);
            load(*bo.left, varIdx);
            // If binary op is Float but left operand is Int, convert
            if (e.type == Type::Float && bo.left->type == Type::Int) {
                i2f();
            }
            load(*bo.right, varIdx);
            // If binary op is Float but right operand is Int, convert
            if (e.type == Type::Float && bo.right->type == Type::Int) {
                i2f();
            }
            if (e.type == Type::Int) {
                switch (bo.op) {
                    case Op::Add: iadd(); break;
                    case Op::Sub: isub(); break;
                    case Op::Mul: imul(); break;
                    case Op::Div: idiv(); break;
                    case Op::Mod: irem(); break;
                }
            } else if (e.type == Type::Float) {
                switch (bo.op) {
                    case Op::Add: fadd(); break;
                    case Op::Sub: fsub(); break;
                    case Op::Mul: fmul(); break;
                    case Op::Div: fdiv(); break;
                    case Op::Mod: frem(); break;
                }
            }
        }
    }

    void genStmt(const Stmt& s, map<string, u1>& varIdx, u1& nextLocal, const map<string, Type>& knownTypes) {
        if (s.kind == StmtKind::Print) {
            const PrintStmt& ps = get<PrintStmt>(s.data);
            
            for (size_t i = 0; i < ps.exprs.size(); ++i) {
                const auto& expr = ps.exprs[i];
                bool isLast = (i == ps.exprs.size() - 1);
                
                // Load System.out
                getstatic(out_field_idx);
                // Load expression value
                load(*expr, varIdx);
                
                // Determine which print method to use
                if (isLast && ps.addNewline) {
                    // Last expression with newline: use println
                    if (expr->type == Type::Int) invokevirtual(println_int_idx);
                    else if (expr->type == Type::Float) invokevirtual(println_float_idx);
                    else if (expr->type == Type::Bool) invokevirtual(println_bool_idx);
                    else invokevirtual(println_str_idx);
                } else {
                    // Not last, or no newline: use print (no newline)
                    if (expr->type == Type::Int) invokevirtual(print_int_idx);
                    else if (expr->type == Type::Float) invokevirtual(print_float_idx);
                    else if (expr->type == Type::Bool) invokevirtual(print_bool_idx);
                    else invokevirtual(print_str_idx);
                }
                
                // Print separator if not last
                if (!isLast) {
                    PrintSep sep = ps.seps[i];
                    if (sep == PrintSep::Comma) {
                        // Print a space for comma separator
                        getstatic(out_field_idx);
                        ldc(print_space_idx);
                        invokevirtual(print_str_idx);
                    }
                    // Semicolon separator: print nothing (no space)
                }
            }
            
            // If no newline at end but expressions were printed, we're done
            // Otherwise if empty PRINT, add newline
            if (ps.exprs.empty()) {
                getstatic(out_field_idx);
                invokevirtual(println_void_idx);
            }
        } else if (s.kind == StmtKind::Let) {
            const LetStmt& ls = get<LetStmt>(s.data);
            
            if (ls.index) {
                // Array element assignment: LET arr(index) = value
                u1 idx = varIdx.at(ls.var);
                aload(idx);  // Load array reference
                load(*ls.index, varIdx);  // Load index
                load(*ls.expr, varIdx);  // Load value
                
                // Store based on type
                if (ls.expr->type == Type::Int) iastore();
                else if (ls.expr->type == Type::Float) fastore();
                else if (ls.expr->type == Type::Bool) bastore();
                else if (ls.expr->type == Type::String) aastore();
            } else {
                // Scalar assignment
                if (varIdx.find(ls.var) == varIdx.end()) {
                    varIdx[ls.var] = nextLocal++;
                }
                u1 idx = varIdx[ls.var];
                load(*ls.expr, varIdx);
                if (ls.expr->type == Type::Int || ls.expr->type == Type::Bool) istore(idx);
                else if (ls.expr->type == Type::Float) fstore(idx);
                else astore(idx);
                max_locals = max(max_locals, static_cast<u2>(nextLocal));
            }
        } else if (s.kind == StmtKind::Input) {
            const InputStmt& is = get<InputStmt>(s.data);
            u1 idx = varIdx.at(is.var);
            Type varType = knownTypes.at(is.var);
            
            // Load scanner
            aload(scanner_local);
            // Call nextLine()
            invokevirtual(scanner_nextline_idx);
            
            // Convert string to appropriate type
            if (varType == Type::Int) {
                // Integer.parseInt(string)
                invokestatic(integer_parseint_idx);
                istore(idx);
            } else if (varType == Type::Float) {
                // Float.parseFloat(string)
                invokestatic(float_parsefloat_idx);
                fstore(idx);
            } else if (varType == Type::Bool) {
                // Check if string equals "true" (case-insensitive)
                // For simplicity: use String.toLowerCase().equals("true")
                u2 tolower_name = cp.addUtf8("toLowerCase");
                u2 tolower_desc = cp.addUtf8("()Ljava/lang/String;");
                u2 nat_tolower = cp.addNameAndType(tolower_name, tolower_desc);
                u2 tolower_idx = cp.addMethodRef(string_class_idx, nat_tolower);
                
                invokevirtual(tolower_idx);
                u2 true_utf = cp.addUtf8("true");
                u2 true_str = cp.addString(true_utf);
                ldc(true_str);
                invokevirtual(string_equals_idx);
                // Result is 0 or 1 (boolean as int)
                istore(idx);
            } else {
                // String: just store directly
                astore(idx);
            }
        } else if (s.kind == StmtKind::Dim) {
            const DimStmt& ds = get<DimStmt>(s.data);
            
            // Allocate local variable for array
            varIdx[ds.var] = nextLocal++;
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
            u1 idx = varIdx[ds.var];
            
            // Load size and create array
            load(*ds.size, varIdx);
            
            Type arrType = knownTypes.at(ds.var);
            if (arrType == Type::IntArray) newarray_int();
            else if (arrType == Type::FloatArray) newarray_float();
            else if (arrType == Type::BoolArray) newarray_bool();
            else if (arrType == Type::StringArray) anewarray(string_class_idx);
            
            // Store array reference
            astore(idx);
            
            // Initialize all elements with initVal
            // For simplicity, we'll initialize in a loop at runtime
            // Save size to a temp variable
            load(*ds.size, varIdx);
            u1 sizeVar = nextLocal++;
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
            istore(sizeVar);
            
            // Initialize counter to 0
            iconst(0);
            u1 counterVar = nextLocal++;
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
            istore(counterVar);
            
            // Loop: while counter < size
            Label loopStart, loopEnd;
            mark(loopStart);
            
            // Check: counter < size
            iload(counterVar);
            iload(sizeVar);
            if_icmpge(loopEnd);
            
            // arr[counter] = initVal
            aload(idx);  // Load array
            iload(counterVar);  // Load index
            load(*ds.initVal, varIdx);  // Load init value
            
            // Store based on type
            if (ds.initVal->type == Type::Int) iastore();
            else if (ds.initVal->type == Type::Float) fastore();
            else if (ds.initVal->type == Type::Bool) bastore();
            else if (ds.initVal->type == Type::String) aastore();
            
            // counter++
            iload(counterVar);
            iconst(1);
            iadd();
            istore(counterVar);
            
            // goto loopStart
            goto_(loopStart);
            mark(loopEnd);
        } else if (s.kind == StmtKind::For) {
            const ForStmt& fs = get<ForStmt>(s.data);
            
            // Allocate loop variable if needed
            if (varIdx.find(fs.var) == varIdx.end()) {
                varIdx[fs.var] = nextLocal++;
                max_locals = max(max_locals, static_cast<u2>(nextLocal));
            }
            u1 varSlot = varIdx[fs.var];
            Type varType = knownTypes.at(fs.var);
            
            // Initialize: var = start
            load(*fs.start, varIdx);
            if (varType == Type::Int) istore(varSlot);
            else fstore(varSlot);
            
            // Determine step value (default 1)
            bool hasStep = (fs.step != nullptr);
            
            Label loopStart, loopEnd;
            mark(loopStart);
            
            // Check condition: var <= end (or >= for negative step)
            if (varType == Type::Int) {
                iload(varSlot);
                load(*fs.end, varIdx);
                // For simplicity, always use <= (assume positive step or user knows what they're doing)
                if_icmpgt(loopEnd);
            } else {
                // Float loop
                fload(varSlot);
                load(*fs.end, varIdx);
                if (fs.start->type == Type::Int) i2f();
                fcmpg();
                ifgt(loopEnd);
            }
            
            // Body
            for (const auto& stmt : fs.body) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            // Increment: var += step (or += 1)
            if (varType == Type::Int) {
                iload(varSlot);
                if (hasStep) {
                    load(*fs.step, varIdx);
                } else {
                    iconst(1);
                }
                iadd();
                istore(varSlot);
            } else {
                fload(varSlot);
                if (hasStep) {
                    load(*fs.step, varIdx);
                    if (fs.step->type == Type::Int) i2f();
                } else {
                    fconst(1.0f);
                }
                fadd();
                fstore(varSlot);
            }
            
            goto_(loopStart);
            mark(loopEnd);
        } else if (s.kind == StmtKind::While) {
            const WhileStmt& ws = get<WhileStmt>(s.data);
            
            Label loopStart, loopEnd;
            mark(loopStart);
            
            // Check condition
            load(*ws.cond, varIdx);
            ifeq(loopEnd);
            
            // Body
            for (const auto& stmt : ws.body) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            goto_(loopStart);
            mark(loopEnd);
        } else if (s.kind == StmtKind::DoWhile) {
            const DoWhileStmt& dws = get<DoWhileStmt>(s.data);
            
            Label loopStart;
            mark(loopStart);
            
            // Body (executes at least once)
            for (const auto& stmt : dws.body) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            // Check condition
            load(*dws.cond, varIdx);
            if (dws.isUntil) {
                // UNTIL: loop while condition is false
                ifeq(loopStart);
            } else {
                // WHILE: loop while condition is true
                ifne(loopStart);
            }
        } else if (s.kind == StmtKind::If) {
            const IfStmt& ifs = get<IfStmt>(s.data);
            
            // Evaluate main condition
            load(*ifs.cond, varIdx);
            Label nextLabel, endLabel;
            ifeq(nextLabel); // Jump to next clause if condition is false (0)
            
            // Generate THEN body
            for (const auto& stmt : ifs.thenBody) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            goto_(endLabel);
            
            // Generate ELSE IF clauses
            for (const auto& elseIf : ifs.elseIfs) {
                mark(nextLabel);
                nextLabel = Label(); // Create new label for next clause
                
                load(*elseIf.cond, varIdx);
                ifeq(nextLabel);
                
                for (const auto& stmt : elseIf.body) {
                    genStmt(*stmt, varIdx, nextLocal, knownTypes);
                }
                goto_(endLabel);
            }
            
            // Generate ELSE body
            mark(nextLabel);
            for (const auto& stmt : ifs.elseBody) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            mark(endLabel);
        } else if (s.kind == StmtKind::Return) {
            const ReturnStmt& rs = get<ReturnStmt>(s.data);
            if (rs.expr) {
                // Load return value
                load(*rs.expr, varIdx);
                // Generate appropriate return instruction
                if (rs.expr->type == Type::Int || rs.expr->type == Type::Bool) {
                    ireturn();
                } else if (rs.expr->type == Type::Float) {
                    freturn();
                } else if (rs.expr->type == Type::String) {
                    areturn();
                }
            } else {
                // Void return
                _return();
            }
        } else if (s.kind == StmtKind::CallStmt) {
            const CallStmtNode& cs = get<CallStmtNode>(s.data);
            // Load arguments
            for (const auto& arg : cs.args) {
                load(*arg, varIdx);
            }
            // Build method descriptor
            string descriptor = "(";
            for (const auto& arg : cs.args) {
                if (arg->type == Type::Int || arg->type == Type::Bool) descriptor += "I";
                else if (arg->type == Type::Float) descriptor += "F";
                else if (arg->type == Type::String) descriptor += "Ljava/lang/String;";
            }
            descriptor += ")V";  // Subs return void
            
            // Create method reference if not cached
            string funcKey = cs.name + descriptor;
            if (functionMethodRefs.find(funcKey) == functionMethodRefs.end()) {
                u2 name_idx = cp.addUtf8(cs.name);
                u2 desc_idx = cp.addUtf8(descriptor);
                u2 nat_idx = cp.addNameAndType(name_idx, desc_idx);
                functionMethodRefs[funcKey] = cp.addMethodRef(this_class_idx, nat_idx);
            }
            
            // Call the user-defined sub
            invokestatic(functionMethodRefs[funcKey]);
        }
    }

    // Generate user-defined function method
    void generateFunction(const FunctionDecl& fd) {
        // Reset code for new method
        code.clear();
        max_stack = 10;
        u1 nextLocal = 0; // Note: for static methods, slot 0 is first param (no 'this')
        
        // Map parameters to local slots
        map<string, u1> varIdx;
        for (const auto& param : fd.params) {
            varIdx[param.name] = nextLocal++;
        }
        max_locals = nextLocal;
        
        // Build parameter types map for genStmt (use return type for params)
        map<string, Type> localTypes;
        for (const auto& param : fd.params) {
            localTypes[param.name] = fd.returnType;  // Assume same type as return
        }
        
        // Set current local types for load() to access
        currentLocalTypes = localTypes;
        
        // Generate body
        for (const auto& stmt : fd.body) {
            genStmt(*stmt, varIdx, nextLocal, localTypes);
        }
        
        // If no explicit return, add default return of 0 or empty string
        if (fd.returnType == Type::Int) {
            iconst(0);
            ireturn();
        } else if (fd.returnType == Type::Float) {
            fconst(0.0f);
            freturn();
        } else if (fd.returnType == Type::String) {
            ldc_string("");
            areturn();
        } else if (fd.returnType == Type::Bool) {
            iconst(0);
            ireturn();
        }
        
        // Build method descriptor using return type as param type (simple inference)
        string descriptor = "(";
        for (const auto& param : fd.params) {
            // Use return type for parameters (simple inference for now)
            if (fd.returnType == Type::Int || fd.returnType == Type::Bool) descriptor += "I";
            else if (fd.returnType == Type::Float) descriptor += "F";
            else if (fd.returnType == Type::String) descriptor += "Ljava/lang/String;";
        }
        descriptor += ")";
        if (fd.returnType == Type::Int || fd.returnType == Type::Bool) descriptor += "I";
        else if (fd.returnType == Type::Float) descriptor += "F";
        else if (fd.returnType == Type::String) descriptor += "Ljava/lang/String;";
        
        // Add name and descriptor to constant pool
        u2 name_idx = cp.addUtf8(fd.name);
        u2 desc_idx = cp.addUtf8(descriptor);
        
        // Save method
        methods.push_back(MethodInfo{
            name_idx,
            desc_idx,
            0x0009, // public static
            code,
            max_stack,
            max_locals
        });
    }
    
    // Generate user-defined sub method
    void generateSub(const SubDecl& sd) {
        // Reset code for new method
        code.clear();
        max_stack = 10;
        u1 nextLocal = 0;
        
        // Map parameters to local slots
        map<string, u1> varIdx;
        for (const auto& param : sd.params) {
            varIdx[param.name] = nextLocal++;
        }
        max_locals = nextLocal;
        
        // Infer parameter types (default to String for SUBs)
        map<string, Type> paramTypes;
        for (const auto& param : sd.params) {
            paramTypes[param.name] = Type::String;  // Default to String
        }
        
        // Build parameter types map for genStmt
        map<string, Type> localTypes;
        for (const auto& param : sd.params) {
            localTypes[param.name] = paramTypes[param.name];
        }
        
        // Set current local types for load() to access
        currentLocalTypes = localTypes;
        
        // Generate body
        for (const auto& stmt : sd.body) {
            genStmt(*stmt, varIdx, nextLocal, localTypes);
        }
        
        // Add return
        _return();
        
        // Build method descriptor
        string descriptor = "(";
        for (const auto& param : sd.params) {
            Type ptype = paramTypes[param.name];
            if (ptype == Type::Int || ptype == Type::Bool) descriptor += "I";
            else if (ptype == Type::Float) descriptor += "F";
            else if (ptype == Type::String) descriptor += "Ljava/lang/String;";
        }
        descriptor += ")V";
        
        // Add name and descriptor to constant pool
        u2 name_idx = cp.addUtf8(sd.name);
        u2 desc_idx = cp.addUtf8(descriptor);
        
        // Save method
        methods.push_back(MethodInfo{
            name_idx,
            desc_idx,
            0x0009, // public static
            code,
            max_stack,
            max_locals
        });
    }

    void generate(const vector<DeclPtr>& declarations, const vector<StmtPtr>& program, const map<string, Type>& knownTypes) {
        // Generate methods for user-defined functions and subs
        for (const auto& decl : declarations) {
            if (decl->kind == DeclKind::Function) {
                const FunctionDecl& fd = get<FunctionDecl>(decl->data);
                generateFunction(fd);
            } else if (decl->kind == DeclKind::Sub) {
                const SubDecl& sd = get<SubDecl>(decl->data);
                generateSub(sd);
            }
        }
        
        // Generate main method
        code.clear();  // Reset code for main method
        currentLocalTypes.clear();  // Clear function local types
        map<string, u1> varIdx;
        u1 nextLocal = 1;
        max_locals = 1;
        max_stack = 10;
        
        // Initialize Scanner for INPUT (allocate before other variables)
        scanner_local = nextLocal++;
        max_locals = max(max_locals, static_cast<u2>(nextLocal));
        
        // new Scanner(System.in)
        new_(scanner_class_idx);
        dup();
        getstatic(system_in_idx);
        invokespecial(scanner_init_idx);
        astore(scanner_local);
        
        for (const auto& sp : program) {
            genStmt(*sp, varIdx, nextLocal, knownTypes);
        }
        _return();
        
        // Save main method
        methods.push_back(MethodInfo{
            main_name_idx,
            main_desc_idx,
            0x0009, // public static
            code,
            max_stack,
            max_locals
        });
    }

    void write(ostream& out) {
        writeU4(out, magic);
        writeU2(out, minor_version);
        writeU2(out, major_version);

        u2 cp_count = static_cast<u2>(cp.entries.size() + 1);
        writeU2(out, cp_count);
        for (const auto& entry : cp.entries) {
            for (u1 b : entry) {
                out.put(b);
            }
        }

        writeU2(out, 0x0021); // public, super
        writeU2(out, this_class_idx);
        writeU2(out, super_class_idx);
        writeU2(out, 0); // interfaces_count
        writeU2(out, 0); // fields_count

        // Write all methods
        writeU2(out, static_cast<u2>(methods.size())); // methods_count
        
        for (const auto& method : methods) {
            writeU2(out, method.access_flags);
            writeU2(out, method.name_idx);
            writeU2(out, method.descriptor_idx);
            writeU2(out, 1); // attributes_count (Code attribute)

            // Code attribute
            auto start_pos = out.tellp();
            writeU2(out, code_name_idx);
            auto len_pos = out.tellp();
            writeU4(out, 0); // placeholder for attribute_length

            writeU2(out, method.max_stack);
            writeU2(out, method.max_locals);
            u4 code_len = static_cast<u4>(method.code.size());
            writeU4(out, code_len);
            for (u1 b : method.code) out.put(b);
            writeU2(out, 0); // exception_table_length
            writeU2(out, 0); // code_attributes_count

            auto end_pos = out.tellp();
            u4 attr_len = static_cast<u4>(end_pos - (len_pos + static_cast<streamoff>(4)));
            out.seekp(len_pos);
            writeU4(out, attr_len);
            out.seekp(end_pos);
        }

        writeU2(out, 0); // attributes_count
    }

private:
    void writeU2(ostream& o, u2 v) {
        o.put(static_cast<char>(v >> 8));
        o.put(static_cast<char>(v & 0xFF));
    }
    void writeU4(ostream& o, u4 v) {
        writeU2(o, static_cast<u2>(v >> 16));
        writeU2(o, static_cast<u2>(v & 0xFFFF));
    }
};

// Compiler
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
