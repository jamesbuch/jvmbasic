#include "semantic.h"
#include "builtin_functions.h"
#include <iostream>

// SymbolTable implementation
void SymbolTable::define(const string& name, Type type) {
    symbols[name] = type;
}

bool SymbolTable::isDefined(const string& name) const {
    if (symbols.count(name)) return true;
    if (parent) return parent->isDefined(name);
    return false;
}

Type SymbolTable::getType(const string& name) const {
    auto it = symbols.find(name);
    if (it != symbols.end()) return it->second;
    if (parent) return parent->getType(name);
    throw runtime_error("Undefined symbol: " + name);
}

// SemanticAnalyzer implementation
void SemanticAnalyzer::error(const string& msg) {
    errors.push_back(msg);
}

bool SemanticAnalyzer::isNumericType(Type t) {
    return t == Type::Int || t == Type::Float;
}

Type SemanticAnalyzer::promoteTypes(Type a, Type b) {
    if (a == b) return a;
    if (isNumericType(a) && isNumericType(b)) return Type::Float;
    return a;  // Default to first type
}

Type SemanticAnalyzer::getArrayElementType(Type arrayType) {
    switch(arrayType) {
        case Type::IntArray: return Type::Int;
        case Type::FloatArray: return Type::Float;
        case Type::StringArray: return Type::String;
        case Type::BoolArray: return Type::Bool;
        default: 
            error("Not an array type");
            return Type::Int;
    }
}

Type SemanticAnalyzer::makeArrayType(Type elemType) {
    switch(elemType) {
        case Type::Int: return Type::IntArray;
        case Type::Float: return Type::FloatArray;
        case Type::String: return Type::StringArray;
        case Type::Bool: return Type::BoolArray;
        default: return Type::IntArray;
    }
}

Type SemanticAnalyzer::inferExprType(const Expr& expr, const SymbolTable& symbols) {
    switch(expr.kind) {
        case ExprKind::Num: {
            // Return the type that the parser set (based on decimal point presence)
            // Don't try to infer from value (15.0 would incorrectly become Int)
            return expr.type;
        }
        
        case ExprKind::Str:
            return Type::String;
            
        case ExprKind::BoolLit:
            return Type::Bool;
            
        case ExprKind::Var: {
            const VarRef& vr = get<VarRef>(expr.data);
            if (!symbols.isDefined(vr.name)) {
                error("Undefined variable: " + vr.name);
                return Type::Int;
            }
            Type varType = symbols.getType(vr.name);
            
            if (vr.index) {
                // Array access - return element type
                return getArrayElementType(varType);
            }
            return varType;
        }
        
        case ExprKind::Bin: {
            const BinOp& bo = get<BinOp>(expr.data);
            Type leftType = inferExprType(*bo.left, symbols);
            Type rightType = inferExprType(*bo.right, symbols);
            return promoteTypes(leftType, rightType);
        }
        
        case ExprKind::Cmp:
            return Type::Bool;
        
        case ExprKind::Unary: {
            const UnaryExpr& ue = get<UnaryExpr>(expr.data);
            return inferExprType(*ue.operand, symbols);
        }
            
        case ExprKind::Call: {
            const CallExpr& ce = get<CallExpr>(expr.data);
            
            // Check built-in functions
            string nameUpper = ce.name;
            for (auto& c : nameUpper) c = toupper(c);
            auto builtinIt = builtinFunctions.find(nameUpper);
            if (builtinIt != builtinFunctions.end()) {
                return builtinIt->second.returnType;
            }
            
            // Check user functions
            auto userIt = userFunctions.find(ce.name);
            if (userIt != userFunctions.end()) {
                return userIt->second.returnType;
            }
            
            // Phase 6/7: Check if it's an array variable (parser creates Call for array access)
            if (symbols.isDefined(ce.name)) {
                Type varType = symbols.getType(ce.name);
                if (varType == Type::IntArray || varType == Type::FloatArray ||
                    varType == Type::StringArray || varType == Type::BoolArray) {
                    // It's an array access, return element type
                    return getArrayElementType(varType);
                }
                
                // If it's a defined variable (e.g., function parameter) being used with (),
                // assume it's array access and return Float (common element type)
                // This handles array parameters that aren't properly typed
                return Type::Float;
            }
            
            error("Unknown function: " + ce.name);
            return Type::Float;
        }
        
        case ExprKind::MemberAccess: {
            // Look up field type from struct/class definition
            const MemberAccessExpr& mae = get<MemberAccessExpr>(expr.data);
            // For now, try to infer from the object type if available
            // This is a simplified implementation
            return Type::Float;  // Default - needs proper type lookup
        }
        
        // Phase 7: OOP expressions - minimal support to avoid errors
        case ExprKind::NewExpr:
        case ExprKind::MethodCall:
        case ExprKind::Me:
            return Type::Float;  // Default return type for now
        
        // Phase 8: Logical expressions
        case ExprKind::Logical:
            return Type::Bool;  // Logical operations always return Bool
    }
    
    return Type::Int;
}

void SemanticAnalyzer::analyzeExpr(Expr& expr, const SymbolTable& symbols) {
    // Infer and set the expression's type
    // For numeric literals, preserve the parser's type (it knows if there was a decimal point)
    if (expr.kind != ExprKind::Num) {
        expr.type = inferExprType(expr, symbols);
    }
    // For Num, keep the type the parser set based on decimal point presence
    
    // Recursively analyze sub-expressions
    switch(expr.kind) {
        case ExprKind::Var: {
            VarRef& vr = get<VarRef>(expr.data);
            if (vr.index) {
                analyzeExpr(*vr.index, symbols);
                if (vr.index->type != Type::Int) {
                    error("Array index must be Int, not " + typeToString(vr.index->type));
                }
            }
            break;
        }
        
        case ExprKind::Bin: {
            BinOp& bo = get<BinOp>(expr.data);
            analyzeExpr(*bo.left, symbols);
            analyzeExpr(*bo.right, symbols);
            
            // Check types are compatible
            if (!isNumericType(bo.left->type) || !isNumericType(bo.right->type)) {
                if (bo.left->type != bo.right->type) {
                    error("Type mismatch in binary operation: " + 
                          typeToString(bo.left->type) + " vs " + typeToString(bo.right->type));
                }
            }
            break;
        }
        
        case ExprKind::Cmp: {
            CmpOp& co = get<CmpOp>(expr.data);
            analyzeExpr(*co.left, symbols);
            analyzeExpr(*co.right, symbols);
            break;
        }
        
        case ExprKind::Unary: {
            UnaryExpr& ue = get<UnaryExpr>(expr.data);
            analyzeExpr(*ue.operand, symbols);
            if (!isNumericType(ue.operand->type)) {
                error("Unary minus requires numeric type");
            }
            break;
        }
        
        case ExprKind::Call: {
            CallExpr& ce = get<CallExpr>(expr.data);
            
            // Analyze arguments
            for (auto& arg : ce.args) {
                analyzeExpr(*arg, symbols);
            }
            
            // Record call site for type inference
            vector<Type> argTypes;
            for (const auto& arg : ce.args) {
                argTypes.push_back(arg->type);
            }
            callSites.push_back(CallSite{ce.name, argTypes, 0});
            break;
        }
        
        // Phase 7: OOP expressions
        case ExprKind::NewExpr: {
            NewExpr& ne = get<NewExpr>(expr.data);
            for (auto& arg : ne.args) {
                analyzeExpr(*arg, symbols);
            }
            break;
        }
        
        case ExprKind::MethodCall: {
            MethodCallExpr& mce = get<MethodCallExpr>(expr.data);
            analyzeExpr(*mce.object, symbols);
            for (auto& arg : mce.args) {
                analyzeExpr(*arg, symbols);
            }
            break;
        }
        
        case ExprKind::Me:
        case ExprKind::MemberAccess:
            // Nothing to analyze for these
            break;
        
        // Phase 8: Logical expressions
        case ExprKind::Logical: {
            LogicalExpr& le = get<LogicalExpr>(expr.data);
            if (le.left) {
                analyzeExpr(*le.left, symbols);
            }
            if (le.right) {
                analyzeExpr(*le.right, symbols);
            }
            break;
        }
        
        default:
            break;
    }
}

void SemanticAnalyzer::analyzeStmt(Stmt& stmt, SymbolTable& symbols) {
    switch(stmt.kind) {
        case StmtKind::Print: {
            PrintStmt& ps = get<PrintStmt>(stmt.data);
            for (auto& expr : ps.exprs) {
                analyzeExpr(*expr, symbols);
            }
            break;
        }
        
        case StmtKind::Let: {
            LetStmt& ls = get<LetStmt>(stmt.data);
            analyzeExpr(*ls.expr, symbols);
            
            if (ls.index) {
                // Array element assignment
                analyzeExpr(*ls.index, symbols);
                if (!symbols.isDefined(ls.var)) {
                    error("Undefined array: " + ls.var);
                } else {
                    Type arrType = symbols.getType(ls.var);
                    Type elemType = getArrayElementType(arrType);
                    if (ls.expr->type != elemType) {
                        // Allow Int->Float promotion
                        if (!(elemType == Type::Float && ls.expr->type == Type::Int)) {
                            error("Type mismatch in array assignment");
                        }
                    }
                }
            } else {
                // Scalar assignment
                if (symbols.isDefined(ls.var)) {
                    Type existingType = symbols.getType(ls.var);
                    if (existingType != ls.expr->type) {
                        error("Type mismatch: cannot reassign " + ls.var + 
                              " from " + typeToString(existingType) + 
                              " to " + typeToString(ls.expr->type));
                    }
                } else {
                    symbols.define(ls.var, ls.expr->type);
                }
            }
            break;
        }
        
        case StmtKind::Input: {
            InputStmt& is = get<InputStmt>(stmt.data);
            if (!symbols.isDefined(is.var)) {
                error("INPUT variable must be defined first: " + is.var);
            }
            if (is.index) {
                analyzeExpr(*is.index, symbols);
            }
            break;
        }
        
        case StmtKind::Dim: {
            DimStmt& ds = get<DimStmt>(stmt.data);
            
            if (!ds.typeName.empty()) {
                // Phase 6/7: DIM var AS TypeName or DIM var AS NEW
                if (symbols.isDefined(ds.var)) {
                    error("Variable already defined: " + ds.var);
                }
                
                if (ds.initVal && ds.initVal->kind == ExprKind::NewExpr) {
                    // Phase 7: DIM var AS NEW ClassName(args)
                    analyzeExpr(*ds.initVal, symbols);
                    symbols.define(ds.var, Type::UserDefined);
                } else {
                    // Phase 6: DIM var AS TypeName (struct)
                    symbols.define(ds.var, Type::UserDefined);
                }
            } else if (ds.initVal && ds.initVal->kind == ExprKind::NewExpr) {
                // Phase 7: DIM var AS NEW ClassName() - size is nullptr!
                if (symbols.isDefined(ds.var)) {
                    error("Variable already defined: " + ds.var);
                }
                analyzeExpr(*ds.initVal, symbols);
                symbols.define(ds.var, Type::UserDefined);
            } else if (ds.size) {
                // Regular array: DIM arr(size) = initVal
                analyzeExpr(*ds.size, symbols);
                if (ds.initVal) {
                    analyzeExpr(*ds.initVal, symbols);
                }
                
                if (ds.size->type != Type::Int) {
                    error("Array size must be Int");
                }
                
                if (ds.initVal) {
                    Type arrType = makeArrayType(ds.initVal->type);
                    if (symbols.isDefined(ds.var)) {
                        error("Variable already defined: " + ds.var);
                    }
                    symbols.define(ds.var, arrType);
                }
            } else {
                error("Invalid DIM statement");
            }
            break;
        }
        
        case StmtKind::If: {
            IfStmt& ifs = get<IfStmt>(stmt.data);
            analyzeExpr(*ifs.cond, symbols);
            
            if (ifs.cond->type != Type::Bool && ifs.cond->type != Type::Int) {
                error("IF condition must be Bool or Int");
            }
            
            for (auto& s : ifs.thenBody) {
                analyzeStmt(*s, symbols);
            }
            
            for (auto& elseIf : ifs.elseIfs) {
                analyzeExpr(*elseIf.cond, symbols);
                for (auto& s : elseIf.body) {
                    analyzeStmt(*s, symbols);
                }
            }
            
            for (auto& s : ifs.elseBody) {
                analyzeStmt(*s, symbols);
            }
            break;
        }
        
        case StmtKind::For: {
            ForStmt& fs = get<ForStmt>(stmt.data);
            analyzeExpr(*fs.start, symbols);
            analyzeExpr(*fs.end, symbols);
            if (fs.step) {
                analyzeExpr(*fs.step, symbols);
            }
            
            // Define loop variable
            symbols.define(fs.var, fs.start->type);
            
            for (auto& s : fs.body) {
                analyzeStmt(*s, symbols);
            }
            break;
        }
        
        case StmtKind::While: {
            WhileStmt& ws = get<WhileStmt>(stmt.data);
            analyzeExpr(*ws.cond, symbols);
            for (auto& s : ws.body) {
                analyzeStmt(*s, symbols);
            }
            break;
        }
        
        case StmtKind::DoWhile: {
            DoWhileStmt& dws = get<DoWhileStmt>(stmt.data);
            for (auto& s : dws.body) {
                analyzeStmt(*s, symbols);
            }
            analyzeExpr(*dws.cond, symbols);
            break;
        }
        
        case StmtKind::Return: {
            ReturnStmt& rs = get<ReturnStmt>(stmt.data);
            if (rs.expr) {
                analyzeExpr(*rs.expr, symbols);
            }
            break;
        }
        
        case StmtKind::CallStmt: {
            CallStmtNode& cs = get<CallStmtNode>(stmt.data);
            
            // Analyze arguments
            vector<Type> argTypes;
            for (auto& arg : cs.args) {
                analyzeExpr(*arg, symbols);
                argTypes.push_back(arg->type);
            }
            
            // Record call site
            callSites.push_back(CallSite{cs.name, argTypes, 0});
            break;
        }
        
        // Phase 7: Method call statement
        case StmtKind::MethodCallStmt: {
            MethodCallStmtNode& mcs = get<MethodCallStmtNode>(stmt.data);
            analyzeExpr(*mcs.object, symbols);
            for (auto& arg : mcs.args) {
                analyzeExpr(*arg, symbols);
            }
            break;
        }
        
        // Phase 8: Control flow statements
        case StmtKind::ExitFor:
        case StmtKind::ExitWhile:
        case StmtKind::Continue:
            // No analysis needed for these
            break;
    }
}

void SemanticAnalyzer::inferReturnType(FunctionDecl& fd) {
    // Find RETURN statements and infer return type
    for (const auto& stmt : fd.body) {
        if (stmt->kind == StmtKind::Return) {
            const ReturnStmt& rs = get<ReturnStmt>(stmt->data);
            if (rs.expr) {
                fd.returnType = rs.expr->type;
                return;
            }
        }
    }
    // Default to Float if no explicit return
    fd.returnType = Type::Float;
}

void SemanticAnalyzer::inferParameterTypes() {
    // Group call sites by function name
    map<string, vector<vector<Type>>> callsByFunc;
    for (const auto& call : callSites) {
        callsByFunc[call.funcName].push_back(call.argTypes);
    }
    
    // Infer parameter types for each function/sub
    for (auto& declPtr : globalSymbols.getSymbols()) {
        // This needs access to declarations - will handle in analyze()
    }
}

void SemanticAnalyzer::analyzeFunctionDecl(FunctionDecl& fd) {
    SymbolTable funcSymbols(&globalSymbols);
    
    // Initially, parameters have placeholder types
    // They'll be refined by call site inference
    
    // Pre-register function signature for recursive calls
    vector<Type> paramTypes;
    for (const auto& param : fd.params) {
        paramTypes.push_back(param.type);
    }
    userFunctions[fd.name] = FuncSignature{fd.name, paramTypes, fd.returnType, false};
    
    // Register parameters in function scope
    for (const auto& param : fd.params) {
        funcSymbols.define(param.name, param.type);
    }
    
    // Analyze body
    for (auto& stmt : fd.body) {
        analyzeStmt(*stmt, funcSymbols);
    }
    
    // Infer return type from RETURN statements
    inferReturnType(fd);
    
    // Update function signature with inferred return type
    userFunctions[fd.name] = FuncSignature{fd.name, paramTypes, fd.returnType, false};
}

void SemanticAnalyzer::analyzeSubDecl(SubDecl& sd) {
    SymbolTable subSymbols(&globalSymbols);
    
    // Pre-register sub signature for recursive calls
    vector<Type> paramTypes;
    for (const auto& param : sd.params) {
        paramTypes.push_back(param.type);
    }
    userFunctions[sd.name] = FuncSignature{sd.name, paramTypes, Type::Int, true};
    
    // Register parameters
    for (const auto& param : sd.params) {
        subSymbols.define(param.name, param.type);
    }
    
    // Analyze body
    for (auto& stmt : sd.body) {
        analyzeStmt(*stmt, subSymbols);
    }
}

void SemanticAnalyzer::analyzeDecl(Decl& decl) {
    if (decl.kind == DeclKind::Function) {
        FunctionDecl& fd = get<FunctionDecl>(decl.data);
        analyzeFunctionDecl(fd);
    } else if (decl.kind == DeclKind::Sub) {
        SubDecl& sd = get<SubDecl>(decl.data);
        analyzeSubDecl(sd);
    }
}

bool SemanticAnalyzer::analyze(Program& program) {
    errors.clear();
    callSites.clear();
    
    // PASS 1: Parse declarations and collect call sites
    for (auto& decl : program.declarations) {
        analyzeDecl(*decl);
    }
    
    // Analyze main program
    for (auto& stmt : program.statements) {
        analyzeStmt(*stmt, globalSymbols);
    }
    
    // PASS 2: Infer parameter types from call sites
    map<string, vector<vector<Type>>> callsByFunc;
    for (const auto& call : callSites) {
        callsByFunc[call.funcName].push_back(call.argTypes);
    }
    
    // Update parameter types based on call sites
    for (auto& decl : program.declarations) {
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
            // No calls found - keep default Float
            continue;
        }
        
        // Infer from first call
        const vector<Type>& firstCall = callsIt->second[0];
        if (firstCall.size() != params->size()) {
            error("Wrong number of arguments for " + funcName);
            continue;
        }
        
        for (size_t i = 0; i < params->size(); ++i) {
            (*params)[i].type = firstCall[i];
        }
        
        // Validate and refine with remaining calls
        for (size_t callIdx = 1; callIdx < callsIt->second.size(); ++callIdx) {
            const vector<Type>& callArgs = callsIt->second[callIdx];
            if (callArgs.size() != params->size()) {
                error("Wrong number of arguments for " + funcName);
                continue;
            }
            
            for (size_t i = 0; i < params->size(); ++i) {
                Type paramType = (*params)[i].type;
                Type argType = callArgs[i];
                
                if (paramType != argType) {
                    // Promote to Float if mixing Int and Float
                    if ((paramType == Type::Int && argType == Type::Float) ||
                        (paramType == Type::Float && argType == Type::Int)) {
                        (*params)[i].type = Type::Float;
                    } else if (paramType == Type::Float && 
                              (argType == Type::IntArray || argType == Type::FloatArray ||
                               argType == Type::StringArray || argType == Type::BoolArray)) {
                        // Parameter is untyped (Float default), argument is array
                        // Update parameter to match array type
                        (*params)[i].type = argType;
                    } else if (paramType == Type::Float && argType == Type::String) {
                        // Parameter is untyped (Float default), argument is String
                        (*params)[i].type = Type::String;
                    } else if (argType == Type::UserDefined || paramType == Type::UserDefined) {
                        // User-defined types (structs/classes) - allow for now
                        (*params)[i].type = Type::UserDefined;
                    } else {
                        error("Type mismatch in call to " + funcName + 
                              " at parameter " + to_string(i+1));
                    }
                }
            }
        }
        
        // Update function signature with inferred types
        vector<Type> paramTypes;
        for (const auto& param : *params) {
            paramTypes.push_back(param.type);
        }
        
        if (decl->kind == DeclKind::Function) {
            FunctionDecl& fd = get<FunctionDecl>(decl->data);
            userFunctions[fd.name] = FuncSignature{fd.name, paramTypes, fd.returnType, false};
        } else {
            userFunctions[funcName] = FuncSignature{funcName, paramTypes, Type::Int, true};
        }
    }
    
    return !hasErrors();
}

