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
    }
}

void ASTPrinter::printStmt(const Stmt& stmt) {
    printIndent();
    
    switch(stmt.kind) {
        case StmtKind::Print: {
            const PrintStmt& ps = get<PrintStmt>(stmt.data);
            out << "PRINT";
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
            const LetStmt& ls = get<LetStmt>(stmt.data);
            out << "LET " << ls.var;
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
            const InputStmt& is = get<InputStmt>(stmt.data);
            out << "INPUT " << is.var;
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
            out << "DIM " << ds.var << "(";
            printExpr(*ds.size);
            out << ") = ";
            printExpr(*ds.initVal);
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
    }
}

void ASTPrinter::printDecl(const Decl& decl) {
    if (decl.kind == DeclKind::Function) {
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

