#pragma once

#include <memory>
#include <string>
#include <vector>
#include <variant>

using namespace std;

// Type system
enum class Type { 
    Int, Float, String, Bool, 
    IntArray, FloatArray, StringArray, BoolArray,
    UserDefined  // For user-defined types (structs)
};

// Operators
enum class Op { Add, Sub, Mul, Div, Mod, Lt, Gt, Le, Ge, Eq, Ne };

// Phase 8: Logical operators
enum class LogicalOp { And, Or, Xor, Not };

// Forward declarations
struct Expr;
struct Stmt;
struct Decl;
using ExprPtr = unique_ptr<Expr>;
using StmtPtr = unique_ptr<Stmt>;
using DeclPtr = unique_ptr<Decl>;

// Expression kinds
enum class ExprKind { Num, Str, Var, Bin, BoolLit, Cmp, Call, Unary, MemberAccess, 
                      // Phase 7: OOP expressions
                      NewExpr, MethodCall, Me,
                      // Phase 8: Logical expressions
                      Logical };

// Unary operators
enum class UnaryOp { Neg };

// Expression structures
struct NumLit { 
    double value; 
};

struct StrLit { 
    string value; 
};

struct VarRef { 
    string name;
    ExprPtr index;  // nullptr for scalar, non-null for array access
};

struct BinOp { 
    Op op; 
    ExprPtr left, right; 
};

struct BoolLit { 
    bool value; 
};

struct CmpOp { 
    Op op; 
    ExprPtr left, right; 
};

struct CallExpr { 
    string name; 
    vector<ExprPtr> args;
};

struct UnaryExpr {
    UnaryOp op;
    ExprPtr operand;
};

struct MemberAccessExpr {
    ExprPtr object;
    string member;
};

// Phase 7: OOP expression structures
struct NewExpr {
    string className;
    vector<ExprPtr> args;
};

struct MethodCallExpr {
    ExprPtr object;
    string methodName;
    vector<ExprPtr> args;
};

struct MeExpr {
    // No data - just a marker for ME/this reference
};

// Phase 8: Logical expression (AND, OR, XOR, NOT)
struct LogicalExpr {
    LogicalOp op;
    ExprPtr left;   // nullptr for NOT (unary operator)
    ExprPtr right;
};

struct Expr {
    ExprKind kind;
    Type type;
    string typeName;  // For UserDefined types, stores the type name
    variant<NumLit, StrLit, VarRef, BinOp, BoolLit, CmpOp, CallExpr, UnaryExpr, MemberAccessExpr,
            NewExpr, MethodCallExpr, MeExpr, LogicalExpr> data;

    Expr(ExprKind k, Type t, NumLit n) : kind(k), type(t), data(n) {}
    Expr(ExprKind k, Type t, StrLit s) : kind(k), type(t), data(s) {}
    Expr(ExprKind k, Type t, VarRef v) : kind(k), type(t), data(std::move(v)) {}
    Expr(ExprKind k, Type t, BinOp b) : kind(k), type(t), data(std::move(b)) {}
    Expr(ExprKind k, Type t, BoolLit bl) : kind(k), type(t), data(bl) {}
    Expr(ExprKind k, Type t, CmpOp c) : kind(k), type(t), data(std::move(c)) {}
    Expr(ExprKind k, Type t, CallExpr c) : kind(k), type(t), data(std::move(c)) {}
    Expr(ExprKind k, Type t, UnaryExpr u) : kind(k), type(t), data(std::move(u)) {}
    Expr(ExprKind k, Type t, MemberAccessExpr m) : kind(k), type(t), data(std::move(m)) {}
    // Phase 7: OOP constructors
    Expr(ExprKind k, Type t, NewExpr n) : kind(k), type(t), data(std::move(n)) {}
    Expr(ExprKind k, Type t, MethodCallExpr mc) : kind(k), type(t), data(std::move(mc)) {}
    Expr(ExprKind k, Type t, MeExpr me) : kind(k), type(t), data(me) {}
    // Phase 8: Logical expression constructor
    Expr(ExprKind k, Type t, LogicalExpr l) : kind(k), type(t), data(std::move(l)) {}
};

// Statement kinds
enum class StmtKind { 
    Print, Let, Input, Dim, If, For, While, DoWhile, Return, CallStmt,
    // Phase 7: OOP statements
    MethodCallStmt,
    // Phase 8: Advanced control flow
    ExitFor, ExitWhile, Continue
};

// Statement structures
enum class PrintSep { Comma, Semi };

struct PrintStmt { 
    vector<ExprPtr> exprs;
    vector<PrintSep> seps;
    bool addNewline;
};

struct LetStmt { 
    string var; 
    ExprPtr expr;
    ExprPtr index;  // nullptr for scalar, non-null for array assignment
};

struct InputStmt { 
    string var;
    ExprPtr index;  // nullptr for scalar, non-null for array input
};

struct DimStmt {
    string var;
    ExprPtr size;
    ExprPtr initVal;
    string typeName;  // For "DIM var AS TypeName"
};

struct ElseIfClause { 
    ExprPtr cond; 
    vector<StmtPtr> body; 
};

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
    ExprPtr step;
    vector<StmtPtr> body;
};

struct WhileStmt {
    ExprPtr cond;
    vector<StmtPtr> body;
};

struct DoWhileStmt {
    ExprPtr cond;
    vector<StmtPtr> body;
    bool isUntil;
};

struct ReturnStmt {
    ExprPtr expr;  // nullptr for void returns
};

struct CallStmtNode {
    string name;
    vector<ExprPtr> args;
};

// Phase 7: Method call statement (CALL obj.method(args))
struct MethodCallStmtNode {
    ExprPtr object;
    string methodName;
    vector<ExprPtr> args;
};

// Phase 8: Simple control flow statements (no data needed)
struct ExitForStmt {};
struct ExitWhileStmt {};
struct ContinueStmt {};

struct Stmt {
    StmtKind kind;
    variant<PrintStmt, LetStmt, InputStmt, DimStmt, IfStmt, ForStmt, 
            WhileStmt, DoWhileStmt, ReturnStmt, CallStmtNode, MethodCallStmtNode,
            ExitForStmt, ExitWhileStmt, ContinueStmt> data;

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
    Stmt(StmtKind k, MethodCallStmtNode mcs) : kind(k), data(std::move(mcs)) {}  // Phase 7
    // Phase 8: Control flow statement constructors
    Stmt(StmtKind k, ExitForStmt efs) : kind(k), data(efs) {}
    Stmt(StmtKind k, ExitWhileStmt ews) : kind(k), data(ews) {}
    Stmt(StmtKind k, ContinueStmt cs) : kind(k), data(cs) {}
};

// Declaration kinds
enum class DeclKind { Function, Sub, TypeDef, Class };

// Parameter for functions/subs
struct Param {
    string name;
    Type type;
    string typeName;  // For UserDefined types
};

// Field definition for TYPE and CLASS declarations
struct Field {
    string name;
    Type type;
    string typeName;  // For UserDefined field types
    bool isPublic = true;  // Phase 7: For CLASS fields (default PUBLIC)
};

// Function/Sub declarations
struct FunctionDecl {
    string name;
    vector<Param> params;
    Type returnType;
    vector<StmtPtr> body;
};

struct SubDecl {
    string name;
    vector<Param> params;
    vector<StmtPtr> body;
};

// TYPE declaration (user-defined type)
struct TypeDefDecl {
    string name;
    vector<Field> fields;
};

// Phase 7: Method declaration (for methods within classes)
struct MethodDecl {
    string name;
    bool isPublic;
    bool isConstructor;  // true if name == "New"
    vector<Param> params;
    Type returnType;
    vector<StmtPtr> body;
};

// Phase 7: CLASS declaration
struct ClassDecl {
    string name;
    vector<Field> fields;
    vector<MethodDecl> methods;
};

struct Decl {
    DeclKind kind;
    variant<FunctionDecl, SubDecl, TypeDefDecl, ClassDecl> data;

    Decl(DeclKind k, FunctionDecl f) : kind(k), data(std::move(f)) {}
    Decl(DeclKind k, SubDecl s) : kind(k), data(std::move(s)) {}
    Decl(DeclKind k, TypeDefDecl t) : kind(k), data(std::move(t)) {}
    Decl(DeclKind k, ClassDecl c) : kind(k), data(std::move(c)) {}  // Phase 7
};

// Program structure
struct Program {
    vector<DeclPtr> declarations;
    vector<StmtPtr> statements;
};

// Utility functions
string typeToString(Type t);
string opToString(Op op);

