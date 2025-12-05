package com.jvmbasic.visitor;

import com.jvmbasic.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Debug Listener - First pass for debugging and tracing
 *
 * This listener is used during the first pass to:
 * - Print trace information about parsing
 * - Log entry/exit of rules
 * - Help debug grammar issues
 *
 * Usage:
 *   DebugListener listener = new DebugListener(parser);
 *   ParseTreeWalker.DEFAULT.walk(listener, tree);
 */
public class DebugListener extends JvmBasicParserBaseListener {

    private final JvmBasicParser parser;
    private int indentLevel = 0;

    public DebugListener(JvmBasicParser parser) {
        this.parser = parser;
    }

    private String indent() {
        return "  ".repeat(indentLevel);
    }

    private void trace(String message) {
        System.out.println(indent() + message);
    }

    // ========================================================================
    // Program Structure
    // ========================================================================

    @Override
    public void enterCompilationUnit(JvmBasicParser.CompilationUnitContext ctx) {
        trace(">>> Compilation Unit");
        indentLevel++;
    }

    @Override
    public void exitCompilationUnit(JvmBasicParser.CompilationUnitContext ctx) {
        indentLevel--;
        trace("<<< Compilation Unit");
    }

    // ========================================================================
    // Declarations
    // ========================================================================

    @Override
    public void enterFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String returnType = ctx.typeName().getText();
        int line = ctx.getStart().getLine();
        trace(">>> Function: " + name + " As " + returnType + " [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
        indentLevel--;
        trace("<<< Function: " + ctx.IDENTIFIER().getText());
    }

    @Override
    public void enterSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        trace(">>> Sub: " + name + " [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        indentLevel--;
        trace("<<< Sub: " + ctx.IDENTIFIER().getText());
    }

    @Override
    public void enterClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        trace(">>> Class: " + name + " [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        indentLevel--;
        trace("<<< Class: " + ctx.IDENTIFIER().getText());
    }

    // ========================================================================
    // Statements
    // ========================================================================

    @Override
    public void enterVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();
        int line = ctx.getStart().getLine();
        boolean hasInit = ctx.expression() != null;
        trace("var " + name + " as " + type + (hasInit ? " = ..." : "") + " [line " + line + "]");
    }

    @Override
    public void enterIfStatement(JvmBasicParser.IfStatementContext ctx) {
        int line = ctx.getStart().getLine();
        trace(">>> If [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitIfStatement(JvmBasicParser.IfStatementContext ctx) {
        indentLevel--;
        trace("<<< End If");
    }

    @Override
    public void enterWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        int line = ctx.getStart().getLine();
        trace(">>> While [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        indentLevel--;
        trace("<<< End While");
    }

    @Override
    public void enterForStatement(JvmBasicParser.ForStatementContext ctx) {
        String var = ctx.IDENTIFIER(0).getText();
        int line = ctx.getStart().getLine();
        trace(">>> For " + var + " [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitForStatement(JvmBasicParser.ForStatementContext ctx) {
        indentLevel--;
        trace("<<< Next");
    }

    @Override
    public void enterForEachStatement(JvmBasicParser.ForEachStatementContext ctx) {
        String var = ctx.IDENTIFIER(0).getText();
        int line = ctx.getStart().getLine();
        trace(">>> For Each " + var + " [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitForEachStatement(JvmBasicParser.ForEachStatementContext ctx) {
        indentLevel--;
        trace("<<< Next");
    }

    @Override
    public void enterTryStatement(JvmBasicParser.TryStatementContext ctx) {
        int line = ctx.getStart().getLine();
        trace(">>> Try [line " + line + "]");
        indentLevel++;
    }

    @Override
    public void exitTryStatement(JvmBasicParser.TryStatementContext ctx) {
        indentLevel--;
        trace("<<< End Try");
    }

    @Override
    public void enterReturnStatement(JvmBasicParser.ReturnStatementContext ctx) {
        int line = ctx.getStart().getLine();
        boolean hasValue = ctx.expression() != null;
        trace("Return" + (hasValue ? " ..." : "") + " [line " + line + "]");
    }

    @Override
    public void enterAssignmentStatement(JvmBasicParser.AssignmentStatementContext ctx) {
        int line = ctx.getStart().getLine();
        String lhs = ctx.lvalue().getText();
        trace(lhs + " = ... [line " + line + "]");
    }

    // ========================================================================
    // Expressions (selected)
    // ========================================================================

    @Override
    public void enterMethodCall(JvmBasicParser.MethodCallContext ctx) {
        String method = ctx.IDENTIFIER().getText();
        trace("  -> Method call: " + method + "()");
    }

    @Override
    public void enterNewObjectExpr(JvmBasicParser.NewObjectExprContext ctx) {
        String type = ctx.typeName().getText();
        trace("  -> New " + type);
    }
}
