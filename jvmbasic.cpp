#include <bits/stdc++.h>

using namespace std;

using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Enums
enum class Type { Int, Float, String };
enum class Op { Add, Sub, Mul, Div, Mod };
enum class TokenType { END, NUMBER, STRING, ID, PLUS, MINUS, MUL, DIV, MOD, ASSIGN, SEMI, LPAREN, RPAREN, PRINT, LET };
enum class ExprKind { Num, Str, Var, Bin };
enum class StmtKind { Print, Let };

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

struct Expr {
    ExprKind kind;
    Type type;
    variant<NumLit, StrLit, VarRef, BinOp> data;

    Expr(ExprKind k, Type t, NumLit n) : kind(k), type(t), data(n) {}
    Expr(ExprKind k, Type t, StrLit s) : kind(k), type(t), data(s) {}
    Expr(ExprKind k, Type t, VarRef v) : kind(k), type(t), data(v) {}
    Expr(ExprKind k, Type t, BinOp b) : kind(k), type(t), data(std::move(b)) {}
};

// Stmt structures
struct PrintStmt { ExprPtr expr; };
struct LetStmt { string var; ExprPtr expr; };

struct Stmt {
    StmtKind kind;
    variant<PrintStmt, LetStmt> data;

    Stmt(StmtKind k, PrintStmt p) : kind(k), data(std::move(p)) {}
    Stmt(StmtKind k, LetStmt l) : kind(k), data(std::move(l)) {}
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
            if (s == "PRINT") return {TokenType::PRINT};
            if (s == "LET") return {TokenType::LET};
            if (s == "MOD") return {TokenType::MOD};
            return {TokenType::ID, s};
        } else if (!eof) {
            if (ch == '+') { read(); return {TokenType::PLUS}; }
            else if (ch == '-') { read(); return {TokenType::MINUS}; }
            else if (ch == '*') { read(); return {TokenType::MUL}; }
            else if (ch == '/') { read(); return {TokenType::DIV}; }
            else if (ch == '%') { read(); return {TokenType::MOD}; }
            else if (ch == '=') { read(); return {TokenType::ASSIGN}; }
            else if (ch == ';') { read(); return {TokenType::SEMI}; }
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

    ExprPtr parseExpr() { return parseAdd(); }

    StmtPtr parseStmt() {
        if (tok.type == TokenType::PRINT) {
            next();
            auto e = parseExpr();
            expect(TokenType::SEMI);
            return make_unique<Stmt>(StmtKind::Print, PrintStmt{move(e)});
        } else if (tok.type == TokenType::LET) {
            next();
            string var = expect(TokenType::ID).val;
            expect(TokenType::ASSIGN);
            auto e = parseExpr();
            Type ty = e->type;
            if (knownTypes.count(var) && knownTypes[var] != ty) error("Type mismatch reassign");
            knownTypes[var] = ty;
            expect(TokenType::SEMI);
            return make_unique<Stmt>(StmtKind::Let, LetStmt{var, move(e)});
        }
        error();
        return nullptr;
    }

public:
    vector<StmtPtr> program;

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

// ClassFile
class ClassFile {
public:
    u4 magic = 0xCAFEBABE;
    u2 minor_version = 0;
    u2 major_version = 65; // Java 21
    ConstantPool cp;
    u2 this_class_idx;
    u2 super_class_idx;
    u2 out_field_idx;
    u2 println_int_idx;
    u2 println_float_idx;
    u2 println_str_idx;
    u2 main_name_idx;
    u2 main_desc_idx;
    u2 code_name_idx;

    vector<u1> code;
    u2 max_stack = 10;
    u2 max_locals = 1;

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
        } else if (e.kind == ExprKind::Var) {
            const VarRef& vr = get<VarRef>(e.data);
            u1 idx = varIdx.at(vr.name);
            if (e.type == Type::Int) {
                iload(idx);
                if (e.type != Type::Float) return; // No conversion needed
                i2f(); // Convert int to float for Float-typed expression
            } else if (e.type == Type::Float) {
                fload(idx);
            } else {
                aload(idx);
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

    void genStmt(const Stmt& s, map<string, u1>& varIdx, u1& nextLocal) {
        if (s.kind == StmtKind::Print) {
            const PrintStmt& ps = get<PrintStmt>(s.data);
            getstatic(out_field_idx);
            load(*ps.expr, varIdx);
            if (ps.expr->type == Type::Int) invokevirtual(println_int_idx);
            else if (ps.expr->type == Type::Float) invokevirtual(println_float_idx);
            else invokevirtual(println_str_idx);
        } else if (s.kind == StmtKind::Let) {
            const LetStmt& ls = get<LetStmt>(s.data);
            if (varIdx.find(ls.var) == varIdx.end()) {
                varIdx[ls.var] = nextLocal++;
            }
            u1 idx = varIdx[ls.var];
            load(*ls.expr, varIdx);
            if (ls.expr->type == Type::Int) istore(idx);
            else if (ls.expr->type == Type::Float) fstore(idx);
            else astore(idx);
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
        }
    }

    void generate(const vector<StmtPtr>& program) {
        map<string, u1> varIdx;
        u1 nextLocal = 1;
        max_locals = 1;
        for (const auto& sp : program) {
            genStmt(*sp, varIdx, nextLocal);
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
        cf.generate(p.program);
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
