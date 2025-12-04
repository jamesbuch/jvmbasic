#include "ast_printer.h"

void ASTPrinter::printIndent() {
    for (int i = 0; i < indent; ++i) {
        out << "  ";
    }
}

void ASTPrinter::printExpr(const Expr& expr) {
    out << "[" << typeToString(expr.type) << "] ";
    
    switch(expr.kind) {
        case ExprKind::Num: {
            const NumLit& nl = get<NumLit>(expr.data);
            out << nl.value;
            break;
        }
        
        case ExprKind::Str: {
            const StrLit& sl = get<StrLit>(expr.data);
            out << "\"" << sl.value << "\"";
            break;
        }
        
        case ExprKind::BoolLit: {
            const BoolLit& bl = get<BoolLit>(expr.data);
            out << (bl.value ? "true" : "false");
            break;
        }
        
        case ExprKind::Var: {
            const VarRef& vr = get<VarRef>(expr.data);
            out << vr.name;
            if (vr.index) {
                out << "(";
                printExpr(*vr.index);
                out << ")";
            }
            break;
        }
        
        case ExprKind::StringConcat: {
            const StringConcatExpr& sc = get<StringConcatExpr>(expr.data);
            out << "(";
            bool first = true;
            for (const auto& op : sc.operands) {
                if (!first) out << " + ";
                printExpr(*op);
                first = false;
            }
            out << ")";
            break;
        }
        case ExprKind::Bin: {
            const BinOp& bo = get<BinOp>(expr.data);
            out << "(";
            printExpr(*bo.left);
            out << " " << opToString(bo.op) << " ";
            printExpr(*bo.right);
            out << ")";
            break;
        }
        
        case ExprKind::Cmp: {
            const CmpOp& co = get<CmpOp>(expr.data);
            out << "(";
            printExpr(*co.left);
            out << " " << opToString(co.op) << " ";
            printExpr(*co.right);
            out << ")";
            break;
        }
        
        case ExprKind::Unary: {
            const UnaryExpr& ue = get<UnaryExpr>(expr.data);
            out << "(-";
            printExpr(*ue.operand);
            out << ")";
            break;
        }
        
        case ExprKind::Call: {
            const CallExpr& ce = get<CallExpr>(expr.data);
            out << ce.name << "(";
            for (size_t i = 0; i < ce.args.size(); ++i) {
                if (i > 0) out << ", ";
                printExpr(*ce.args[i]);
            }
            out << ")";
            break;
        }
        
        case ExprKind::MemberAccess: {
            const MemberAccessExpr& mae = get<MemberAccessExpr>(expr.data);
            printExpr(*mae.object);
            out << "." << mae.member;
            break;
        }
        
        // Phase 7: OOP expressions
        case ExprKind::NewExpr: {
            const NewExpr& ne = get<NewExpr>(expr.data);
            out << "NEW " << ne.className << "(";
            for (size_t i = 0; i < ne.args.size(); ++i) {
                if (i > 0) out << ", ";
                printExpr(*ne.args[i]);
            }
            out << ")";
            break;
        }
        
        case ExprKind::MethodCall: {
            const MethodCallExpr& mce = get<MethodCallExpr>(expr.data);
            printExpr(*mce.object);
            out << "." << mce.methodName << "(";
            for (size_t i = 0; i < mce.args.size(); ++i) {
                if (i > 0) out << ", ";
                printExpr(*mce.args[i]);
            }
            out << ")";
            break;
        }
        
        case ExprKind::Me: {
            out << "ME";
            break;
        }
        
        // Phase 8: Logical expressions
        case ExprKind::Logical: {
            const LogicalExpr& le = get<LogicalExpr>(expr.data);
            if (le.op == LogicalOp::Not) {
                out << "(NOT ";
                printExpr(*le.right);
                out << ")";
            } else {
                out << "(";
                printExpr(*le.left);
                if (le.op == LogicalOp::And) out << " AND ";
                else if (le.op == LogicalOp::Or) out << " OR ";
                else if (le.op == LogicalOp::Xor) out << " XOR ";
                printExpr(*le.right);
                out << ")";
            }
            break;
        }
        
        // Phase 9: Namespace call expressions
        case ExprKind::NamespaceCall: {
            const NamespaceCallExpr& nce = get<NamespaceCallExpr>(expr.data);
            out << nce.namespaceName << "." << nce.methodName << "(";
            for (size_t i = 0; i < nce.args.size(); ++i) {
                if (i > 0) out << ", ";
                printExpr(*nce.args[i]);
            }
            out << ")";
            break;
        }
    }
}

void ASTPrinter::printStmt(const Stmt& stmt) {
    printIndent();
    
    switch(stmt.kind) {
        case StmtKind::Print: {
            // DEPRECATED: Use Console.WriteLine() instead
            const PrintStmt& ps = get<PrintStmt>(stmt.data);
            out << "PRINT [DEPRECATED]";
            for (size_t i = 0; i < ps.exprs.size(); ++i) {
                if (i > 0) {
                    out << (ps.seps[i-1] == PrintSep::Comma ? ", " : "; ");
                } else {
                    out << " ";
                }
                printExpr(*ps.exprs[i]);
            }
            if (!ps.addNewline) out << " [no newline]";
            out << "\n";
            break;
        }

        case StmtKind::Let: {
            // Assignment statement (LET keyword removed in Phase 10)
            const LetStmt& ls = get<LetStmt>(stmt.data);
            out << ls.var;
            if (ls.index) {
                out << "(";
                printExpr(*ls.index);
                out << ")";
            }
            out << " = ";
            printExpr(*ls.expr);
            out << "\n";
            break;
        }
        
        case StmtKind::Input: {
            // DEPRECATED: Use Console.ReadLine() instead
            const InputStmt& is = get<InputStmt>(stmt.data);
            out << "INPUT [DEPRECATED] " << is.var;
            if (is.index) {
                out << "(";
                printExpr(*is.index);
                out << ")";
            }
            out << "\n";
            break;
        }
        
        case StmtKind::Dim: {
            const DimStmt& ds = get<DimStmt>(stmt.data);
            out << "DIM " << ds.var;
            if (ds.size) {
                // Array declaration: DIM arr(size) AS Type
                out << "(";
                printExpr(*ds.size);
                out << ")";
            }
            if (!ds.typeName.empty()) {
                out << " AS " << ds.typeName;
            }
            if (ds.initVal) {
                out << " = ";
                printExpr(*ds.initVal);
            }
            out << "\n";
            break;
        }
        
        case StmtKind::If: {
            const IfStmt& ifs = get<IfStmt>(stmt.data);
            out << "IF ";
            printExpr(*ifs.cond);
            out << " THEN\n";
            indent++;
            for (const auto& s : ifs.thenBody) {
                printStmt(*s);
            }
            indent--;
            
            for (const auto& elseIf : ifs.elseIfs) {
                printIndent();
                out << "ELSEIF ";
                printExpr(*elseIf.cond);
                out << " THEN\n";
                indent++;
                for (const auto& s : elseIf.body) {
                    printStmt(*s);
                }
                indent--;
            }
            
            if (!ifs.elseBody.empty()) {
                printIndent();
                out << "ELSE\n";
                indent++;
                for (const auto& s : ifs.elseBody) {
                    printStmt(*s);
                }
                indent--;
            }
            
            printIndent();
            out << "ENDIF\n";
            break;
        }
        
        case StmtKind::For: {
            const ForStmt& fs = get<ForStmt>(stmt.data);
            out << "FOR " << fs.var << " = ";
            printExpr(*fs.start);
            out << " TO ";
            printExpr(*fs.end);
            if (fs.step) {
                out << " STEP ";
                printExpr(*fs.step);
            }
            out << "\n";
            indent++;
            for (const auto& s : fs.body) {
                printStmt(*s);
            }
            indent--;
            printIndent();
            out << "NEXT\n";
            break;
        }
        
        case StmtKind::While: {
            const WhileStmt& ws = get<WhileStmt>(stmt.data);
            out << "WHILE ";
            printExpr(*ws.cond);
            out << "\n";
            indent++;
            for (const auto& s : ws.body) {
                printStmt(*s);
            }
            indent--;
            printIndent();
            out << "ENDWHILE\n";
            break;
        }
        
        case StmtKind::DoWhile: {
            const DoWhileStmt& dws = get<DoWhileStmt>(stmt.data);
            out << "DO\n";
            indent++;
            for (const auto& s : dws.body) {
                printStmt(*s);
            }
            indent--;
            printIndent();
            out << (dws.isUntil ? "UNTIL " : "WHILE ");
            printExpr(*dws.cond);
            out << "\n";
            break;
        }
        
        case StmtKind::Return: {
            const ReturnStmt& rs = get<ReturnStmt>(stmt.data);
            out << "RETURN";
            if (rs.expr) {
                out << " ";
                printExpr(*rs.expr);
            }
            out << "\n";
            break;
        }
        
        case StmtKind::CallStmt: {
            const CallStmtNode& cs = get<CallStmtNode>(stmt.data);
            out << "CALL " << cs.name << "(";
            for (size_t i = 0; i < cs.args.size(); ++i) {
                if (i > 0) out << ", ";
                printExpr(*cs.args[i]);
            }
            out << ")\n";
            break;
        }
        
        // Phase 7: Method call statement
        case StmtKind::MethodCallStmt: {
            const MethodCallStmtNode& mcs = get<MethodCallStmtNode>(stmt.data);
            out << "CALL ";
            printExpr(*mcs.object);
            out << "." << mcs.methodName << "(";
            for (size_t i = 0; i < mcs.args.size(); ++i) {
                if (i > 0) out << ", ";
                printExpr(*mcs.args[i]);
            }
            out << ")\n";
            break;
        }
        
        // Phase 8: Control flow statements
        case StmtKind::ExitFor:
            out << "EXIT FOR\n";
            break;
            
        case StmtKind::ExitWhile:
            out << "EXIT WHILE\n";
            break;
            
        case StmtKind::Continue:
            out << "CONTINUE\n";
            break;
        
        // Phase 9: Expression statement
        case StmtKind::ExprStmt: {
            const ExprStmtNode& es = get<ExprStmtNode>(stmt.data);
            printExpr(*es.expr);
            out << "  ' (result discarded)\n";
            break;
        }
    }
}

void ASTPrinter::printDecl(const Decl& decl) {
    if (decl.kind == DeclKind::TypeDef) {
        const TypeDefDecl& td = get<TypeDefDecl>(decl.data);
        out << "TYPE " << td.name << "\n";
        indent++;
        for (const Field& field : td.fields) {
            printIndent();
            out << field.name << " AS " << typeToString(field.type);
            if (!field.typeName.empty()) {
                out << " (" << field.typeName << ")";
            }
            out << "\n";
        }
        indent--;
        out << "ENDTYPE\n\n";
    } else if (decl.kind == DeclKind::Function) {
        const FunctionDecl& fd = get<FunctionDecl>(decl.data);
        out << "FUNCTION " << fd.name << "(";
        for (size_t i = 0; i < fd.params.size(); ++i) {
            if (i > 0) out << ", ";
            out << fd.params[i].name << ":" << typeToString(fd.params[i].type);
        }
        out << ") -> " << typeToString(fd.returnType) << "\n";
        indent++;
        for (const auto& s : fd.body) {
            printStmt(*s);
        }
        indent--;
        out << "ENDFUNCTION\n\n";
    } else if (decl.kind == DeclKind::Sub) {
        const SubDecl& sd = get<SubDecl>(decl.data);
        out << "SUB " << sd.name << "(";
        for (size_t i = 0; i < sd.params.size(); ++i) {
            if (i > 0) out << ", ";
            out << sd.params[i].name << ":" << typeToString(sd.params[i].type);
        }
        out << ")\n";
        indent++;
        for (const auto& s : sd.body) {
            printStmt(*s);
        }
        indent--;
        out << "ENDSUB\n\n";
    } else if (decl.kind == DeclKind::Class) {
        // Phase 7: CLASS declarations
        const ClassDecl& cd = get<ClassDecl>(decl.data);
        out << "CLASS " << cd.name << "\n";
        indent++;
        
        // Print fields
        for (const Field& field : cd.fields) {
            printIndent();
            out << (field.isPublic ? "PUBLIC " : "PRIVATE ");
            out << field.name << " AS " << typeToString(field.type);
            if (!field.typeName.empty()) {
                out << " (" << field.typeName << ")";
            }
            out << "\n";
        }
        
        // Print methods
        for (const MethodDecl& method : cd.methods) {
            out << "\n";
            printIndent();
            out << (method.isPublic ? "PUBLIC " : "PRIVATE ");
            out << (method.isConstructor ? "SUB New(" : 
                    (method.isSub ? "SUB " : "FUNCTION ") + method.name + "(");
            for (size_t i = 0; i < method.params.size(); ++i) {
                if (i > 0) out << ", ";
                out << method.params[i].name << ":" << typeToString(method.params[i].type);
            }
            out << ")";
            if (!method.isConstructor && method.returnType != Type::Float) {
                out << " -> " << typeToString(method.returnType);
            }
            out << "\n";
            indent++;
            for (const auto& s : method.body) {
                printStmt(*s);
            }
            indent--;
            printIndent();
            out << (method.isConstructor || method.isSub ? "END SUB" : "END FUNCTION") << "\n";
        }
        
        indent--;
        out << "END CLASS\n\n";
    }
}

void ASTPrinter::print(const Program& program) {
    out << "=== AST Dump ===\n\n";
    
    if (!program.declarations.empty()) {
        out << "--- Declarations ---\n";
        for (const auto& decl : program.declarations) {
            printDecl(*decl);
        }
    }
    
    if (!program.statements.empty()) {
        out << "--- Main Program ---\n";
        for (const auto& stmt : program.statements) {
            printStmt(*stmt);
        }
    }
    
    out << "\n=== End AST ===\n";
}

