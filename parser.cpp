#include "parser.h"
#include "builtin_functions.h"
#include <cctype>

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

ExprPtr Parser::parsePrimary() {
    // Handle unary minus
    if (tok.type == TokenType::MINUS) {
        next();
        auto operand = parsePrimary();
        return make_unique<Expr>(ExprKind::Unary, operand->type, UnaryExpr{UnaryOp::Neg, move(operand)});
    }
    
    if (tok.type == TokenType::NUMBER) {
        Token nt = expect(TokenType::NUMBER);
        bool isFloat = nt.val.find('.') != string::npos;
        Type ty = isFloat ? Type::Float : Type::Int;
        return make_unique<Expr>(ExprKind::Num, ty, NumLit{nt.num});
    }
    
    if (tok.type == TokenType::STRING) {
        Token st = expect(TokenType::STRING);
        return make_unique<Expr>(ExprKind::Str, Type::String, StrLit{st.val});
    }
    
    if (tok.type == TokenType::TRUE) {
        next();
        return make_unique<Expr>(ExprKind::BoolLit, Type::Bool, BoolLit{true});
    }
    
    if (tok.type == TokenType::FALSE) {
        next();
        return make_unique<Expr>(ExprKind::BoolLit, Type::Bool, BoolLit{false});
    }
    
    if (tok.type == TokenType::ID) {
        string name = tok.val;
        string nameUpper = name;
        for (auto& c : nameUpper) c = toupper(c);
        next();
        
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
        return make_unique<Expr>(ExprKind::Var, Type::Float, VarRef{name, move(index)});
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

ExprPtr Parser::parseAdd() {
    auto left = parseMul();
    while (tok.type == TokenType::PLUS || tok.type == TokenType::MINUS) {
        Op op = (tok.type == TokenType::PLUS) ? Op::Add : Op::Sub;
        next();
        auto right = parseMul();
        // Type unknown - semantic analysis will determine
        auto bin = make_unique<Expr>(ExprKind::Bin, Type::Float, BinOp{op, move(left), move(right)});
        left = move(bin);
    }
    return left;
}

ExprPtr Parser::parseEq() {
    auto left = parseAdd();
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
        auto right = parseAdd();
        auto cmp = make_unique<Expr>(ExprKind::Cmp, Type::Bool, CmpOp{op, move(left), move(right)});
        left = move(cmp);
    }
    return left;
}

ExprPtr Parser::parseExpr() {
    return parseEq();
}

StmtPtr Parser::parseStmt() {
    if (tok.type == TokenType::PRINT) {
        next();
        vector<ExprPtr> exprs;
        vector<PrintSep> seps;
        bool addNewline = true;
        
        // Parse expressions with separators (check we're not at a statement keyword or end)
        bool shouldParse = (tok.type != TokenType::END && 
                           tok.type != TokenType::PRINT && 
                           tok.type != TokenType::LET && 
                           tok.type != TokenType::INPUT &&
                           tok.type != TokenType::DIM && 
                           tok.type != TokenType::IF &&
                           tok.type != TokenType::FOR && 
                           tok.type != TokenType::WHILE && 
                           tok.type != TokenType::DO &&
                           tok.type != TokenType::NEXT && 
                           tok.type != TokenType::ENDWHILE && 
                           tok.type != TokenType::WEND &&
                           tok.type != TokenType::ENDIF && 
                           tok.type != TokenType::ELSEIF && 
                           tok.type != TokenType::ELSE &&
                           tok.type != TokenType::RETURN && 
                           tok.type != TokenType::CALL &&
                           tok.type != TokenType::ENDFUNCTION && 
                           tok.type != TokenType::ENDSUB);
        
        if (shouldParse) {
            
            exprs.push_back(parseExpr());
            
            while (tok.type == TokenType::COMMA || tok.type == TokenType::SEMI) {
                PrintSep sep = (tok.type == TokenType::COMMA) ? PrintSep::Comma : PrintSep::Semi;
                seps.push_back(sep);
                next();
                
                // Check for trailing separator
                if (tok.type == TokenType::END || tok.type == TokenType::PRINT ||
                    tok.type == TokenType::LET || tok.type == TokenType::INPUT ||
                    tok.type == TokenType::DIM || tok.type == TokenType::IF ||
                    tok.type == TokenType::FOR || tok.type == TokenType::WHILE || tok.type == TokenType::DO ||
                    tok.type == TokenType::NEXT || tok.type == TokenType::ENDWHILE || tok.type == TokenType::WEND ||
                    tok.type == TokenType::ENDIF || tok.type == TokenType::ELSEIF || tok.type == TokenType::ELSE ||
                    tok.type == TokenType::RETURN || tok.type == TokenType::CALL ||
                    tok.type == TokenType::ENDFUNCTION || tok.type == TokenType::ENDSUB) {
                    addNewline = false;
                    break;
                }
                
                exprs.push_back(parseExpr());
            }
        }
        
        return make_unique<Stmt>(StmtKind::Print, PrintStmt{move(exprs), move(seps), addNewline});
    }
    
    if (tok.type == TokenType::LET) {
        next();
        string var = expect(TokenType::ID).val;
        
        ExprPtr index = nullptr;
        if (tok.type == TokenType::LPAREN) {
            next();
            index = parseExpr();
            expect(TokenType::RPAREN);
        }
        
        expect(TokenType::ASSIGN);
        auto expr = parseExpr();
        
        return make_unique<Stmt>(StmtKind::Let, LetStmt{var, move(expr), move(index)});
    }
    
    if (tok.type == TokenType::INPUT) {
        next();
        string var = expect(TokenType::ID).val;
        ExprPtr index = nullptr;
        if (tok.type == TokenType::LPAREN) {
            next();
            index = parseExpr();
            expect(TokenType::RPAREN);
        }
        return make_unique<Stmt>(StmtKind::Input, InputStmt{var, move(index)});
    }
    
    if (tok.type == TokenType::DIM) {
        next();
        string var = expect(TokenType::ID).val;
        expect(TokenType::LPAREN);
        auto size = parseExpr();
        expect(TokenType::RPAREN);
        expect(TokenType::ASSIGN);
        auto initVal = parseExpr();
        
        return make_unique<Stmt>(StmtKind::Dim, DimStmt{var, move(size), move(initVal)});
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
    
    if (tok.type == TokenType::CALL) {
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
        
        return make_unique<Stmt>(StmtKind::CallStmt, CallStmtNode{name, move(args)});
    }
    
    error("Unexpected token in statement: '" + tok.val + "'");
    return nullptr;
}

DeclPtr Parser::parseDecl() {
    if (tok.type == TokenType::FUNCTION) {
        next();
        string name = expect(TokenType::ID).val;
        expect(TokenType::LPAREN);
        
        vector<Param> params;
        if (tok.type != TokenType::RPAREN) {
            string paramName = expect(TokenType::ID).val;
            params.push_back(Param{paramName, Type::Float});  // Type will be inferred
            while (tok.type == TokenType::COMMA) {
                next();
                paramName = expect(TokenType::ID).val;
                params.push_back(Param{paramName, Type::Float});
            }
        }
        expect(TokenType::RPAREN);
        
        vector<StmtPtr> body;
        while (tok.type != TokenType::ENDFUNCTION && tok.type != TokenType::END) {
            body.push_back(parseStmt());
        }
        expect(TokenType::ENDFUNCTION);
        
        // Return type will be inferred from RETURN statements
        return make_unique<Decl>(DeclKind::Function, FunctionDecl{name, params, Type::Float, move(body)});
    }
    
    if (tok.type == TokenType::SUB) {
        next();
        string name = expect(TokenType::ID).val;
        expect(TokenType::LPAREN);
        
        vector<Param> params;
        if (tok.type != TokenType::RPAREN) {
            string paramName = expect(TokenType::ID).val;
            params.push_back(Param{paramName, Type::Float});  // Type will be inferred
            while (tok.type == TokenType::COMMA) {
                next();
                paramName = expect(TokenType::ID).val;
                params.push_back(Param{paramName, Type::Float});
            }
        }
        expect(TokenType::RPAREN);
        
        vector<StmtPtr> body;
        while (tok.type != TokenType::ENDSUB && tok.type != TokenType::END) {
            body.push_back(parseStmt());
        }
        expect(TokenType::ENDSUB);
        
        return make_unique<Decl>(DeclKind::Sub, SubDecl{name, params, move(body)});
    }
    
    error("Expected FUNCTION or SUB");
    return nullptr;
}

Program Parser::parse() {
    Program prog;
    
    // Parse all function/sub declarations
    while (tok.type == TokenType::FUNCTION || tok.type == TokenType::SUB) {
        prog.declarations.push_back(parseDecl());
    }
    
    // Parse main program statements
    while (tok.type != TokenType::END) {
        prog.statements.push_back(parseStmt());
    }
    
    return prog;
}

