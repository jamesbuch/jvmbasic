#include "semantic.h"
#include "builtin_functions.h"
#include <iostream>
#include <algorithm>

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
                // Not in local scope - check if it's an implicit class field (inside class method)
                if (!currentClassContext.empty()) {
                    // currentClassContext is already uppercase from analyzeClassDecl
                    auto classIt = classFieldTypes.find(currentClassContext);
                    if (classIt != classFieldTypes.end()) {
                        string fieldNameLower = vr.name;
                        transform(fieldNameLower.begin(), fieldNameLower.end(), fieldNameLower.begin(), ::tolower);

                        auto fieldIt = classIt->second.find(fieldNameLower);
                        if (fieldIt != classIt->second.end()) {
                            // It's a class field - return its type
                            return fieldIt->second;
                        }
                    }
                }
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
            // Phase 10: String concatenation - if either operand is String and op is Add, result is String
            if (bo.op == Op::Add && (leftType == Type::String || rightType == Type::String)) {
                return Type::String;
            }
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
            
            // Try to determine the object's type name
            string objectTypeName;
            if (mae.object->kind == ExprKind::Var) {
                const VarRef& vr = get<VarRef>(mae.object->data);
                // Check if we know the type name for this variable
                if (varTypeNames.find(vr.name) != varTypeNames.end()) {
                    objectTypeName = varTypeNames[vr.name];
                } else if (symbols.isDefined(vr.name)) {
                    // Variable is defined but we don't have type name - check if it's a struct/class
                    Type varType = symbols.getType(vr.name);
                    if (varType == Type::UserDefined) {
                        // This shouldn't happen - UserDefined should have type name
                        return Type::Float;
                    }
                }
            }
            
            // Look up field type in struct field types
            if (!objectTypeName.empty()) {
                // Normalize class name to uppercase for lookup (classes are stored uppercase)
                string lookupName = objectTypeName;
                transform(lookupName.begin(), lookupName.end(), lookupName.begin(), ::toupper);
                
                // Normalize member name to lowercase for lookup
                string memberNameLower = mae.member;
                transform(memberNameLower.begin(), memberNameLower.end(), memberNameLower.begin(), ::tolower);
                
                if (structFieldTypes.find(lookupName) != structFieldTypes.end() &&
                    structFieldTypes[lookupName].find(memberNameLower) != structFieldTypes[lookupName].end()) {
                    return structFieldTypes[lookupName][memberNameLower];
                }
                // Check class field types
                if (classFieldTypes.find(lookupName) != classFieldTypes.end() &&
                    classFieldTypes[lookupName].find(memberNameLower) != classFieldTypes[lookupName].end()) {
                    return classFieldTypes[lookupName][memberNameLower];
                }
            }
            
            // Default to Float if we can't determine (shouldn't happen in well-formed code)
            return Type::Float;
        }
        
        // Phase 7: OOP expressions - infer types properly
        case ExprKind::NewExpr: {
            // analyzeExpr already infers argument types recursively
            return Type::UserDefined;  // NEW returns an object instance
        }
        case ExprKind::MethodCall:
        case ExprKind::Me:
            return Type::UserDefined;  // Object methods return objects
        
        // Phase 8: Logical expressions
        case ExprKind::Logical:
            return Type::Bool;  // Logical operations always return Bool
        
        // Phase 9: Namespace calls
        case ExprKind::NamespaceCall: {
            const NamespaceCallExpr& nce = get<NamespaceCallExpr>(expr.data);
            // Determine return type based on namespace and method
            // Compare uppercase version for case-insensitivity
            string methodUpper = nce.methodName;
            for (auto& c : methodUpper) c = toupper(c);
            
            if (nce.namespaceName == "CONSOLE") {
                if (methodUpper == "WRITELINE" || methodUpper == "WRITE") {
                    return Type::Int;
                } else if (methodUpper == "READLINE" || methodUpper == "READKEY") {
                    return Type::String;
                }
            } else if (nce.namespaceName == "MATH") {
                if (methodUpper == "ROUND") {
                    return Type::Int;
                }
                return Type::Float;
            } else if (nce.namespaceName == "FILE") {
                if (methodUpper == "READALLTEXT") {
                    return Type::String;
                } else if (methodUpper == "EXISTS" || methodUpper == "DELETE" || 
                          methodUpper == "WRITEALLTEXT" || methodUpper == "COPY" || 
                          methodUpper == "MOVE" || methodUpper == "ISDIRECTORY") {
                    return Type::Int;
                } else if (methodUpper == "SIZE") {
                    return Type::Float;
                }
            } else if (nce.namespaceName == "HTTP") {
                if (methodUpper == "GET" || methodUpper == "POST" || 
                    methodUpper == "URLENCODE" || methodUpper == "URLDECODE") {
                    return Type::String;
                }
            } else if (nce.namespaceName == "JSON") {
                if (methodUpper == "PARSE" || methodUpper == "NEWOBJECT" || 
                    methodUpper == "PUT" || methodUpper == "PUTINT") {
                    return Type::Int;  // Returns object ID
                } else if (methodUpper == "GETSTRING" || methodUpper == "TOSTRING") {
                    return Type::String;
                } else if (methodUpper == "GETINT") {
                    return Type::Int;
                } else if (methodUpper == "GETFLOAT") {
                    return Type::Float;
                }
            } else if (nce.namespaceName == "DB") {
                if (methodUpper == "CONNECT" || methodUpper == "QUERY" || 
                    methodUpper == "NEXT" || methodUpper == "CLOSE" || 
                    methodUpper == "GETINT") {
                    return Type::Int;
                } else if (methodUpper == "GETSTRING") {
                    return Type::String;
                }
            } else if (nce.namespaceName == "XML") {
                if (methodUpper == "PARSE") {
                    return Type::Int;
                } else if (methodUpper == "GETTEXT") {
                    return Type::String;
                }
            } else if (nce.namespaceName == "PATH") {
                if (methodUpper == "GETFILENAME" || methodUpper == "GETEXTENSION" || 
                    methodUpper == "GETDIRECTORY" || methodUpper == "COMBINE" ||
                    methodUpper == "GETFILENAMEWITHOUTEXTENSION") {
                    return Type::String;
                }
            } else if (nce.namespaceName == "DIR") {
                if (methodUpper == "GETFILES") {
                    return Type::String;
                } else if (methodUpper == "EXISTS" || methodUpper == "CREATE") {
                    return Type::Int;
                }
            } else if (nce.namespaceName == "ARGS") {
                if (methodUpper == "GETCOUNT") {
                    return Type::Int;
                } else if (methodUpper == "GET" || methodUpper == "GETALL") {
                    return Type::String;
                } else if (methodUpper == "CONTAINS" || methodUpper == "INDEXOF") {
                    return Type::Int;
                }
            } else if (nce.namespaceName == "REGEX") {
                if (methodUpper == "MATCH") {
                    return Type::Bool;
                } else if (methodUpper == "FIND" || methodUpper == "REPLACE" || methodUpper == "GROUP") {
                    return Type::String;
                }
            } else if (nce.namespaceName == "ARRAY") {
                if (methodUpper == "SORT" || methodUpper == "SORTINT" ||
                    methodUpper == "LENGTH" || methodUpper == "LENGTHINT" || methodUpper == "LENGTHSTRING" ||
                    methodUpper == "SUMINT") {
                    return Type::Int;
                } else if (methodUpper == "MIN" || methodUpper == "MAX" ||
                           methodUpper == "AVG" || methodUpper == "SUM") {
                    return Type::Float;
                } else if (methodUpper == "MININT" || methodUpper == "MAXINT") {
                    return Type::Int;
                }
            } else if (nce.namespaceName == "STR") {
                if (methodUpper == "FORMAT" || methodUpper == "FORMATINT" ||
                    methodUpper == "FORMATFLOAT" || methodUpper == "JOIN") {
                    return Type::String;
                } else if (methodUpper == "SPLIT") {
                    return Type::StringArray;
                }
            } else if (nce.namespaceName == "INTLIST") {
                if (methodUpper == "CREATE" || methodUpper == "ADD" || methodUpper == "GET" ||
                    methodUpper == "SET" || methodUpper == "SIZE" || methodUpper == "REMOVE" ||
                    methodUpper == "INDEXOF" || methodUpper == "CLEAR") {
                    return Type::Int;
                } else if (methodUpper == "CONTAINS") {
                    return Type::Bool;
                } else if (methodUpper == "TOARRAY") {
                    return Type::IntArray;
                }
            } else if (nce.namespaceName == "STRINGLIST") {
                if (methodUpper == "CREATE" || methodUpper == "ADD" || methodUpper == "SIZE" ||
                    methodUpper == "INDEXOF" || methodUpper == "CLEAR") {
                    return Type::Int;
                } else if (methodUpper == "GET" || methodUpper == "SET" || methodUpper == "REMOVE") {
                    return Type::String;
                } else if (methodUpper == "CONTAINS") {
                    return Type::Bool;
                } else if (methodUpper == "TOARRAY") {
                    return Type::StringArray;
                }
            } else if (nce.namespaceName == "MAP") {
                if (methodUpper == "CREATE" || methodUpper == "SIZE" || methodUpper == "CLEAR") {
                    return Type::Int;
                } else if (methodUpper == "PUT" || methodUpper == "GET" || methodUpper == "REMOVE") {
                    return Type::String;
                } else if (methodUpper == "CONTAINSKEY") {
                    return Type::Bool;
                } else if (methodUpper == "KEYS" || methodUpper == "VALUES") {
                    return Type::StringArray;
                }
            } else if (nce.namespaceName == "STACK") {
                if (methodUpper == "CREATE" || methodUpper == "PUSH" ||
                    methodUpper == "SIZE" || methodUpper == "CLEAR") {
                    return Type::Int;
                } else if (methodUpper == "POP" || methodUpper == "PEEK") {
                    return Type::String;
                } else if (methodUpper == "ISEMPTY") {
                    return Type::Bool;
                }
            } else if (nce.namespaceName == "QUEUE") {
                if (methodUpper == "CREATE" || methodUpper == "ENQUEUE" ||
                    methodUpper == "SIZE" || methodUpper == "CLEAR") {
                    return Type::Int;
                } else if (methodUpper == "DEQUEUE" || methodUpper == "PEEK") {
                    return Type::String;
                } else if (methodUpper == "ISEMPTY") {
                    return Type::Bool;
                }
            }
            return Type::Float;  // Default
        }
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
            // Phase 10: Allow string concatenation (String + any type)
            if (bo.op == Op::Add && (bo.left->type == Type::String || bo.right->type == Type::String)) {
                // String concatenation is allowed with any type
                // Code generator will handle type conversion
            } else if (!isNumericType(bo.left->type) || !isNumericType(bo.right->type)) {
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
            // Nothing to analyze
            break;
            
        case ExprKind::MemberAccess: {
            MemberAccessExpr& mae = get<MemberAccessExpr>(expr.data);
            // Analyze the object expression first
            analyzeExpr(*mae.object, symbols);
            
            // Phase 7: Check private field access
            // Determine the object's type name
            string objectTypeName;
            bool isMeAccess = false;
            
            if (mae.object->kind == ExprKind::Me) {
                // ME.field - use current class context
                if (!currentClassContext.empty()) {
                    objectTypeName = currentClassContext;
                    isMeAccess = true;
                }
            } else if (mae.object->kind == ExprKind::Var) {
                const VarRef& vr = get<VarRef>(mae.object->data);
                auto varIt = varTypeNames.find(vr.name);
                if (varIt != varTypeNames.end()) {
                    objectTypeName = varIt->second;
                }
            }
            
            // Check if accessing a private field from outside the class
            if (!objectTypeName.empty() && !isMeAccess) {
                // We're accessing a field from outside the class
                // objectTypeName is already normalized to uppercase (stored that way in varTypeNames)
                string classNameUpper = objectTypeName;
                transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
                
                // Normalize member name to lowercase for lookup (field names are stored lowercase)
                string memberNameLower = mae.member;
                transform(memberNameLower.begin(), memberNameLower.end(), memberNameLower.begin(), ::tolower);
                
                // Check if this class has field access information
                auto classIt = classFieldAccess.find(classNameUpper);
                if (classIt != classFieldAccess.end()) {
                    // Check if this specific field exists
                    const auto& fieldMap = classIt->second;
                    auto fieldIt = fieldMap.find(memberNameLower);
                    if (fieldIt != fieldMap.end()) {
                        bool isPublic = fieldIt->second;
                        // Check if we're outside the class (currentClassContext is empty or different)
                        string currentContextUpper = currentClassContext;
                        transform(currentContextUpper.begin(), currentContextUpper.end(), currentContextUpper.begin(), ::toupper);
                        if (!isPublic && currentContextUpper != classNameUpper) {
                            error("Cannot access private field '" + mae.member + "' of class '" + objectTypeName + "' from outside the class");
                            return;  // Early return to avoid further processing
                        }
                    }
                    // else: Field not found - might be a struct field or invalid, skip access check
                }
                // else: class not found - might be a struct, skip access check
            }
            
            // The type is already set by inferExprType above
            break;
        }
        
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
        
        // Phase 9: Namespace calls
        case ExprKind::NamespaceCall: {
            NamespaceCallExpr& nce = get<NamespaceCallExpr>(expr.data);
            // Analyze arguments
            for (auto& arg : nce.args) {
                analyzeExpr(*arg, symbols);
            }
            // Type will be determined by the specific namespace method
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
            
            // Phase 7: Check private field access in assignments (var.member = value)
            if (ls.var.find('.') != string::npos) {
                size_t dotPos = ls.var.find('.');
                string varName = ls.var.substr(0, dotPos);
                string memberName = ls.var.substr(dotPos + 1);
                
                bool isMeAssignment = (varName == "ME");
                string objectTypeName;
                
                if (isMeAssignment) {
                    // ME.field = value - always allowed (within class)
                    if (!currentClassContext.empty()) {
                        objectTypeName = currentClassContext;
                    }
                } else {
                    // Try to find variable in varTypeNames
                    auto varIt = varTypeNames.find(varName);
                    if (varIt != varTypeNames.end()) {
                        // var.field = value - check access
                        objectTypeName = varIt->second;
                        
                        // Normalize class name to uppercase for lookup (already normalized, but be safe)
                        string classNameUpper = objectTypeName;
                        transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
                        
                        // Normalize member name to lowercase for lookup (field names are stored lowercase)
                        string memberNameLower = memberName;
                        transform(memberNameLower.begin(), memberNameLower.end(), memberNameLower.begin(), ::tolower);
                        
                        // Check if accessing a private field from outside the class
                        auto classIt = classFieldAccess.find(classNameUpper);
                        if (classIt != classFieldAccess.end()) {
                            // Check if this specific field exists
                            const auto& fieldMap = classIt->second;
                            auto fieldIt = fieldMap.find(memberNameLower);
                            if (fieldIt != fieldMap.end()) {
                                bool isPublic = fieldIt->second;
                                // Check if we're outside the class (currentClassContext is empty or different)
                                string currentContextUpper = currentClassContext;
                                transform(currentContextUpper.begin(), currentContextUpper.end(), currentContextUpper.begin(), ::toupper);
                                if (!isPublic && currentContextUpper != classNameUpper) {
                                    error("Cannot assign to private field '" + memberName + "' of class '" + objectTypeName + "' from outside the class");
                                    return;  // Early return to avoid further processing
                                }
                            }
                            // else: Field not found - might be a struct field or invalid, skip access check
                        }
                        // else: Class not found in classFieldAccess - might be a struct, skip access check
                    }
                    // else: Variable not in varTypeNames - might not be a class instance, skip access check
                }
            }
            
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
                    // Check if this is an implicit class field assignment
                    // (class field accessed without Me. prefix inside a class method)
                    bool isClassField = false;
                    if (!currentClassContext.empty()) {
                        auto classIt = classFieldTypes.find(currentClassContext);
                        if (classIt != classFieldTypes.end()) {
                            string fieldNameLower = ls.var;
                            transform(fieldNameLower.begin(), fieldNameLower.end(), fieldNameLower.begin(), ::tolower);
                            if (classIt->second.find(fieldNameLower) != classIt->second.end()) {
                                isClassField = true;
                            }
                        }
                    }

                    if (!isClassField) {
                        // Regular local variable - define it
                        symbols.define(ls.var, ls.expr->type);
                    }
                    // If it's a class field, don't define it as a local variable
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
            
            if (ds.size) {
                // Array declaration: DIM arr(size) AS Type or DIM arr(size) = initVal
                analyzeExpr(*ds.size, symbols);
                if (ds.initVal) {
                    analyzeExpr(*ds.initVal, symbols);
                }
                
                if (ds.size->type != Type::Int) {
                    error("Array size must be Int");
                }
                
                Type elemType;
                if (!ds.typeName.empty()) {
                    // DIM arr(size) AS Type - explicit type
                    if (ds.typeName == "INTEGER" || ds.typeName == "LONG") {
                        elemType = Type::Int;
                    } else if (ds.typeName == "SINGLE" || ds.typeName == "DOUBLE") {
                        elemType = Type::Float;
                    } else if (ds.typeName == "BOOLEAN") {
                        elemType = Type::Bool;
                    } else if (ds.typeName == "STRING") {
                        elemType = Type::String;
                    } else if (ds.typeName == "DECIMAL") {
                        elemType = Type::Decimal;
                    } else {  // BIGINT
                        elemType = Type::BigInt;
                    }
                } else if (ds.initVal) {
                    // DIM arr(size) = initVal - infer type from init value
                    elemType = ds.initVal->type;
                } else {
                    error("Array declaration must specify type or initial value");
                    elemType = Type::Int; // fallback
                }
                
                Type arrType = makeArrayType(elemType);
                if (symbols.isDefined(ds.var)) {
                    error("Variable already defined: " + ds.var);
                }
                symbols.define(ds.var, arrType);
            } else if (!ds.typeName.empty()) {
                // Scalar variable: DIM var AS Type = value
                bool isBuiltInType = (ds.typeName == "INTEGER" || ds.typeName == "SINGLE" || 
                                     ds.typeName == "DOUBLE" || ds.typeName == "LONG" || 
                                     ds.typeName == "BOOLEAN" || ds.typeName == "STRING" ||
                                     ds.typeName == "DECIMAL" || ds.typeName == "BIGINT");
                
                if (symbols.isDefined(ds.var)) {
                    error("Variable already defined: " + ds.var);
                }
                
                if (ds.initVal && ds.initVal->kind == ExprKind::NewExpr) {
                    // Phase 7: DIM var AS NEW ClassName(args)
                    analyzeExpr(*ds.initVal, symbols);
                    symbols.define(ds.var, Type::UserDefined);
                    // Store type name for MemberAccess type inference and access control
                    // Normalize to uppercase for consistent lookup
                    const NewExpr& ne = get<NewExpr>(ds.initVal->data);
                    string classNameUpper = ne.className;
                    transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
                    varTypeNames[ds.var] = classNameUpper;
                } else if (isBuiltInType) {
                    // Phase 9: DIM var AS Integer = value (typed scalar variable)
                    if (ds.initVal) {
                        analyzeExpr(*ds.initVal, symbols);
                    }
                    
                    // Map type name to Type enum
                    Type varType;
                    if (ds.typeName == "INTEGER" || ds.typeName == "LONG") {
                        varType = Type::Int;
                    } else if (ds.typeName == "SINGLE" || ds.typeName == "DOUBLE") {
                        varType = Type::Float;
                    } else if (ds.typeName == "BOOLEAN") {
                        varType = Type::Bool;
                    } else if (ds.typeName == "STRING") {
                        varType = Type::String;
                    } else if (ds.typeName == "DECIMAL") {
                        varType = Type::Decimal;
                    } else {  // BIGINT
                        varType = Type::BigInt;
                    }
                    
                    symbols.define(ds.var, varType);
                } else {
                    // Phase 6: DIM var AS TypeName (user-defined struct/class)
                    symbols.define(ds.var, Type::UserDefined);
                    // Store type name for MemberAccess type inference
                    varTypeNames[ds.var] = ds.typeName;
                }
            } else if (ds.initVal && ds.initVal->kind == ExprKind::NewExpr) {
                // Phase 7: DIM var AS NEW ClassName() - size is nullptr!
                if (symbols.isDefined(ds.var)) {
                    error("Variable already defined: " + ds.var);
                }
                analyzeExpr(*ds.initVal, symbols);
                symbols.define(ds.var, Type::UserDefined);
                // Store type name for MemberAccess type inference and access control
                // Normalize to uppercase for consistent lookup
                const NewExpr& ne = get<NewExpr>(ds.initVal->data);
                string classNameUpper = ne.className;
                transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
                varTypeNames[ds.var] = classNameUpper;
                // Debug: ensure varTypeNames is populated
            } else {
                error("Invalid DIM statement - must specify type with AS keyword");
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
            // Phase 8/9: Control flow statements - no semantic analysis needed
            break;
        
        case StmtKind::ExprStmt: {
            // Phase 9: Expression statement - analyze the expression
            ExprStmtNode& es = get<ExprStmtNode>(stmt.data);
            analyzeExpr(*es.expr, symbols);
            // Note: Result is discarded, but expression must be valid
            break;
        }
    }
}

void SemanticAnalyzer::inferReturnType(FunctionDecl& fd) {
    // Phase 9: Only infer return type if it wasn't explicitly specified
    // If function declared as "Function Name() As Type", don't override
    // Default returnType is Float, so if it's anything else, it was explicitly set
    bool wasExplicitlyTyped = (fd.returnType != Type::Float);
    
    // Find RETURN statements and validate types
    for (const auto& stmt : fd.body) {
        if (stmt->kind == StmtKind::Return) {
            const ReturnStmt& rs = get<ReturnStmt>(stmt->data);
            if (rs.expr) {
                // CRITICAL FIX: Validate return type matches declared type
                if (wasExplicitlyTyped) {
                    // Function has explicit return type - validate it matches
                    Type returnExprType = rs.expr->type;
                    if (returnExprType != fd.returnType) {
                        // Allow numeric promotion (Int -> Float) but warn
                        if ((fd.returnType == Type::Float && returnExprType == Type::Int) ||
                            (fd.returnType == Type::Int && returnExprType == Type::Float)) {
                            // Numeric promotion is OK, but we need to ensure codegen handles it
                            // For now, convert Int return to Float if function expects Float
                            if (fd.returnType == Type::Float && returnExprType == Type::Int) {
                                // This is OK - codegen will handle i2f conversion
                            } else {
                                // Float return in Int function - this is an error
                                error("Function " + fd.name + " declared As Integer but returns Float value");
                            }
                        } else {
                            error("Function " + fd.name + " declared As " + typeToString(fd.returnType) + 
                                  " but returns " + typeToString(returnExprType));
                        }
                    }
                } else {
                    // No explicit type - infer from return expression
                    fd.returnType = rs.expr->type;
                }
                return;
            }
        }
    }
    
    // Default to Float if no explicit return found and no explicit type
    if (!wasExplicitlyTyped) {
        fd.returnType = Type::Float;
    }
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
    } else if (decl.kind == DeclKind::Class) {
        ClassDecl& cd = get<ClassDecl>(decl.data);
        analyzeClassDecl(cd);
    }
}

// Phase 7: Analyze class declaration (methods and their bodies)
void SemanticAnalyzer::analyzeClassDecl(ClassDecl& cd) {
    // Set current class context for method analysis
    // Normalize to uppercase for consistent lookup
    string savedContext = currentClassContext;
    string classNameUpper = cd.name;
    transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
    currentClassContext = classNameUpper;
    
    SymbolTable classSymbols(&globalSymbols);
    
    // Analyze each method
    for (auto& method : cd.methods) {
        // Register parameters
        for (const auto& param : method.params) {
            classSymbols.define(param.name, param.type);
        }
        
        // Analyze method body with class context set
        for (auto& stmt : method.body) {
            analyzeStmt(*stmt, classSymbols);
        }
        
        // If it's a function, infer return type
        if (!method.isSub && !method.isConstructor) {
            // Similar to analyzeFunctionDecl - find RETURN statements
            for (const auto& stmt : method.body) {
                if (stmt->kind == StmtKind::Return) {
                    const ReturnStmt& rs = get<ReturnStmt>(stmt->data);
                    if (rs.expr) {
                        // Type is already inferred by analyzeStmt
                        // Validate return type matches declared type
                        if (method.returnType != Type::Float && rs.expr->type != method.returnType) {
                            // Allow numeric promotion
                            if (!((method.returnType == Type::Int && rs.expr->type == Type::Float) ||
                                  (method.returnType == Type::Float && rs.expr->type == Type::Int))) {
                                error("Method " + cd.name + "." + method.name + " return type mismatch");
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Restore previous context
    currentClassContext = savedContext;
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
    
    // PASS 3: Flatten string concatenations in AST
    flattenStringConcats(program);
    
    return !hasErrors();
}

// Helper function to collect string concatenation operands
// This is called AFTER flattening, so operands might already be StringConcatExpr
static void collectStringConcatOperands(ExprPtr e, vector<ExprPtr>& operands) {
    if (!e) return;
    
    // Check StringConcat first (most common after flattening)
    if (e->kind == ExprKind::StringConcat) {
        // Already flattened - add its operands
        StringConcatExpr& sc = get<StringConcatExpr>(e->data);
        for (auto& op : sc.operands) {
            operands.push_back(move(op));
        }
    } else if (e->kind == ExprKind::Bin) {
        // Might still be BinOp if flattening didn't convert it (non-string concat)
        BinOp& b = get<BinOp>(e->data);
        if (b.op == Op::Add && (b.left->type == Type::String || b.right->type == Type::String)) {
            // It's a nested string concat - recurse (shouldn't happen after flattening, but handle it)
            collectStringConcatOperands(move(b.left), operands);
            collectStringConcatOperands(move(b.right), operands);
        } else {
            // Not a string concat - add as-is
            operands.push_back(move(e));
        }
    } else {
        // Leaf node - add as operand
        operands.push_back(move(e));
    }
}

// Phase 10: Flatten nested string concatenations into StringConcatExpr
ExprPtr SemanticAnalyzer::flattenStringConcat(ExprPtr expr) {
    if (!expr) return nullptr;
    
    // Recursively flatten sub-expressions first
    switch (expr->kind) {
        case ExprKind::Bin: {
            BinOp& bo = get<BinOp>(expr->data);
            // Flatten left and right first (they might become StringConcatExpr)
            bo.left = flattenStringConcat(move(bo.left));
            bo.right = flattenStringConcat(move(bo.right));
            
            // Check if this is a string concatenation
            // After flattening, left/right might be StringConcatExpr, but type should still be String
            if (bo.op == Op::Add && (bo.left->type == Type::String || bo.right->type == Type::String)) {
                // Collect all operands into a flat list
                // collectStringConcatOperands handles both BinOp and StringConcatExpr
                vector<ExprPtr> operands;
                collectStringConcatOperands(move(bo.left), operands);
                collectStringConcatOperands(move(bo.right), operands);
                
                // Create flattened StringConcatExpr - this replaces the BinOp
                return make_unique<Expr>(ExprKind::StringConcat, Type::String, 
                                        StringConcatExpr{move(operands)});
            }
            // Not a string concat - return as-is (but left/right are already flattened)
            return expr;
        }
        
        case ExprKind::StringConcat: {
            // Already flattened, but recursively flatten its operands
            StringConcatExpr& sc = get<StringConcatExpr>(expr->data);
            for (auto& op : sc.operands) {
                op = flattenStringConcat(move(op));
            }
            return expr;
        }
        
        case ExprKind::Call: {
            CallExpr& ce = get<CallExpr>(expr->data);
            for (auto& arg : ce.args) {
                arg = flattenStringConcat(move(arg));
            }
            return expr;
        }
        
        case ExprKind::NamespaceCall: {
            NamespaceCallExpr& nce = get<NamespaceCallExpr>(expr->data);
            for (auto& arg : nce.args) {
                arg = flattenStringConcat(move(arg));
            }
            return expr;
        }
        
        case ExprKind::MethodCall: {
            MethodCallExpr& mce = get<MethodCallExpr>(expr->data);
            mce.object = flattenStringConcat(move(mce.object));
            for (auto& arg : mce.args) {
                arg = flattenStringConcat(move(arg));
            }
            return expr;
        }
        
        case ExprKind::MemberAccess: {
            MemberAccessExpr& mae = get<MemberAccessExpr>(expr->data);
            mae.object = flattenStringConcat(move(mae.object));
            return expr;
        }
        
        case ExprKind::NewExpr: {
            NewExpr& ne = get<NewExpr>(expr->data);
            for (auto& arg : ne.args) {
                arg = flattenStringConcat(move(arg));
            }
            return expr;
        }
        
        default:
            // Other expression types don't have sub-expressions that need flattening
            return expr;
    }
}

// Helper to flatten string concats in statements
void SemanticAnalyzer::flattenStmt(Stmt& stmt) {
    switch (stmt.kind) {
        case StmtKind::Let: {
            LetStmt& ls = get<LetStmt>(stmt.data);
            if (ls.expr) {
                ls.expr = flattenStringConcat(move(ls.expr));
            }
            break;
        }
        case StmtKind::Return: {
            ReturnStmt& rs = get<ReturnStmt>(stmt.data);
            if (rs.expr) {
                rs.expr = flattenStringConcat(move(rs.expr));
            }
            break;
        }
        case StmtKind::Print: {
            PrintStmt& ps = get<PrintStmt>(stmt.data);
            for (auto& expr : ps.exprs) {
                expr = flattenStringConcat(move(expr));
            }
            break;
        }
        case StmtKind::ExprStmt: {
            ExprStmtNode& es = get<ExprStmtNode>(stmt.data);
            if (es.expr) {
                es.expr = flattenStringConcat(move(es.expr));
            }
            break;
        }
        case StmtKind::If: {
            IfStmt& is = get<IfStmt>(stmt.data);
            if (is.cond) {
                is.cond = flattenStringConcat(move(is.cond));
            }
            for (auto& s : is.thenBody) {
                flattenStmt(*s);
            }
            for (auto& s : is.elseBody) {
                flattenStmt(*s);
            }
            break;
        }
        case StmtKind::For: {
            ForStmt& fs = get<ForStmt>(stmt.data);
            if (fs.start) fs.start = flattenStringConcat(move(fs.start));
            if (fs.end) fs.end = flattenStringConcat(move(fs.end));
            if (fs.step) fs.step = flattenStringConcat(move(fs.step));
            for (auto& s : fs.body) {
                flattenStmt(*s);
            }
            break;
        }
        case StmtKind::While: {
            WhileStmt& ws = get<WhileStmt>(stmt.data);
            if (ws.cond) {
                ws.cond = flattenStringConcat(move(ws.cond));
            }
            for (auto& s : ws.body) {
                flattenStmt(*s);
            }
            break;
        }
        default:
            // Other statement types don't contain expressions that need flattening
            break;
    }
}

// Flatten string concatenations in the entire program
void SemanticAnalyzer::flattenStringConcats(Program& program) {
    // Flatten in declarations (function/sub bodies)
    for (auto& decl : program.declarations) {
        if (decl->kind == DeclKind::Function) {
            FunctionDecl& fd = get<FunctionDecl>(decl->data);
            for (auto& stmt : fd.body) {
                flattenStmt(*stmt);
            }
        } else if (decl->kind == DeclKind::Sub) {
            SubDecl& sd = get<SubDecl>(decl->data);
            for (auto& stmt : sd.body) {
                flattenStmt(*stmt);
            }
        } else if (decl->kind == DeclKind::Class) {
            ClassDecl& cd = get<ClassDecl>(decl->data);
            for (auto& method : cd.methods) {
                for (auto& stmt : method.body) {
                    flattenStmt(*stmt);
                }
            }
        }
    }
    
    // Flatten in main program statements
    for (auto& stmt : program.statements) {
        flattenStmt(*stmt);
    }
}

// Initialize struct field types from parser
void SemanticAnalyzer::initStructTypes(const map<string, TypeDefDecl>& userTypes) {
    for (const auto& [typeName, typeDef] : userTypes) {
        // Normalize type name to uppercase for consistent lookup
        string typeNameUpper = typeName;
        transform(typeNameUpper.begin(), typeNameUpper.end(), typeNameUpper.begin(), ::toupper);
        for (const Field& field : typeDef.fields) {
            // Normalize field name to lowercase for consistent lookup
            string fieldNameLower = field.name;
            transform(fieldNameLower.begin(), fieldNameLower.end(), fieldNameLower.begin(), ::tolower);
            structFieldTypes[typeNameUpper][fieldNameLower] = field.type;
        }
    }
}

// Initialize class field types from program declarations
void SemanticAnalyzer::initClassTypesFromProgram(const Program& program) {
    for (const auto& decl : program.declarations) {
        if (decl->kind == DeclKind::Class) {
            const ClassDecl& cd = get<ClassDecl>(decl->data);
            // Normalize class name to uppercase for consistent lookup
            string classNameUpper = cd.name;
            transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
            for (const Field& field : cd.fields) {
                // Normalize field name to lowercase for consistent lookup
                string fieldNameLower = field.name;
                transform(fieldNameLower.begin(), fieldNameLower.end(), fieldNameLower.begin(), ::tolower);
                classFieldTypes[classNameUpper][fieldNameLower] = field.type;
                classFieldAccess[classNameUpper][fieldNameLower] = field.isPublic;
            }
        }
    }
}

