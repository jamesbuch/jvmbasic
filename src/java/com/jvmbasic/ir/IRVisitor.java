package com.jvmbasic.ir;

import com.jvmbasic.ir.decl.*;
import com.jvmbasic.ir.expr.*;
import com.jvmbasic.ir.stmt.*;

/**
 * Visitor interface for IR nodes.
 *
 * @param <T> Return type of visitor methods
 */
public interface IRVisitor<T> {
    // Compilation unit
    T visitCompilationUnit(IRCompilationUnit unit);

    // Declarations
    T visitFunction(IRFunction func);
    T visitClass(IRClass cls);
    T visitParameter(IRParameter param);
    T visitVariable(IRVariable var);

    // Statements
    T visitVarDecl(IRVarDecl stmt);
    T visitAssignment(IRAssignment stmt);
    T visitIf(IRIf stmt);
    T visitWhile(IRWhile stmt);
    T visitFor(IRFor stmt);
    T visitForEach(IRForEach stmt);
    T visitReturn(IRReturn stmt);
    T visitExprStmt(IRExprStmt stmt);
    T visitBlock(IRBlock block);
    T visitTry(IRTry stmt);
    T visitThrow(IRThrow stmt);

    // Expressions
    T visitIntLiteral(IRIntLiteral expr);
    T visitLongLiteral(IRLongLiteral expr);
    T visitFloatLiteral(IRFloatLiteral expr);
    T visitDoubleLiteral(IRDoubleLiteral expr);
    T visitStringLiteral(IRStringLiteral expr);
    T visitBoolLiteral(IRBoolLiteral expr);
    T visitNullLiteral(IRNullLiteral expr);
    T visitIdentifier(IRIdentifier expr);
    T visitBinaryOp(IRBinaryOp expr);
    T visitUnaryOp(IRUnaryOp expr);
    T visitCall(IRCall expr);
    T visitMethodCall(IRMethodCall expr);
    T visitMemberAccess(IRMemberAccess expr);
    T visitArrayAccess(IRArrayAccess expr);
    T visitNewObject(IRNewObject expr);
    T visitNewArray(IRNewArray expr);
    T visitCast(IRCast expr);
    T visitTernary(IRTernary expr);
}
