#include <bits/stdc++.h>

using namespace std;

using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Enums
enum class Type { Int, Float, String, Bool };
enum class Op { Add, Sub, Mul, Div, Mod, Lt, Gt, Le, Ge, Eq, Ne };
enum class TokenType { END, NUMBER, STRING, ID, PLUS, MINUS, MUL, DIV, MOD, ASSIGN, SEMI, COMMA, LPAREN, RPAREN, 
                       PRINT, LET, INPUT, LT, GT, LE, GE, EQ, NE, 
                       TRUE, FALSE, IF, THEN, ELSE, ENDIF, ELSEIF };
enum class ExprKind { Num, Str, Var, Bin, BoolLit, Cmp };
enum class StmtKind { Print, Let, Input, If };

// Forward declarations
struct Expr;
struct Stmt;
using ExprPtr = unique_ptr<Expr>;
using StmtPtr = unique_ptr<Stmt>;

// Expr structures
struct NumLit { double value; };
struct StrLit { string value; };
struct VarRef { string name; };
struct BinOp { Op op; ExprPtr left, right; };
struct BoolLit { bool value; };
struct CmpOp { Op op; ExprPtr left, right; };

struct Expr {
    ExprKind kind;
    Type type;
    variant<NumLit, StrLit, VarRef, BinOp, BoolLit, CmpOp> data;

    Expr(ExprKind k, Type t, NumLit n) : kind(k), type(t), data(n) {}
    Expr(ExprKind k, Type t, StrLit s) : kind(k), type(t), data(s) {}
    Expr(ExprKind k, Type t, VarRef v) : kind(k), type(t), data(v) {}
    Expr(ExprKind k, Type t, BinOp b) : kind(k), type(t), data(std::move(b)) {}
    Expr(ExprKind k, Type t, BoolLit bl) : kind(k), type(t), data(bl) {}
    Expr(ExprKind k, Type t, CmpOp c) : kind(k), type(t), data(std::move(c)) {}
};

// Stmt structures
enum class PrintSep { Comma, Semi };
struct PrintStmt { 
    vector<ExprPtr> exprs;
    vector<PrintSep> seps; // separators between expressions (size = exprs.size() - 1)
    bool addNewline; // false if statement ends with , or ;
};
struct LetStmt { string var; ExprPtr expr; };
struct InputStmt { string var; };
struct ElseIfClause { ExprPtr cond; vector<StmtPtr> body; };
struct IfStmt { 
    ExprPtr cond; 
    vector<StmtPtr> thenBody;
    vector<ElseIfClause> elseIfs;
    vector<StmtPtr> elseBody;
};

struct Stmt {
    StmtKind kind;
    variant<PrintStmt, LetStmt, InputStmt, IfStmt> data;

    Stmt(StmtKind k, PrintStmt p) : kind(k), data(std::move(p)) {}
    Stmt(StmtKind k, LetStmt l) : kind(k), data(std::move(l)) {}
    Stmt(StmtKind k, InputStmt i) : kind(k), data(std::move(i)) {}
    Stmt(StmtKind k, IfStmt ifs) : kind(k), data(std::move(ifs)) {}
};

// Token
struct Token {
    TokenType type;
    string val;
    double num = 0.0;
};

// Lexer
class Lexer {
private:
    istream& in;
    char ch = 0;
    bool eof = false;

    void read() {
        if (!in.get(ch)) {
            eof = true;
            ch = 0;
        }
    }

    void skipWhite() {
        while (!eof && isspace(ch)) {
            read();
        }
    }

public:
    Lexer(istream& i) : in(i) { read(); skipWhite(); }

    Token nextToken() {
        skipWhite();
        if (eof) return {TokenType::END};

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
            Token t{TokenType::NUMBER, s, s.empty() ? 0.0 : stod(s)};
            return t;
        } else if (ch == '"') {
            read();
            string s;
            while (!eof && ch != '"') {
                s += ch;
                read();
            }
            if (!eof && ch == '"') read();
            else if (eof) error("Unterminated string");
            return {TokenType::STRING, s};
        } else if (!eof && isalpha(ch)) {
            string s;
            while (!eof && isalnum(ch)) {
                s += ch;
                read();
            }
            // Convert to uppercase for keyword matching
            string upper = s;
            for (auto& c : upper) c = toupper(c);
            
            if (upper == "PRINT") return {TokenType::PRINT};
            if (upper == "LET") return {TokenType::LET};
            if (upper == "INPUT") return {TokenType::INPUT};
            if (upper == "MOD") return {TokenType::MOD};
            if (upper == "IF") return {TokenType::IF};
            if (upper == "THEN") return {TokenType::THEN};
            if (upper == "ELSE") return {TokenType::ELSE};
            if (upper == "ELSEIF") return {TokenType::ELSEIF};
            if (upper == "ENDIF") return {TokenType::ENDIF};
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
                    if (nextUpper == "IF") return {TokenType::ENDIF};
                    // Not "END IF", so this is an error or undefined ID
                    error("Expected IF after END");
                }
                error("Expected IF after END");
            }
            // Boolean literals (case-insensitive, normalized to lowercase)
            if (upper == "TRUE") return {TokenType::TRUE, "true"};
            if (upper == "FALSE") return {TokenType::FALSE, "false"};
            
            return {TokenType::ID, s};
        } else if (!eof) {
            if (ch == '+') { read(); return {TokenType::PLUS}; }
            else if (ch == '-') { read(); return {TokenType::MINUS}; }
            else if (ch == '*') { read(); return {TokenType::MUL}; }
            else if (ch == '/') { read(); return {TokenType::DIV}; }
            else if (ch == '%') { read(); return {TokenType::MOD}; }
            else if (ch == '=') { 
                read(); 
                if (!eof && ch == '=') { read(); return {TokenType::EQ}; }
                return {TokenType::ASSIGN}; 
            }
            else if (ch == '<') {
                read();
                if (!eof && ch == '=') { read(); return {TokenType::LE}; }
                if (!eof && ch == '>') { read(); return {TokenType::NE}; }
                return {TokenType::LT};
            }
            else if (ch == '>') {
                read();
                if (!eof && ch == '=') { read(); return {TokenType::GE}; }
                return {TokenType::GT};
            }
            else if (ch == ';') { read(); return {TokenType::SEMI}; }
            else if (ch == ',') { read(); return {TokenType::COMMA}; }
            else if (ch == '(') { read(); return {TokenType::LPAREN}; }
            else if (ch == ')') { read(); return {TokenType::RPAREN}; }
            else {
                char c = ch;
                read();
                error("Invalid character: " + string(1, c));
            }
        }
        return {TokenType::END};
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

    void next() { tok = lex.nextToken(); }
    Token expect(TokenType tt) {
        if (tok.type == tt) {
            Token res = tok;
            next();
            return res;
        }
        throw runtime_error("Unexpected token");
    }
    void error(const string& msg = "Parse error") { throw runtime_error(msg); }

    ExprPtr parsePrimary() {
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
            next();
            auto it = knownTypes.find(name);
            if (it == knownTypes.end()) error("Undefined variable: " + name);
            return make_unique<Expr>(ExprKind::Var, it->second, VarRef{name});
        } else if (tok.type == TokenType::LPAREN) {
            next();
            auto e = parseExpr();
            expect(TokenType::RPAREN);
            return e;
        }
        error();
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
                    tok.type == TokenType::LET || tok.type == TokenType::INPUT || tok.type == TokenType::IF) {
                    addNewline = false;
                    break;
                }
                
                exprs.push_back(parseExpr());
            }
            
            return make_unique<Stmt>(StmtKind::Print, PrintStmt{move(exprs), move(seps), addNewline});
        } else if (tok.type == TokenType::LET) {
            next();
            string var = expect(TokenType::ID).val;
            expect(TokenType::ASSIGN);
            auto e = parseExpr();
            Type ty = e->type;
            if (knownTypes.count(var) && knownTypes[var] != ty) error("Type mismatch reassign");
            knownTypes[var] = ty;
            return make_unique<Stmt>(StmtKind::Let, LetStmt{var, move(e)});
        } else if (tok.type == TokenType::INPUT) {
            next();
            string var = expect(TokenType::ID).val;
            // Variable must already be defined to know its type
            if (knownTypes.find(var) == knownTypes.end()) {
                error("INPUT variable must be defined first with LET");
            }
            return make_unique<Stmt>(StmtKind::Input, InputStmt{var});
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
        }
        error();
        return nullptr;
    }

public:
    vector<StmtPtr> program;
    
    const map<string, Type>& getKnownTypes() const { return knownTypes; }

    Parser(istream& i) : lex(i) { next(); }
    void parse() {
        while (tok.type != TokenType::END) {
            program.push_back(parseStmt());
        }
    }
};

// Constant Pool Entry
using CpEntry = vector<u1>;

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

    vector<u1> code;
    u2 max_stack = 10;
    u2 max_locals = 1;
    u1 scanner_local = 0; // Local variable index for Scanner
    
    int labelCounter = 0;

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

        code_name_idx = cp.addUtf8("Code");
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
    void i2f() { emit(0x86); } // Added i2f instruction
    void _return() { emit(0xB1); }
    void new_(u2 idx) { emit(0xBB, idx); }
    void dup() { emit(0x59); }
    void invokespecial(u2 idx) { emit(0xB7, idx); }
    void invokestatic(u2 idx) { emit(0xB8, idx); }
    
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
            if (e.type == Type::Int || e.type == Type::Bool) {
                iload(idx);
                if (e.type != Type::Float) return; // No conversion needed
                i2f(); // Convert int to float for Float-typed expression
            } else if (e.type == Type::Float) {
                fload(idx);
            } else {
                aload(idx);
            }
        } else if (e.kind == ExprKind::Cmp) {
            const CmpOp& co = get<CmpOp>(e.data);
            loadComparison(co, varIdx);
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
            if (varIdx.find(ls.var) == varIdx.end()) {
                varIdx[ls.var] = nextLocal++;
            }
            u1 idx = varIdx[ls.var];
            load(*ls.expr, varIdx);
            if (ls.expr->type == Type::Int || ls.expr->type == Type::Bool) istore(idx);
            else if (ls.expr->type == Type::Float) fstore(idx);
            else astore(idx);
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
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
        }
    }

    void generate(const vector<StmtPtr>& program, const map<string, Type>& knownTypes) {
        map<string, u1> varIdx;
        u1 nextLocal = 1;
        max_locals = 1;
        
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

        writeU2(out, 1); // methods_count
        writeU2(out, 0x0009); // public static
        writeU2(out, main_name_idx);
        writeU2(out, main_desc_idx);
        writeU2(out, 1); // attributes_count

        // Code attribute
        auto start_pos = out.tellp();
        writeU2(out, code_name_idx);
        auto len_pos = out.tellp();
        writeU4(out, 0); // placeholder for attribute_length

        writeU2(out, max_stack);
        writeU2(out, max_locals);
        u4 code_len = static_cast<u4>(code.size());
        writeU4(out, code_len);
        for (u1 b : code) out.put(b);
        writeU2(out, 0); // exception_table_length
        writeU2(out, 0); // code_attributes_count

        auto end_pos = out.tellp();
        u4 attr_len = static_cast<u4>(end_pos - (len_pos + static_cast<streamoff>(4)));
        out.seekp(len_pos);
        writeU4(out, attr_len);
        out.seekp(end_pos);

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
        cf.generate(p.program, p.getKnownTypes());
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
