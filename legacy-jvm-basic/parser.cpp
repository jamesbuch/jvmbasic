#include "parser.h"
#include "builtin_functions.h"
#include <cctype>
#include <cmath>
#include <algorithm>

Parser::Parser(Lexer& l) : lex(l) {
    next();
}

void Parser::next() {
    tok = lex.nextToken();
}

Token Parser::expect(TokenType tt) {
    if (tok.type == tt) {
        Token res = tok;
        next();
        return res;
    }
    string expected = tokenTypeName(tt);
    string got = tok.val.empty() ? tokenTypeName(tok.type) : "'" + tok.val + "'";
    error("Expected " + expected + " but got " + got);
    return tok;
}

void Parser::error(const string& msg) {
    throw runtime_error("Line " + to_string(tok.line) + ": " + msg);
}

string Parser::tokenTypeName(TokenType tt) {
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

// Resolve type name to Type enum
Type Parser::resolveTypeName(const string& typeName) {
    string upper = typeName;
    transform(upper.begin(), upper.end(), upper.begin(), ::toupper);

    if (upper == "INT" || upper == "INTEGER") return Type::Int;
    if (upper == "FLOAT" || upper == "SINGLE") return Type::Float;
    if (upper == "DOUBLE") return Type::Double;
    if (upper == "STRING") return Type::String;
    if (upper == "BOOL" || upper == "BOOLEAN") return Type::Bool;
    if (upper == "DECIMAL") return Type::Decimal;
    if (upper == "BIGINT") return Type::BigInt;
    if (upper == "INTARRAY") return Type::IntArray;
    if (upper == "FLOATARRAY") return Type::FloatArray;
    if (upper == "DOUBLEARRAY") return Type::DoubleArray;
    if (upper == "STRINGARRAY") return Type::StringArray;
    if (upper == "BOOLARRAY") return Type::BoolArray;
    
    // Check if it's a user-defined type (TYPE)
    if (userTypes.count(upper)) {
        return Type::UserDefined;
    }
    
    // Phase 7: Check if it's a user-defined class (CLASS)
    if (userClassNames.count(upper)) {
        return Type::UserDefined;
    }
    
    // Unknown type - default to UserDefined and store name for later
    return Type::UserDefined;
}

// Parse user-defined type (TYPE...ENDTYPE)
DeclPtr Parser::parseTypeDecl() {
    expect(TokenType::TYPE);
    string typeName = expect(TokenType::ID).val;
    
    // Normalize type name to uppercase for consistent lookup
    string typeNameUpper = typeName;
    transform(typeNameUpper.begin(), typeNameUpper.end(), typeNameUpper.begin(), ::toupper);
    
    vector<Field> fields;
    while (tok.type != TokenType::ENDTYPE && tok.type != TokenType::END) {
        string fieldName = expect(TokenType::ID).val;
        expect(TokenType::AS);
        
        // Phase 9: Accept both old-style ID and new-style type keywords
        string fieldTypeName;
        if (tok.type == TokenType::INTEGER) {
            fieldTypeName = "INTEGER";
            next();
        } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
            fieldTypeName = "FLOAT";
            next();
        } else if (tok.type == TokenType::DOUBLE) {
            fieldTypeName = "DOUBLE";
            next();
        } else if (tok.type == TokenType::LONG) {
            fieldTypeName = "LONG";
            next();
        } else if (tok.type == TokenType::BOOLEAN) {
            fieldTypeName = "BOOLEAN";
            next();
        } else if (tok.type == TokenType::STRINGTYPE) {
            fieldTypeName = "STRING";
            next();
        } else if (tok.type == TokenType::DECIMAL) {
            fieldTypeName = "DECIMAL";
            next();
        } else if (tok.type == TokenType::BIGINT) {
            fieldTypeName = "BIGINT";
            next();
        } else {
            fieldTypeName = expect(TokenType::ID).val;
        }
        
        // Resolve field type
        Type fieldType = resolveTypeName(fieldTypeName);
        fields.push_back(Field{fieldName, fieldType, fieldTypeName});
    }
    expect(TokenType::ENDTYPE);
    
    // Store with uppercase name for consistent lookup
    return make_unique<Decl>(DeclKind::TypeDef, TypeDefDecl{typeNameUpper, fields});
}

// Phase 7: Parse method declaration within a class
MethodDecl Parser::parseMethodDecl(bool isPublic) {
    bool isConstructor = false;
    Type returnType = Type::Float;  // Default for SUB
    string name;
    bool isSub = false;  // Track if this is SUB or FUNCTION
    
    if (tok.type == TokenType::SUB) {
        isSub = true;
        next();
        
        // Special case: "SUB New" - NEW is a keyword but also the constructor name
        if (tok.type == TokenType::NEW) {
            name = "New";
            isConstructor = true;
            next();
        } else {
            name = expect(TokenType::ID).val;
            
            // Check if it's a constructor (SUB New - if it wasn't tokenized as NEW)
            string nameUpper = name;
            transform(nameUpper.begin(), nameUpper.end(), nameUpper.begin(), ::toupper);
            if (nameUpper == "NEW") {
                isConstructor = true;
            }
        }
    } else if (tok.type == TokenType::FUNCTION) {
        isSub = false;
        next();
        name = expect(TokenType::ID).val;
        // Return type will be determined from AS clause or inferred from RETURN
    } else {
        error("Expected SUB or FUNCTION in method declaration");
    }
    
    // Parse parameters
    expect(TokenType::LPAREN);
    vector<Param> params;
    if (tok.type != TokenType::RPAREN) {
        do {
            if (tok.type == TokenType::COMMA) next();
            
            string paramName = expect(TokenType::ID).val;
            Type paramType = Type::Float;  // Default
            string paramTypeName;
            
            // Phase 9: Check for AS Type (accepting type keywords)
            if (tok.type == TokenType::AS) {
                next();
                if (tok.type == TokenType::INTEGER) {
                    paramTypeName = "INTEGER";
                    paramType = Type::Int;
                    next();
                } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                    paramTypeName = "FLOAT";
                    paramType = Type::Float;
                    next();
                } else if (tok.type == TokenType::DOUBLE) {
                    paramTypeName = "DOUBLE";
                    paramType = Type::Double;
                    next();
                } else if (tok.type == TokenType::LONG) {
                    paramTypeName = "LONG";
                    paramType = Type::Int;
                    next();
                } else if (tok.type == TokenType::BOOLEAN) {
                    paramTypeName = "BOOLEAN";
                    paramType = Type::Bool;
                    next();
                } else if (tok.type == TokenType::STRINGTYPE) {
                    paramTypeName = "STRING";
                    paramType = Type::String;
                    next();
                } else if (tok.type == TokenType::DECIMAL) {
                    paramTypeName = "DECIMAL";
                    paramType = Type::Decimal;
                    next();
                } else if (tok.type == TokenType::BIGINT) {
                    paramTypeName = "BIGINT";
                    paramType = Type::BigInt;
                    next();
                } else {
                    paramTypeName = expect(TokenType::ID).val;
                    paramType = resolveTypeName(paramTypeName);
                }
            }
            
            params.push_back(Param{paramName, paramType, paramTypeName});
        } while (tok.type == TokenType::COMMA);
    }
    expect(TokenType::RPAREN);
    
    // Phase 9: Check for return type on FUNCTION (accepting type keywords)
    if (!isConstructor && tok.type == TokenType::AS) {
        next();
        if (tok.type == TokenType::INTEGER) {
            returnType = Type::Int;
            next();
        } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
            returnType = Type::Float;
            next();
        } else if (tok.type == TokenType::DOUBLE) {
            returnType = Type::Double;
            next();
        } else if (tok.type == TokenType::LONG) {
            returnType = Type::Int;
            next();
        } else if (tok.type == TokenType::BOOLEAN) {
            returnType = Type::Bool;
            next();
        } else if (tok.type == TokenType::STRINGTYPE) {
            returnType = Type::String;
            next();
        } else if (tok.type == TokenType::DECIMAL) {
            returnType = Type::Decimal;
            next();
        } else if (tok.type == TokenType::BIGINT) {
            returnType = Type::BigInt;
            next();
        } else {
            string returnTypeName = expect(TokenType::ID).val;
            returnType = resolveTypeName(returnTypeName);
        }
    }
    
    // Parse method body
    vector<StmtPtr> body;
    while (tok.type != TokenType::ENDSUB && 
           tok.type != TokenType::ENDFUNCTION && 
           tok.type != TokenType::END &&
           tok.type != TokenType::PUBLIC &&
           tok.type != TokenType::PRIVATE &&
           tok.type != TokenType::ENDCLASS) {
        body.push_back(parseStmt());
    }
    
    // Expect END SUB or END FUNCTION
    if (tok.type == TokenType::ENDSUB || tok.type == TokenType::ENDFUNCTION) {
        next();
    } else {
        error("Expected ENDSUB or ENDFUNCTION");
    }
    
    MethodDecl method;
    method.name = name;
    method.isPublic = isPublic;
    method.isConstructor = isConstructor;
    method.isSub = isSub;  // Track if it was SUB or FUNCTION
    method.params = move(params);
    method.returnType = returnType;
    method.body = move(body);
    
    return method;
}

// Phase 7: Parse CLASS...END CLASS
DeclPtr Parser::parseClassDecl() {
    expect(TokenType::CLASS);
    string className = expect(TokenType::ID).val;
    
    // Normalize class name to uppercase for consistent lookup
    string classNameUpper = className;
    transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
    
    vector<Field> fields;
    vector<MethodDecl> methods;
    
    while (tok.type != TokenType::ENDCLASS && tok.type != TokenType::END) {
        bool isPublic = true;  // Default
        
        // Check for access modifier
        if (tok.type == TokenType::PUBLIC) {
            isPublic = true;
            next();
        } else if (tok.type == TokenType::PRIVATE) {
            isPublic = false;
            next();
        }
        
        // Check what follows the modifier
        if (tok.type == TokenType::SUB || tok.type == TokenType::FUNCTION) {
            // It's a method
            methods.push_back(move(parseMethodDecl(isPublic)));
        } else if (tok.type == TokenType::ID) {
            // It's a field declaration: PUBLIC/PRIVATE name AS Type
            string fieldName = expect(TokenType::ID).val;
            expect(TokenType::AS);
            
            // Phase 9: Accept both old-style ID and new-style type keywords
            string fieldTypeName;
            if (tok.type == TokenType::INTEGER) {
                fieldTypeName = "INTEGER";
                next();
            } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                fieldTypeName = "FLOAT";
                next();
            } else if (tok.type == TokenType::DOUBLE) {
                fieldTypeName = "DOUBLE";
                next();
            } else if (tok.type == TokenType::LONG) {
                fieldTypeName = "LONG";
                next();
            } else if (tok.type == TokenType::BOOLEAN) {
                fieldTypeName = "BOOLEAN";
                next();
            } else if (tok.type == TokenType::STRINGTYPE) {
                fieldTypeName = "STRING";
                next();
            } else if (tok.type == TokenType::DECIMAL) {
                fieldTypeName = "DECIMAL";
                next();
            } else if (tok.type == TokenType::BIGINT) {
                fieldTypeName = "BIGINT";
                next();
            } else {
                fieldTypeName = expect(TokenType::ID).val;
            }

            Type fieldType = resolveTypeName(fieldTypeName);
            fields.push_back(Field{fieldName, fieldType, fieldTypeName, isPublic});
        } else {
            error("Expected field or method declaration in CLASS");
        }
    }
    
    expect(TokenType::ENDCLASS);
    
    return make_unique<Decl>(DeclKind::Class, ClassDecl{classNameUpper, move(fields), move(methods)});
}

ExprPtr Parser::parsePrimary() {
    // Handle unary minus
    if (tok.type == TokenType::MINUS) {
        next();
        auto operand = parsePrimary();
        if (!operand) {
            error("Expected expression after '-'");
            return make_unique<Expr>(ExprKind::Num, Type::Int, NumLit{0});
        }
        Type opType = operand->type;
        auto unaryExpr = UnaryExpr{UnaryOp::Neg, move(operand)};
        return make_unique<Expr>(ExprKind::Unary, opType, move(unaryExpr));
    }
    
    // Phase 7: NEW expression for object creation
    if (tok.type == TokenType::NEW) {
        next();
        string className = expect(TokenType::ID).val;
        
        // Normalize class name
        string classNameUpper = className;
        transform(classNameUpper.begin(), classNameUpper.end(), classNameUpper.begin(), ::toupper);
        
        // Parentheses are optional for no-arg constructors
        vector<ExprPtr> args;
        if (tok.type == TokenType::LPAREN) {
            next();
            if (tok.type != TokenType::RPAREN) {
                args.push_back(parseExpr());
                while (tok.type == TokenType::COMMA) {
                    next();
                    args.push_back(parseExpr());
                }
            }
            expect(TokenType::RPAREN);
        }
        // If no parentheses, assume no-arg constructor (args remains empty)
        
        // Return UserDefined type for class instances
        return make_unique<Expr>(ExprKind::NewExpr, Type::UserDefined, NewExpr{classNameUpper, move(args)});
    }
    
    // Phase 7: ME expression (self reference)
    if (tok.type == TokenType::ME) {
        next();
        // Create ME expression
        ExprPtr expr = make_unique<Expr>(ExprKind::Me, Type::UserDefined, MeExpr{});
        
        // Check for member access: ME.field or ME.method()
        while (tok.type == TokenType::DOT) {
            next();
            string member = expect(TokenType::ID).val;
            
            // Check if it's a method call (followed by parentheses)
            if (tok.type == TokenType::LPAREN) {
                next();
                vector<ExprPtr> args;
                if (tok.type != TokenType::RPAREN) {
                    args.push_back(parseExpr());
                    while (tok.type == TokenType::COMMA) {
                        next();
                        args.push_back(parseExpr());
                    }
                }
                expect(TokenType::RPAREN);
                
                // Method call: ME.method(args)
                expr = make_unique<Expr>(ExprKind::MethodCall, Type::Float,
                                       MethodCallExpr{move(expr), member, move(args)});
            } else {
                // Property/field access: ME.field
                expr = make_unique<Expr>(ExprKind::MemberAccess, Type::Float, 
                                       MemberAccessExpr{move(expr), member});
            }
        }
        
        return expr;
    }
    
    if (tok.type == TokenType::NUMBER) {
        Token nt = expect(TokenType::NUMBER);
        // A number is Float if it has a decimal point in the original string
        bool isFloat = (nt.val.find('.') != string::npos);
        Type ty = isFloat ? Type::Float : Type::Int;
        return make_unique<Expr>(ExprKind::Num, ty, NumLit{nt.num});
    }
    
    if (tok.type == TokenType::STRING) {
        Token st = expect(TokenType::STRING);
        return make_unique<Expr>(ExprKind::Str, Type::String, StrLit{st.val});
    }
    
    // Phase 10: Interpolated strings ($"text {var} more")
    if (tok.type == TokenType::INTERPOLATED_STRING) {
        vector<InterpolationPart> parts = tok.interpolationParts;
        next();
        
        // If no interpolation parts, return empty string
        if (parts.empty()) {
            return make_unique<Expr>(ExprKind::Str, Type::String, StrLit{""});
        }
        
        // Build string concatenation expression
        ExprPtr result = nullptr;
        
        for (size_t i = 0; i < parts.size(); i++) {
            const auto& part = parts[i];
            ExprPtr partExpr;
            
            if (part.type == InterpolationPart::Type::TEXT) {
                // Text literal
                partExpr = make_unique<Expr>(ExprKind::Str, Type::String, StrLit{part.value});
            } else {
                // Variable reference - need to parse array access
                string varName = part.value;
                
                // Check if it's array access (contains parentheses)
                size_t parenPos = varName.find('(');
                if (parenPos != string::npos) {
                    // Array access: varName(index)
                    string arrayName = varName.substr(0, parenPos);
                    string indexStr = varName.substr(parenPos + 1);
                    
                    // Remove closing parenthesis
                    if (indexStr.back() == ')') {
                        indexStr.pop_back();
                    }
                    
                    // Parse the index as an expression
                    // For now, handle simple integer indices
                    int index = 0;
                    try {
                        index = stoi(indexStr);
                    } catch (...) {
                        // If parsing fails, treat as 0
                        index = 0;
                    }
                    
                    // Create array access expression
                    auto indexExpr = make_unique<Expr>(ExprKind::Num, Type::Int, NumLit{static_cast<double>(index)});
                    partExpr = make_unique<Expr>(ExprKind::Var, Type::String, VarRef{arrayName, move(indexExpr)});
                } else {
                    // Simple variable reference
                    partExpr = make_unique<Expr>(ExprKind::Var, Type::String, VarRef{varName, nullptr});
                }
            }
            
            // Concatenate with result
            if (result == nullptr) {
                result = move(partExpr);
            } else {
                // result + partExpr
                result = make_unique<Expr>(ExprKind::Bin, Type::String, 
                                         BinOp{Op::Add, move(result), move(partExpr)});
            }
        }
        
        return result ? move(result) : make_unique<Expr>(ExprKind::Str, Type::String, StrLit{""});
    }
    
    if (tok.type == TokenType::TRUE) {
        next();
        return make_unique<Expr>(ExprKind::BoolLit, Type::Bool, BoolLit{true});
    }
    
    if (tok.type == TokenType::FALSE) {
        next();
        return make_unique<Expr>(ExprKind::BoolLit, Type::Bool, BoolLit{false});
    }
    
    // Phase 9: Check for namespace keyword tokens (CONSOLE, etc.)
    if (tok.type == TokenType::CONSOLE) {
        string namespaceName = "CONSOLE";
        next();
        if (tok.type == TokenType::DOT) {
            next();  // Consume DOT
            string methodName = expect(TokenType::ID).val;
            string methodUpper = methodName;
            for (auto& c : methodUpper) c = toupper(c);

            // Parse arguments
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

            // Create namespace call expression
            // Store original methodName to preserve casing (WriteLine not WRITELINE)
            return make_unique<Expr>(ExprKind::NamespaceCall, Type::Float,
                                   NamespaceCallExpr{namespaceName, methodName, move(args)});
        }
        // If not followed by DOT, error
        error("Expected '.' after Console");
    }

    // Phase 11: Handle BIGINT namespace calls (BigInt.FromString, etc.)
    if (tok.type == TokenType::BIGINT) {
        next();
        if (tok.type == TokenType::DOT) {
            next();  // Consume DOT
            string methodName = expect(TokenType::ID).val;

            // Parse arguments
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

            // Return type is BigInt for most operations
            return make_unique<Expr>(ExprKind::NamespaceCall, Type::BigInt,
                                   NamespaceCallExpr{"BIGINT", methodName, move(args)});
        }
        // If not followed by DOT, it's being used as a type name (shouldn't reach here in expression context)
        error("Expected '.' after BigInt");
    }

    // Phase 11: Handle DECIMAL namespace calls (Decimal.FromString, etc.)
    if (tok.type == TokenType::DECIMAL) {
        next();
        if (tok.type == TokenType::DOT) {
            next();  // Consume DOT
            string methodName = expect(TokenType::ID).val;

            // Parse arguments
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

            // Return type is Decimal for most operations
            return make_unique<Expr>(ExprKind::NamespaceCall, Type::Decimal,
                                   NamespaceCallExpr{"DECIMAL", methodName, move(args)});
        }
        // If not followed by DOT, it's being used as a type name (shouldn't reach here in expression context)
        error("Expected '.' after Decimal");
    }
    
    if (tok.type == TokenType::ID) {
        string name = tok.val;
        string nameUpper = name;
        for (auto& c : nameUpper) c = toupper(c);
        next();
        
        // Phase 9: Check for namespace call: Namespace.Method()
        if (tok.type == TokenType::DOT) {
            // Check if it's a known namespace
            bool isNamespace = (nameUpper == "MATH" ||
                               nameUpper == "FILE" || nameUpper == "HTTP" ||
                               nameUpper == "JSON" || nameUpper == "XML" ||
                               nameUpper == "DB" || nameUpper == "PATH" ||
                               nameUpper == "DIR" || nameUpper == "ARGS" ||
                               nameUpper == "REGEX" || nameUpper == "ARRAY" ||
                               nameUpper == "STR" || nameUpper == "INTLIST" ||
                               nameUpper == "STRINGLIST" || nameUpper == "MAP" ||
                               nameUpper == "STACK" || nameUpper == "QUEUE" ||
                               nameUpper == "CRYPTO" || nameUpper == "THREAD" ||
                               nameUpper == "BIGINT" || nameUpper == "DECIMAL" ||
                               nameUpper == "SYSTEM" ||  // Phase 12: System namespace
                               nameUpper == "WEBSERVER" || nameUpper == "REQUEST" ||
                               nameUpper == "RESPONSE");  // Phase 12: Web server namespaces

            if (isNamespace) {
                // Parse Namespace.Method(args)
                next();  // Consume DOT
                // Accept either ID or EXIT keyword as method name (for System.exit)
                string methodName;
                if (tok.type == TokenType::ID) {
                    methodName = tok.val;
                    next();
                } else if (tok.type == TokenType::EXIT) {
                    methodName = "exit";
                    next();
                } else {
                    error("Expected identifier but got '" + tok.val + "'");
                    methodName = "error";
                }
                // Store original method name for proper camelCase generation
                
                // Parse arguments
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
                
                // Create namespace call expression
                // Store original method name (preserves casing like WriteLine)
                return make_unique<Expr>(ExprKind::NamespaceCall, Type::Float,
                                       NamespaceCallExpr{nameUpper, methodName, move(args)});
            }
            // If not a namespace, fall through to normal member access handling
            // (will be handled later for variable.Method() syntax)
        }
        
        // Check if it's a built-in function call
        auto funcIt = builtinFunctions.find(nameUpper);
        if (funcIt != builtinFunctions.end()) {
            const FunctionSig& sig = funcIt->second;
            vector<ExprPtr> args;
            
            if (sig.paramTypes.empty()) {
                if (tok.type == TokenType::LPAREN) {
                    next();
                    expect(TokenType::RPAREN);
                }
            } else {
                expect(TokenType::LPAREN);
                for (size_t i = 0; i < sig.paramTypes.size(); ++i) {
                    args.push_back(parseExpr());
                    if (i < sig.paramTypes.size() - 1) {
                        expect(TokenType::COMMA);
                    }
                }
                expect(TokenType::RPAREN);
            }
            
            return make_unique<Expr>(ExprKind::Call, sig.returnType, CallExpr{nameUpper, move(args)});
        }
        
        // Check for function call (user-defined or to be determined)
        if (tok.type == TokenType::LPAREN) {
            next();
            vector<ExprPtr> args;
            if (tok.type != TokenType::RPAREN) {
                args.push_back(parseExpr());
                while (tok.type == TokenType::COMMA) {
                    next();
                    args.push_back(parseExpr());
                }
            }
            expect(TokenType::RPAREN);
            
            // Return type unknown - will be determined by semantic analysis
            return make_unique<Expr>(ExprKind::Call, Type::Float, CallExpr{name, move(args)});
        }
        
        // Check for array indexing
        ExprPtr index = nullptr;
        if (tok.type == TokenType::LPAREN) {
            next();
            index = parseExpr();
            expect(TokenType::RPAREN);
        }
        
        // Variable or array access - type unknown until semantic analysis
        ExprPtr expr = make_unique<Expr>(ExprKind::Var, Type::Float, VarRef{name, move(index)});
        
        // Check for member access (dot operator) - Phase 6 structs & Phase 7 classes
        while (tok.type == TokenType::DOT) {
            next();
            string member = expect(TokenType::ID).val;
            
            // Phase 7: Check if it's a method call (followed by parentheses)
            if (tok.type == TokenType::LPAREN) {
                next();
                vector<ExprPtr> args;
                if (tok.type != TokenType::RPAREN) {
                    args.push_back(parseExpr());
                    while (tok.type == TokenType::COMMA) {
                        next();
                        args.push_back(parseExpr());
                    }
                }
                expect(TokenType::RPAREN);
                
                // Method call: obj.method(args)
                expr = make_unique<Expr>(ExprKind::MethodCall, Type::Float,
                                       MethodCallExpr{move(expr), member, move(args)});
            } else {
                // Property/field access: obj.field
                expr = make_unique<Expr>(ExprKind::MemberAccess, Type::Float, 
                                       MemberAccessExpr{move(expr), member});
            }
        }
        
        return expr;
    }
    
    if (tok.type == TokenType::LPAREN) {
        next();
        auto e = parseExpr();
        expect(TokenType::RPAREN);
        return e;
    }
    
    error("Unexpected token in expression: '" + tok.val + "'");
    return nullptr;
}

ExprPtr Parser::parseMul() {
    auto left = parsePrimary();
    while (tok.type == TokenType::MUL || tok.type == TokenType::DIV || tok.type == TokenType::MOD) {
        Op op = (tok.type == TokenType::MUL) ? Op::Mul : (tok.type == TokenType::DIV ? Op::Div : Op::Mod);
        next();
        auto right = parsePrimary();
        // Type unknown - semantic analysis will determine
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Float, BinOp{op, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

// Phase 9: Parse shift operators (<< >>)
ExprPtr Parser::parseShift() {
    auto left = parseMul();
    while (tok.type == TokenType::SHL || tok.type == TokenType::SHR) {
        Op op = (tok.type == TokenType::SHL) ? Op::Shl : Op::Shr;
        next();
        auto right = parseMul();
        // Shift operations work on integers
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Int, BinOp{op, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

ExprPtr Parser::parseAdd() {
    auto left = parseShift();  // Shifts bind tighter than addition
    while (tok.type == TokenType::PLUS || tok.type == TokenType::MINUS) {
        Op op = (tok.type == TokenType::PLUS) ? Op::Add : Op::Sub;
        next();
        auto right = parseShift();
        // Type unknown - semantic analysis will determine
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Float, BinOp{op, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

// Phase 9: Parse bitwise AND
ExprPtr Parser::parseBitAnd() {
    auto left = parseAdd();
    while (tok.type == TokenType::BITAND) {
        next();
        auto right = parseAdd();
        // Bitwise operations work on integers
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Int, BinOp{Op::BitAnd, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

// Phase 9: Parse bitwise XOR
ExprPtr Parser::parseBitXor() {
    auto left = parseBitAnd();
    while (tok.type == TokenType::BITXOR) {
        next();
        auto right = parseBitAnd();
        // Bitwise operations work on integers
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Int, BinOp{Op::BitXor, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

// Phase 9: Parse bitwise OR
ExprPtr Parser::parseBitOr() {
    auto left = parseBitXor();
    while (tok.type == TokenType::BITOR) {
        next();
        auto right = parseBitXor();
        // Bitwise operations work on integers
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Int, BinOp{Op::BitOr, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

ExprPtr Parser::parseEq() {
    auto left = parseBitOr();  // Bitwise operators bind tighter than comparisons
    while (tok.type == TokenType::LT || tok.type == TokenType::GT || 
           tok.type == TokenType::LE || tok.type == TokenType::GE ||
           tok.type == TokenType::EQ || tok.type == TokenType::NE) {
        Op op;
        if (tok.type == TokenType::LT) op = Op::Lt;
        else if (tok.type == TokenType::GT) op = Op::Gt;
        else if (tok.type == TokenType::LE) op = Op::Le;
        else if (tok.type == TokenType::GE) op = Op::Ge;
        else if (tok.type == TokenType::EQ) op = Op::Eq;
        else op = Op::Ne;
        
        next();
        auto right = parseBitOr();
        auto cmp = make_unique<Expr>(ExprKind::Cmp, Type::Bool, CmpOp{op, move(left), move(right)});
        left = move(cmp);
    }
    return left;
}

// Phase 8: Parse NOT (prefix operator)
ExprPtr Parser::parseNot() {
    if (tok.type == TokenType::NOT) {
        next();
        ExprPtr operand = parseNot();  // Allow chaining: NOT NOT x
        return make_unique<Expr>(ExprKind::Logical, Type::Bool,
                               LogicalExpr{LogicalOp::Not, nullptr, move(operand)});
    }
    return parseEq();  // Parse comparisons
}

// Phase 8: Parse AND
ExprPtr Parser::parseAnd() {
    ExprPtr left = parseNot();
    while (tok.type == TokenType::AND) {
        next();
        ExprPtr right = parseNot();
        left = make_unique<Expr>(ExprKind::Logical, Type::Bool,
                                LogicalExpr{LogicalOp::And, move(left), move(right)});
    }
    return left;
}

// Phase 8: Parse XOR
ExprPtr Parser::parseXor() {
    ExprPtr left = parseAnd();
    while (tok.type == TokenType::XOR) {
        next();
        ExprPtr right = parseAnd();
        left = make_unique<Expr>(ExprKind::Logical, Type::Bool,
                                LogicalExpr{LogicalOp::Xor, move(left), move(right)});
    }
    return left;
}

// Phase 8: Parse OR (lowest precedence logical operator)
ExprPtr Parser::parseOr() {
    ExprPtr left = parseXor();
    while (tok.type == TokenType::OR) {
        next();
        ExprPtr right = parseXor();
        left = make_unique<Expr>(ExprKind::Logical, Type::Bool,
                                LogicalExpr{LogicalOp::Or, move(left), move(right)});
    }
    return left;
}

ExprPtr Parser::parseExpr() {
    return parseOr();  // Start with lowest precedence (OR)
}

StmtPtr Parser::parseStmt() {
    // Phase 10: LET keyword is now optional - removed LET requirement
    // Bare assignments are handled below in the ID parsing section
    // Note: PRINT and INPUT removed - use Console.WriteLine/Console.ReadLine

    if (tok.type == TokenType::DIM) {
        next();
        string var = expect(TokenType::ID).val;
        
        // Check for array syntax: DIM var(size)
        if (tok.type == TokenType::LPAREN) {
            next();
            auto size = parseExpr();
            expect(TokenType::RPAREN);
            
            // Phase 9: Modern syntax - DIM arr(10) As Integer
            string typeNameUpper = "";
            Type arrayType = Type::FloatArray;  // Default
            if (tok.type == TokenType::AS) {
                next();
                string typeName;
                if (tok.type == TokenType::INTEGER) {
                    typeName = "INTEGER";
                    arrayType = Type::IntArray;
                    next();
                } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                    typeName = "FLOAT";
                    arrayType = Type::FloatArray;
                    next();
                } else if (tok.type == TokenType::DOUBLE) {
                    typeName = "DOUBLE";
                    arrayType = Type::DoubleArray;
                    next();
                } else if (tok.type == TokenType::LONG) {
                    typeName = "LONG";
                    arrayType = Type::IntArray;
                    next();
                } else if (tok.type == TokenType::BOOLEAN) {
                    typeName = "BOOLEAN";
                    arrayType = Type::BoolArray;
                    next();
                } else if (tok.type == TokenType::STRINGTYPE) {
                    typeName = "STRING";
                    arrayType = Type::StringArray;
                    next();
                } else if (tok.type == TokenType::DECIMAL) {
                    typeName = "DECIMAL";
                    arrayType = Type::FloatArray;  // Map to float array for now
                    next();
                } else if (tok.type == TokenType::BIGINT) {
                    typeName = "BIGINT";
                    arrayType = Type::IntArray;  // Map to int array for now
                    next();
                } else {
                    typeName = expect(TokenType::ID).val;
                }

                transform(typeName.begin(), typeName.end(), typeName.begin(), ::toupper);
                typeNameUpper = typeName;
                // Store array type for codegen
                knownTypes[var] = arrayType;
            }
            
            // Old syntax still requires = initValue
            if (tok.type == TokenType::ASSIGN) {
                next();
                auto initVal = parseExpr();
                return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, move(size), move(initVal), typeNameUpper});
            }
            
            // New syntax allows omitting initialization (defaults to 0 for numeric, "" for string)
            return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, move(size), nullptr, typeNameUpper});
        }
        
        // Check if it's "DIM var AS TypeName" or "DIM var AS NEW ClassName(args)"
        if (tok.type == TokenType::AS) {
            next();
            
            // Phase 7: Check for "AS NEW ClassName(args)" syntax
            if (tok.type == TokenType::NEW) {
                // DIM obj AS NEW ClassName(args) - creates and initializes object
                // We'll treat this as DIM followed by assignment of NEW expression
                // For now, parse the NEW expression and store it in initVal
                auto newExpr = parsePrimary();  // This will parse the NEW expression
                
                // Store in DimStmt with special handling
                knownTypes[var] = Type::UserDefined;
                return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, nullptr, move(newExpr), ""});
            } else {
                // Phase 9: Modern typed variable declaration
                string typeName;
                if (tok.type == TokenType::INTEGER) {
                    typeName = "INTEGER";
                    next();
                    knownTypes[var] = Type::Int;
                } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                    typeName = "FLOAT";
                    next();
                    knownTypes[var] = Type::Float;
                } else if (tok.type == TokenType::DOUBLE) {
                    typeName = "DOUBLE";
                    next();
                    knownTypes[var] = Type::Double;
                } else if (tok.type == TokenType::LONG) {
                    typeName = "LONG";
                    next();
                    knownTypes[var] = Type::Int;  // Map to Int for now
                } else if (tok.type == TokenType::BOOLEAN) {
                    typeName = "BOOLEAN";
                    next();
                    knownTypes[var] = Type::Bool;
                } else if (tok.type == TokenType::STRINGTYPE) {
                    typeName = "STRING";
                    next();
                    knownTypes[var] = Type::String;
                } else if (tok.type == TokenType::DECIMAL) {
                    typeName = "DECIMAL";
                    next();
                    knownTypes[var] = Type::Decimal;
                } else if (tok.type == TokenType::BIGINT) {
                    typeName = "BIGINT";
                    next();
                    knownTypes[var] = Type::BigInt;
                } else {
                    // User-defined type or class name
                    typeName = expect(TokenType::ID).val;
                    knownTypes[var] = Type::UserDefined;
                }
                
                // Normalize type name to uppercase for consistent lookup
                string typeNameUpper = typeName;
                transform(typeNameUpper.begin(), typeNameUpper.end(), typeNameUpper.begin(), ::toupper);
                
                // Phase 9: Check for initialization: Dim x As Integer = 10
                if (tok.type == TokenType::ASSIGN) {
                    next();
                    auto initVal = parseExpr();
                    return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, nullptr, move(initVal), typeNameUpper});
                }
                
                // No initialization - return DIM with just type
                return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, nullptr, nullptr, typeNameUpper});
            }
        }
        
        // Require explicit typing - no type inference
        error("Expected AS after variable name in DIM statement");
        return nullptr;
    }
    
    if (tok.type == TokenType::FOR) {
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
        
        vector<StmtPtr> body;
        while (tok.type != TokenType::NEXT && tok.type != TokenType::END) {
            body.push_back(parseStmt());
        }
        
        expect(TokenType::NEXT);
        if (tok.type == TokenType::ID) {
            next();
        }
        
        return make_unique<Stmt>(StmtKind::For, ForStmt{var, move(start), move(end), move(step), move(body)});
    }
    
    if (tok.type == TokenType::WHILE) {
        next();
        auto cond = parseExpr();
        
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
    }
    
    if (tok.type == TokenType::DO) {
        next();
        
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
    }
    
    if (tok.type == TokenType::IF) {
        next();
        auto cond = parseExpr();
        expect(TokenType::THEN);
        
        vector<StmtPtr> thenBody;
        while (tok.type != TokenType::ELSEIF && tok.type != TokenType::ELSE &&
               tok.type != TokenType::ENDIF && tok.type != TokenType::END) {
            thenBody.push_back(parseStmt());
        }
        
        vector<ElseIfClause> elseIfs;
        while (tok.type == TokenType::ELSEIF) {
            next();
            auto elseIfCond = parseExpr();
            expect(TokenType::THEN);
            vector<StmtPtr> elseIfBody;
            while (tok.type != TokenType::ELSEIF && tok.type != TokenType::ELSE &&
                   tok.type != TokenType::ENDIF && tok.type != TokenType::END) {
                elseIfBody.push_back(parseStmt());
            }
            elseIfs.push_back(ElseIfClause{move(elseIfCond), move(elseIfBody)});
        }
        
        vector<StmtPtr> elseBody;
        if (tok.type == TokenType::ELSE) {
            next();
            while (tok.type != TokenType::ENDIF && tok.type != TokenType::END) {
                elseBody.push_back(parseStmt());
            }
        }
        
        expect(TokenType::ENDIF);
        
        return make_unique<Stmt>(StmtKind::If, IfStmt{move(cond), move(thenBody), move(elseIfs), move(elseBody)});
    }
    
    if (tok.type == TokenType::RETURN) {
        next();
        if (tok.type == TokenType::END || tok.type == TokenType::ENDFUNCTION || tok.type == TokenType::ENDSUB) {
            return make_unique<Stmt>(StmtKind::Return, ReturnStmt{nullptr});
        } else {
            auto expr = parseExpr();
            return make_unique<Stmt>(StmtKind::Return, ReturnStmt{move(expr)});
        }
    }
    
    // Phase 8: EXIT FOR, EXIT WHILE, CONTINUE
    if (tok.type == TokenType::EXIT) {
        next();
        if (tok.type == TokenType::FOR) {
            next();
            return make_unique<Stmt>(StmtKind::ExitFor, ExitForStmt{});
        } else if (tok.type == TokenType::WHILE) {
            next();
            return make_unique<Stmt>(StmtKind::ExitWhile, ExitWhileStmt{});
        } else {
            error("Expected FOR or WHILE after EXIT");
            return make_unique<Stmt>(StmtKind::ExitFor, ExitForStmt{});
        }
    }
    
    if (tok.type == TokenType::CONTINUE) {
        next();
        return make_unique<Stmt>(StmtKind::Continue, ContinueStmt{});
    }
    
    if (tok.type == TokenType::CALL) {
        next();
        
        // Phase 7: CALL can now be "CALL func(args)" or "CALL obj.method(args)"
        // Parse as an expression which handles both cases
        auto expr = parseExpr();
        
        // The expression should be either Call or MethodCall
        if (expr->kind == ExprKind::Call) {
            const CallExpr& ce = get<CallExpr>(expr->data);
            // Convert to CallStmt
            // Note: We need to extract args from the CallExpr
            // For now, just store the expression and handle in codegen
            // Actually, CallStmt expects name and args separately
            // This is a limitation - let me extract them
            CallExpr& ce_mut = get<CallExpr>(expr->data);
            return make_unique<Stmt>(StmtKind::CallStmt, CallStmtNode{ce_mut.name, move(ce_mut.args)});
        } else if (expr->kind == ExprKind::MethodCall) {
            // Phase 7: Method call - extract object, method name, and args
            MethodCallExpr& mce = get<MethodCallExpr>(expr->data);
            return make_unique<Stmt>(StmtKind::MethodCallStmt, 
                                    MethodCallStmtNode{move(mce.object), mce.methodName, move(mce.args)});
        } else {
            error("CALL must be followed by a function or method call");
        }
    }
    
    // Phase 7: ME.field = value assignment
    if (tok.type == TokenType::ME) {
        next();
        if (tok.type == TokenType::DOT) {
            vector<string> memberPath;
            while (tok.type == TokenType::DOT) {
                next();
                memberPath.push_back(expect(TokenType::ID).val);
            }
            
            expect(TokenType::ASSIGN);
            auto expr = parseExpr();
            
            // Store as "ME.member" for codegen to handle
            if (memberPath.size() == 1) {
                string fullPath = "ME." + memberPath[0];
                return make_unique<Stmt>(StmtKind::Let, LetStmt{fullPath, move(expr), nullptr});
            } else {
                error("Nested member access in assignment not yet supported");
            }
        }
        error("Expected . after ME");
    }
    
    // Phase 7/9: Bare assignment (without LET) or expression statement
    if (tok.type == TokenType::ID) {
        string var = tok.val;
        string varUpper = var;
        for (auto& c : varUpper) c = toupper(c);
        
        // Phase 9: Check if it's a namespace
        bool isNamespace = (varUpper == "MATH" || varUpper == "FILE" ||
                           varUpper == "HTTP" || varUpper == "JSON" ||
                           varUpper == "XML" || varUpper == "DB" ||
                           varUpper == "PATH" || varUpper == "DIR" ||
                           varUpper == "ARGS" || varUpper == "REGEX" ||
                           varUpper == "ARRAY" || varUpper == "STR" ||
                           varUpper == "INTLIST" || varUpper == "STRINGLIST" ||
                           varUpper == "MAP" || varUpper == "STACK" ||
                           varUpper == "QUEUE" || varUpper == "CRYPTO" ||
                           varUpper == "THREAD" || varUpper == "BIGINT" ||
                           varUpper == "DECIMAL" || varUpper == "SYSTEM" ||  // Phase 12
                           varUpper == "WEBSERVER" || varUpper == "REQUEST" ||
                           varUpper == "RESPONSE");  // Phase 12: Web server namespaces

        next();

        // Check for member access: var.member or Namespace.Method
        if (tok.type == TokenType::DOT) {
            // Phase 9: If it's a namespace, parse as expression statement (not assignment)
            if (isNamespace) {
                next();  // Consume DOT
                // Accept either ID or EXIT keyword as method name (for System.exit)
                string methodName;
                if (tok.type == TokenType::ID) {
                    methodName = tok.val;
                    next();
                } else if (tok.type == TokenType::EXIT) {
                    methodName = "exit";
                    next();
                } else {
                    error("Expected identifier but got '" + tok.val + "'");
                    methodName = "error";
                }
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
                
                // Create namespace call expression and wrap in ExprStmt
                auto expr = make_unique<Expr>(ExprKind::NamespaceCall, Type::Float,
                                            NamespaceCallExpr{varUpper, methodName, move(args)});
                return make_unique<Stmt>(StmtKind::ExprStmt, ExprStmtNode{move(expr)});
            }
            
            // Not a namespace - could be method call or member assignment
            // Build the object expression first
            ExprPtr objExpr = make_unique<Expr>(ExprKind::Var, Type::Float, VarRef{var, nullptr});
            
            // Parse member access chain
            bool foundAssignment = false;
            while (tok.type == TokenType::DOT) {
                next();
                string member = expect(TokenType::ID).val;
                
                // Phase 7: Check if it's a method call (followed by parentheses)
                if (tok.type == TokenType::LPAREN) {
                    // Method call: obj.method(args) - parse as expression statement
                    next();
                    vector<ExprPtr> args;
                    if (tok.type != TokenType::RPAREN) {
                        args.push_back(parseExpr());
                        while (tok.type == TokenType::COMMA) {
                            next();
                            args.push_back(parseExpr());
                        }
                    }
                    expect(TokenType::RPAREN);
                    
                    // Create method call expression and wrap in ExprStmt
                    auto expr = make_unique<Expr>(ExprKind::MethodCall, Type::Float,
                                                 MethodCallExpr{move(objExpr), member, move(args)});
                    return make_unique<Stmt>(StmtKind::ExprStmt, ExprStmtNode{move(expr)});
                } else if (tok.type == TokenType::ASSIGN) {
                    // Member assignment: obj.field = value
                    // Build the member access expression first, then handle assignment
                    objExpr = make_unique<Expr>(ExprKind::MemberAccess, Type::Float, 
                                              MemberAccessExpr{move(objExpr), member});
                    foundAssignment = true;
                    break;  // Exit the while loop to handle assignment below
                } else {
                    // Property/field access: obj.field (continue building chain)
                    objExpr = make_unique<Expr>(ExprKind::MemberAccess, Type::Float, 
                                              MemberAccessExpr{move(objExpr), member});
                }
            }
            
            // If we get here, it's a member assignment (obj.field = value)
            if (foundAssignment) {
                next();  // Consume the ASSIGN token that we already saw
            } else {
                expect(TokenType::ASSIGN);
            }
            auto expr = parseExpr();
            
            // Handle single-level member access
            if (objExpr->kind == ExprKind::MemberAccess) {
                const MemberAccessExpr& mae = get<MemberAccessExpr>(objExpr->data);
                if (mae.object->kind == ExprKind::Var) {
                    const VarRef& vr = get<VarRef>(mae.object->data);
                    string fullPath = vr.name + "." + mae.member;
                    return make_unique<Stmt>(StmtKind::Let, LetStmt{fullPath, move(expr), nullptr});
                } else {
                    error("Complex member access in assignment not yet supported");
                }
            } else {
                error("Expected member access before assignment");
            }
        }
        
        // Check for array assignment or function call: var(index) = value or func(args)
        ExprPtr index = nullptr;
        if (tok.type == TokenType::LPAREN) {
            // Could be array assignment or function call as expression statement
            next();
            if (tok.type == TokenType::RPAREN) {
                // Empty parens - function call with no args
                next();
                // Create call expression and wrap in ExprStmt
                auto expr = make_unique<Expr>(ExprKind::Call, Type::Float, CallExpr{var, {}});
                return make_unique<Stmt>(StmtKind::ExprStmt, ExprStmtNode{move(expr)});
            }
            
            index = parseExpr();
            expect(TokenType::RPAREN);
            
            // If followed by =, it's array assignment; otherwise expression statement
            if (tok.type != TokenType::ASSIGN) {
                // It's a function call with one arg - wrap in ExprStmt
                vector<ExprPtr> args;
                args.push_back(move(index));
                auto expr = make_unique<Expr>(ExprKind::Call, Type::Float, CallExpr{var, move(args)});
                return make_unique<Stmt>(StmtKind::ExprStmt, ExprStmtNode{move(expr)});
            }
        }
        
        // Must be followed by assignment
        if (tok.type == TokenType::ASSIGN) {
            next();
            auto expr = parseExpr();
            return make_unique<Stmt>(StmtKind::Let, LetStmt{var, move(expr), move(index)});
        }
        
        error("Expected = after variable name");
    }
    
    // Phase 9: Try to parse as expression statement (e.g., Console.WriteLine(...))
    // This allows function/method calls as statements without LET
    if (tok.type == TokenType::ID || tok.type == TokenType::CONSOLE) {
        // Try to parse as expression
        auto expr = parseExpr();
        // Expression statement - the result will be discarded (pop instruction added in codegen)
        return make_unique<Stmt>(StmtKind::ExprStmt, ExprStmtNode{move(expr)});
    }
    
    error("Unexpected token in statement: '" + tok.val + "'");
    return nullptr;
}

DeclPtr Parser::parseDecl() {
    if (tok.type == TokenType::FUNCTION) {
        next();
        string name = expect(TokenType::ID).val;
        expect(TokenType::LPAREN);
        
        // Phase 9: Parse parameters with optional types
        vector<Param> params;
        if (tok.type != TokenType::RPAREN) {
            do {
                if (tok.type == TokenType::COMMA) next();
                
                string paramName = expect(TokenType::ID).val;
                Type paramType = Type::Float;  // Default
                string paramTypeName;
                
                // Phase 10: Require explicit types for all parameters
                if (tok.type == TokenType::AS) {
                    next();
                    if (tok.type == TokenType::INTEGER) {
                        paramTypeName = "INTEGER";
                        paramType = Type::Int;
                        next();
                    } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                        paramTypeName = "FLOAT";
                        paramType = Type::Float;
                        next();
                    } else if (tok.type == TokenType::DOUBLE) {
                        paramTypeName = "DOUBLE";
                        paramType = Type::Double;
                        next();
                    } else if (tok.type == TokenType::LONG) {
                        paramTypeName = "LONG";
                        paramType = Type::Int;
                        next();
                    } else if (tok.type == TokenType::BOOLEAN) {
                        paramTypeName = "BOOLEAN";
                        paramType = Type::Bool;
                        next();
                    } else if (tok.type == TokenType::STRINGTYPE) {
                        paramTypeName = "STRING";
                        paramType = Type::String;
                        next();
                    } else if (tok.type == TokenType::DECIMAL) {
                        paramTypeName = "DECIMAL";
                        paramType = Type::Decimal;
                        next();
                    } else if (tok.type == TokenType::BIGINT) {
                        paramTypeName = "BIGINT";
                        paramType = Type::BigInt;
                        next();
                    } else {
                        paramTypeName = expect(TokenType::ID).val;
                        paramType = resolveTypeName(paramTypeName);
                    }
                } else {
                    // Phase 10: Require explicit parameter types
                    error("Parameter must have explicit type (As TypeName)");
                }

                params.push_back(Param{paramName, paramType, paramTypeName});
            } while (tok.type == TokenType::COMMA);
        }
        expect(TokenType::RPAREN);

        // Phase 10: Require explicit return type for all functions
        Type returnType = Type::Float;  // Default fallback
        if (tok.type == TokenType::AS) {
            next();
            if (tok.type == TokenType::INTEGER) {
                returnType = Type::Int;
                next();
            } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                returnType = Type::Float;
                next();
            } else if (tok.type == TokenType::DOUBLE) {
                returnType = Type::Double;
                next();
            } else if (tok.type == TokenType::LONG) {
                returnType = Type::Int;
                next();
            } else if (tok.type == TokenType::BOOLEAN) {
                returnType = Type::Bool;
                next();
            } else if (tok.type == TokenType::STRINGTYPE) {
                returnType = Type::String;
                next();
            } else if (tok.type == TokenType::DECIMAL) {
                returnType = Type::Decimal;
                next();
            } else if (tok.type == TokenType::BIGINT) {
                returnType = Type::BigInt;
                next();
            } else {
                string returnTypeName = expect(TokenType::ID).val;
                returnType = resolveTypeName(returnTypeName);
            }
        } else {
            // Phase 10: Require explicit return type
            error("Function must have explicit return type (As TypeName)");
        }
        
        vector<StmtPtr> body;
        while (tok.type != TokenType::ENDFUNCTION && tok.type != TokenType::END) {
            body.push_back(parseStmt());
        }
        
        if (tok.type == TokenType::ENDFUNCTION) {
            next();
        } else {
            error("Expected ENDFUNCTION or END FUNCTION");
        }
        
        return make_unique<Decl>(DeclKind::Function, FunctionDecl{name, params, returnType, move(body)});
    }
    
    if (tok.type == TokenType::SUB) {
        next();
        string name = expect(TokenType::ID).val;
        expect(TokenType::LPAREN);
        
        // Phase 9: Parse parameters with optional types
        vector<Param> params;
        if (tok.type != TokenType::RPAREN) {
            do {
                if (tok.type == TokenType::COMMA) next();
                
                string paramName = expect(TokenType::ID).val;
                Type paramType = Type::Float;  // Default
                string paramTypeName;
                
                // Phase 10: Require explicit types for all parameters
                if (tok.type == TokenType::AS) {
                    next();
                    if (tok.type == TokenType::INTEGER) {
                        paramTypeName = "INTEGER";
                        paramType = Type::Int;
                        next();
                    } else if (tok.type == TokenType::SINGLE || tok.type == TokenType::FLOAT) {
                        paramTypeName = "FLOAT";
                        paramType = Type::Float;
                        next();
                    } else if (tok.type == TokenType::DOUBLE) {
                        paramTypeName = "DOUBLE";
                        paramType = Type::Double;
                        next();
                    } else if (tok.type == TokenType::LONG) {
                        paramTypeName = "LONG";
                        paramType = Type::Int;
                        next();
                    } else if (tok.type == TokenType::BOOLEAN) {
                        paramTypeName = "BOOLEAN";
                        paramType = Type::Bool;
                        next();
                    } else if (tok.type == TokenType::STRINGTYPE) {
                        paramTypeName = "STRING";
                        paramType = Type::String;
                        next();
                    } else if (tok.type == TokenType::DECIMAL) {
                        paramTypeName = "DECIMAL";
                        paramType = Type::Decimal;
                        next();
                    } else if (tok.type == TokenType::BIGINT) {
                        paramTypeName = "BIGINT";
                        paramType = Type::BigInt;
                        next();
                    } else {
                        paramTypeName = expect(TokenType::ID).val;
                        paramType = resolveTypeName(paramTypeName);
                    }
                } else {
                    // Phase 10: Require explicit parameter types
                    error("Parameter must have explicit type (As TypeName)");
                }

                params.push_back(Param{paramName, paramType, paramTypeName});
            } while (tok.type == TokenType::COMMA);
        }
        expect(TokenType::RPAREN);
        
        vector<StmtPtr> body;
        while (tok.type != TokenType::ENDSUB && tok.type != TokenType::END) {
            body.push_back(parseStmt());
        }
        
        if (tok.type == TokenType::ENDSUB) {
            next();
        } else {
            error("Expected ENDSUB or END SUB");
        }
        
        return make_unique<Decl>(DeclKind::Sub, SubDecl{name, params, move(body)});
    }
    
    error("Expected FUNCTION or SUB");
    return nullptr;
}

Program Parser::parse() {
    Program prog;
    
    // Phase 6: First, parse all TYPE declarations (user-defined types)
    while (tok.type == TokenType::TYPE) {
        auto typeDecl = parseTypeDecl();
        // Register the type for later reference (name is already uppercase from parseTypeDecl)
        const TypeDefDecl& td = get<TypeDefDecl>(typeDecl->data);
        userTypes[td.name] = td;  // td.name is uppercase
        prog.declarations.push_back(move(typeDecl));
    }
    
    // Phase 7: Parse all CLASS declarations
    while (tok.type == TokenType::CLASS) {
        auto classDecl = parseClassDecl();
        // Register the class name for type resolution (can't copy ClassDecl due to unique_ptr)
        const ClassDecl& cd = get<ClassDecl>(classDecl->data);
        userClassNames.insert(cd.name);  // cd.name is uppercase
        prog.declarations.push_back(move(classDecl));
    }
    
    // Parse all function/sub declarations
    while (tok.type == TokenType::FUNCTION || tok.type == TokenType::SUB) {
        prog.declarations.push_back(parseDecl());
    }
    
    // Parse main program statements (and any TYPE/CLASS/FUNCTION/SUB declarations mixed in)
    while (tok.type != TokenType::END) {
        // Allow TYPE, CLASS, FUNCTION, SUB declarations to appear anywhere
        if (tok.type == TokenType::TYPE) {
            auto typeDecl = parseTypeDecl();
            const TypeDefDecl& td = get<TypeDefDecl>(typeDecl->data);
            userTypes[td.name] = td;
            prog.declarations.push_back(move(typeDecl));
        } else if (tok.type == TokenType::CLASS) {
            auto classDecl = parseClassDecl();
            const ClassDecl& cd = get<ClassDecl>(classDecl->data);
            userClassNames.insert(cd.name);
            prog.declarations.push_back(move(classDecl));
        } else if (tok.type == TokenType::FUNCTION || tok.type == TokenType::SUB) {
            prog.declarations.push_back(parseDecl());
        } else {
            prog.statements.push_back(parseStmt());
        }
    }
    
    return prog;
}

