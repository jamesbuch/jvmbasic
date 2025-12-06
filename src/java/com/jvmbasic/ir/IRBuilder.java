package com.jvmbasic.ir;

import com.jvmbasic.ir.decl.*;
import com.jvmbasic.ir.expr.*;
import com.jvmbasic.ir.stmt.*;
import com.jvmbasic.grammar.JvmBasicParser;
import com.jvmbasic.grammar.JvmBasicParserBaseVisitor;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.jvmbasic.grammar.JvmBasicLexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds IR from ANTLR parse tree.
 *
 * This visitor transforms the concrete syntax tree produced by ANTLR
 * into our typed intermediate representation.
 */
public class IRBuilder extends JvmBasicParserBaseVisitor<Object> {

    private final String unitName;
    private IRCompilationUnit unit;

    // Simple type environment for resolving types
    private final Map<String, IRType> typeMap = new HashMap<>();

    // Track known functions for return type lookup
    private final Map<String, IRType> functionReturnTypes = new HashMap<>();

    public IRBuilder(String unitName) {
        this.unitName = unitName;
        initTypeMap();
    }

    private void initTypeMap() {
        // Map BASIC type names to IR types
        typeMap.put("Integer", IRType.Primitive.INT);
        typeMap.put("integer", IRType.Primitive.INT);
        typeMap.put("Long", IRType.Primitive.LONG);
        typeMap.put("long", IRType.Primitive.LONG);
        typeMap.put("Float", IRType.Primitive.FLOAT);
        typeMap.put("float", IRType.Primitive.FLOAT);
        typeMap.put("Double", IRType.Primitive.DOUBLE);
        typeMap.put("double", IRType.Primitive.DOUBLE);
        typeMap.put("Boolean", IRType.Primitive.BOOLEAN);
        typeMap.put("boolean", IRType.Primitive.BOOLEAN);
        typeMap.put("Byte", IRType.Primitive.BYTE);
        typeMap.put("byte", IRType.Primitive.BYTE);
        typeMap.put("Char", IRType.Primitive.CHAR);
        typeMap.put("char", IRType.Primitive.CHAR);
        typeMap.put("String", IRType.Reference.STRING);
        typeMap.put("string", IRType.Reference.STRING);
        typeMap.put("Object", IRType.Reference.OBJECT);
        typeMap.put("object", IRType.Reference.OBJECT);
    }

    public IRCompilationUnit build(JvmBasicParser.CompilationUnitContext ctx) {
        unit = new IRCompilationUnit(unitName);

        // First pass: collect function signatures for forward references
        // Functions can now appear anywhere in topLevelElement
        for (JvmBasicParser.TopLevelElementContext elem : ctx.topLevelElement()) {
            if (elem.declaration() != null) {
                var decl = elem.declaration();
                if (decl.functionDeclaration() != null) {
                    var funcDecl = decl.functionDeclaration();
                    String name = funcDecl.IDENTIFIER().getText();
                    IRType returnType = visitTypeName(funcDecl.typeName());
                    functionReturnTypes.put(name, returnType);
                } else if (decl.subDeclaration() != null) {
                    var subDecl = decl.subDeclaration();
                    String name = subDecl.IDENTIFIER().getText();
                    functionReturnTypes.put(name, IRType.Primitive.VOID);
                }
            }
        }

        // Process imports
        for (JvmBasicParser.ImportDeclarationContext imp : ctx.importDeclaration()) {
            unit.addImport(visitImportDeclaration(imp));
        }

        // Process top-level elements (declarations and statements can be interleaved)
        for (JvmBasicParser.TopLevelElementContext elem : ctx.topLevelElement()) {
            if (elem.declaration() != null) {
                processDeclaration(elem.declaration());
            } else if (elem.statement() != null) {
                IRStatement irStmt = visitStatement(elem.statement());
                if (irStmt != null) {
                    unit.addStatement(irStmt);
                }
            }
        }

        return unit;
    }

    // ========================================================================
    // Import Handling
    // ========================================================================

    public String visitImportDeclaration(JvmBasicParser.ImportDeclarationContext ctx) {
        String qualifiedName = visitQualifiedName(ctx.qualifiedName());
        if (ctx.STAR() != null) {
            return qualifiedName + ".*";
        }
        return qualifiedName;
    }

    public String visitQualifiedName(JvmBasicParser.QualifiedNameContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
            if (i > 0) sb.append(".");
            sb.append(ctx.IDENTIFIER(i).getText());
        }
        return sb.toString();
    }

    // ========================================================================
    // Declarations
    // ========================================================================

    private void processDeclaration(JvmBasicParser.DeclarationContext ctx) {
        if (ctx.functionDeclaration() != null) {
            unit.addFunction(visitFunctionDeclaration(ctx.functionDeclaration()));
        } else if (ctx.subDeclaration() != null) {
            unit.addFunction(visitSubDeclaration(ctx.subDeclaration()));
        } else if (ctx.classDeclaration() != null) {
            unit.addClass(visitClassDeclaration(ctx.classDeclaration()));
        } else if (ctx.constDeclaration() != null) {
            // Constants become static final fields - for now add as statement
            unit.addStatement(visitConstDeclaration(ctx.constDeclaration()));
        }
        // TODO: interface, enum declarations
    }

    public IRFunction visitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
        Token token = ctx.getStart();
        String name = ctx.IDENTIFIER().getText();
        IRType returnType = visitTypeName(ctx.typeName());

        IRFunction func = new IRFunction(name, returnType, token.getLine(), token.getCharPositionInLine());

        // Parameters
        if (ctx.parameterList() != null) {
            for (JvmBasicParser.ParameterContext param : ctx.parameterList().parameter()) {
                func.addParameter(visitParameter(param));
            }
        }

        // Body statements
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                func.addStatement(irStmt);
            }
        }

        return func;
    }

    public IRFunction visitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        Token token = ctx.getStart();
        String name = ctx.IDENTIFIER().getText();

        IRFunction func = new IRFunction(name, IRType.Primitive.VOID, token.getLine(), token.getCharPositionInLine());

        // Parameters
        if (ctx.parameterList() != null) {
            for (JvmBasicParser.ParameterContext param : ctx.parameterList().parameter()) {
                func.addParameter(visitParameter(param));
            }
        }

        // Body statements
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                func.addStatement(irStmt);
            }
        }

        return func;
    }

    public IRParameter visitParameter(JvmBasicParser.ParameterContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        IRType type = visitTypeName(ctx.typeName());
        boolean byRef = ctx.BYREF() != null;
        Token token = ctx.IDENTIFIER().getSymbol();

        IRExpression defaultValue = null;
        if (ctx.expression() != null) {
            defaultValue = visitExpression(ctx.expression());
        }

        return new IRParameter(name, type, byRef, defaultValue, token.getLine(), token.getCharPositionInLine());
    }

    public IRVarDecl visitConstDeclaration(JvmBasicParser.ConstDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        IRType type = visitTypeName(ctx.typeName());
        IRExpression value = visitExpression(ctx.expression());
        Token token = ctx.CONST().getSymbol();

        return new IRVarDecl(name, type, value, token.getLine(), token.getCharPositionInLine());
    }

    public IRClass visitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Token token = ctx.getStart();

        String superClass = null;
        if (ctx.typeName() != null) {
            superClass = ctx.typeName().getText();
        }

        List<String> interfaces = new ArrayList<>();
        if (ctx.typeNameList() != null) {
            for (JvmBasicParser.TypeNameContext typeName : ctx.typeNameList().typeName()) {
                interfaces.add(typeName.getText());
            }
        }

        boolean isAbstract = ctx.ABSTRACT() != null;
        IRClass cls = new IRClass(name, superClass, isAbstract, token.getLine(), token.getCharPositionInLine());
        for (String iface : interfaces) {
            cls.addInterface(iface);
        }

        // Process class members
        for (JvmBasicParser.ClassMemberContext member : ctx.classMember()) {
            // TODO: Process fields, properties, methods, constructors
        }

        return cls;
    }

    // ========================================================================
    // Type Resolution
    // ========================================================================

    public IRType visitTypeName(JvmBasicParser.TypeNameContext ctx) {
        if (ctx == null) {
            return IRType.Reference.OBJECT;
        }

        // Handle different type name alternatives
        if (ctx instanceof JvmBasicParser.PrimitiveTypeNameContext ptCtx) {
            return visitPrimitiveType(ptCtx.primitiveType());
        } else if (ctx instanceof JvmBasicParser.QualifiedTypeNameContext qtCtx) {
            String name = visitQualifiedName(qtCtx.qualifiedName());
            IRType type = typeMap.get(name);
            if (type != null) {
                return type;
            }
            return new IRType.Reference(name);
        } else if (ctx instanceof JvmBasicParser.ArrayTypeNameContext atCtx) {
            IRType elementType = visitTypeName(atCtx.typeName());
            return new IRType.Array(elementType);
        } else if (ctx instanceof JvmBasicParser.NullableTypeNameContext ntCtx) {
            IRType wrapped = visitTypeName(ntCtx.typeName());
            return new IRType.Nullable(wrapped);
        } else if (ctx instanceof JvmBasicParser.FunctionTypeNameContext ftCtx) {
            List<IRType> paramTypes = new ArrayList<>();
            if (ftCtx.typeNameList() != null) {
                for (JvmBasicParser.TypeNameContext tn : ftCtx.typeNameList().typeName()) {
                    paramTypes.add(visitTypeName(tn));
                }
            }
            IRType returnType = visitTypeName(ftCtx.typeName());
            return new IRType.Function(paramTypes, returnType);
        }

        // Fallback: try to parse as simple name
        String text = ctx.getText();
        IRType type = typeMap.get(text);
        if (type != null) {
            return type;
        }

        return new IRType.Reference(text);
    }

    public IRType visitPrimitiveType(JvmBasicParser.PrimitiveTypeContext ctx) {
        String text = ctx.getText().toLowerCase();
        return switch (text) {
            case "integer" -> IRType.Primitive.INT;
            case "long" -> IRType.Primitive.LONG;
            case "float" -> IRType.Primitive.FLOAT;
            case "double" -> IRType.Primitive.DOUBLE;
            case "boolean" -> IRType.Primitive.BOOLEAN;
            case "byte" -> IRType.Primitive.BYTE;
            case "char" -> IRType.Primitive.CHAR;
            case "string" -> IRType.Reference.STRING;
            case "object" -> IRType.Reference.OBJECT;
            case "decimal" -> new IRType.Reference("java.math.BigDecimal");
            case "bigint" -> new IRType.Reference("java.math.BigInteger");
            default -> IRType.Reference.OBJECT;
        };
    }

    // ========================================================================
    // Statements
    // ========================================================================

    public IRStatement visitStatement(JvmBasicParser.StatementContext ctx) {
        if (ctx.varStatement() != null) {
            return visitVarStatement(ctx.varStatement());
        } else if (ctx.assignmentStatement() != null) {
            return visitAssignmentStatement(ctx.assignmentStatement());
        } else if (ctx.ifStatement() != null) {
            return visitIfStatement(ctx.ifStatement());
        } else if (ctx.forStatement() != null) {
            return visitForStatement(ctx.forStatement());
        } else if (ctx.forEachStatement() != null) {
            return visitForEachStatement(ctx.forEachStatement());
        } else if (ctx.whileStatement() != null) {
            return visitWhileStatement(ctx.whileStatement());
        } else if (ctx.doStatement() != null) {
            return visitDoStatement(ctx.doStatement());
        } else if (ctx.returnStatement() != null) {
            return visitReturnStatement(ctx.returnStatement());
        } else if (ctx.tryStatement() != null) {
            return visitTryStatement(ctx.tryStatement());
        } else if (ctx.throwStatement() != null) {
            return visitThrowStatement(ctx.throwStatement());
        } else if (ctx.expressionStatement() != null) {
            return visitExpressionStatement(ctx.expressionStatement());
        } else if (ctx.selectStatement() != null) {
            return visitSelectStatement(ctx.selectStatement());
        }
        // TODO: exit, continue, using statements
        return null;
    }

    public IRVarDecl visitVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        IRType type = visitTypeName(ctx.typeName());
        Token token = ctx.VAR().getSymbol();

        IRExpression initializer = null;
        if (ctx.expression() != null) {
            initializer = visitExpression(ctx.expression());
        }

        return new IRVarDecl(name, type, initializer, token.getLine(), token.getCharPositionInLine());
    }

    public IRAssignment visitAssignmentStatement(JvmBasicParser.AssignmentStatementContext ctx) {
        String targetName = visitLValue(ctx.lvalue());
        IRExpression value = visitExpression(ctx.expression());
        String opText = ctx.assignmentOp().getText();
        Token token = ctx.lvalue().getStart();

        // Convert target name to expression
        IRExpression target = new IRIdentifier(targetName, inferType(value), token.getLine(), token.getCharPositionInLine());

        // Determine assignment operator
        IRAssignment.Operator op = switch (opText) {
            case "=" -> IRAssignment.Operator.ASSIGN;
            case "+=" -> IRAssignment.Operator.ADD;
            case "-=" -> IRAssignment.Operator.SUB;
            case "*=" -> IRAssignment.Operator.MUL;
            case "/=" -> IRAssignment.Operator.DIV;
            default -> IRAssignment.Operator.ASSIGN;
        };

        return new IRAssignment(target, value, op, token.getLine(), token.getCharPositionInLine());
    }

    public String visitLValue(JvmBasicParser.LvalueContext ctx) {
        // For now, return the full lvalue as a string
        // More complex handling needed for member/index access
        return ctx.getText();
    }

    public IRIf visitIfStatement(JvmBasicParser.IfStatementContext ctx) {
        IRExpression condition = visitExpression(ctx.expression());
        Token token = ctx.getStart();

        IRIf irIf = new IRIf(condition, token.getLine(), token.getCharPositionInLine());

        // Then block
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                irIf.addThenStatement(irStmt);
            }
        }

        // ElseIf clauses
        for (JvmBasicParser.ElseIfClauseContext elseIf : ctx.elseIfClause()) {
            IRExpression elseIfCond = visitExpression(elseIf.expression());
            List<IRStatement> elseIfBody = new ArrayList<>();
            for (JvmBasicParser.StatementContext stmt : elseIf.statement()) {
                IRStatement irStmt = visitStatement(stmt);
                if (irStmt != null) {
                    elseIfBody.add(irStmt);
                }
            }
            irIf.addElseIf(elseIfCond, elseIfBody);
        }

        // Else clause
        if (ctx.elseClause() != null) {
            for (JvmBasicParser.StatementContext stmt : ctx.elseClause().statement()) {
                IRStatement irStmt = visitStatement(stmt);
                if (irStmt != null) {
                    irIf.addElseStatement(irStmt);
                }
            }
        }

        return irIf;
    }

    public IRFor visitForStatement(JvmBasicParser.ForStatementContext ctx) {
        String variable = ctx.IDENTIFIER(0).getText();
        IRExpression start = visitExpression(ctx.expression(0));
        IRExpression end = visitExpression(ctx.expression(1));
        Token token = ctx.FOR().getSymbol();

        IRExpression step = null;
        if (ctx.expression().size() > 2) {
            step = visitExpression(ctx.expression(2));
        }

        IRFor irFor = new IRFor(variable, start, end, step, token.getLine(), token.getCharPositionInLine());

        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                irFor.addStatement(irStmt);
            }
        }

        return irFor;
    }

    public IRForEach visitForEachStatement(JvmBasicParser.ForEachStatementContext ctx) {
        String variable = ctx.IDENTIFIER(0).getText();
        IRExpression collection = visitExpression(ctx.expression());
        Token token = ctx.FOR().getSymbol();

        // Default element type to Object - can be refined by type inference later
        IRType elementType = IRType.Reference.OBJECT;
        IRForEach irForEach = new IRForEach(variable, elementType, collection, token.getLine(), token.getCharPositionInLine());

        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                irForEach.addStatement(irStmt);
            }
        }

        return irForEach;
    }

    public IRWhile visitWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        IRExpression condition = visitExpression(ctx.expression());
        Token token = ctx.getStart();

        IRWhile irWhile = new IRWhile(condition, token.getLine(), token.getCharPositionInLine());

        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                irWhile.addStatement(irStmt);
            }
        }

        return irWhile;
    }

    public IRWhile visitDoStatement(JvmBasicParser.DoStatementContext ctx) {
        // For now, convert do loops to while loops
        // This is a simplification - proper do/until needs special handling
        Token token = ctx.DO().getSymbol();

        IRExpression condition;
        if (ctx.expression().size() > 0) {
            condition = visitExpression(ctx.expression(0));
            // Handle UNTIL by negating condition
            if (ctx.UNTIL().size() > 0) {
                condition = new IRUnaryOp(
                    IRUnaryOp.Operator.NOT, condition, IRType.Primitive.BOOLEAN,
                    token.getLine(), token.getCharPositionInLine()
                );
            }
        } else {
            // Infinite loop
            condition = new IRBoolLiteral(true, token.getLine(), token.getCharPositionInLine());
        }

        IRWhile irWhile = new IRWhile(condition, token.getLine(), token.getCharPositionInLine());

        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                irWhile.addStatement(irStmt);
            }
        }

        return irWhile;
    }

    public IRReturn visitReturnStatement(JvmBasicParser.ReturnStatementContext ctx) {
        Token token = ctx.RETURN().getSymbol();
        IRExpression value = null;
        if (ctx.expression() != null) {
            value = visitExpression(ctx.expression());
        }
        return new IRReturn(value, token.getLine(), token.getCharPositionInLine());
    }

    public IRTry visitTryStatement(JvmBasicParser.TryStatementContext ctx) {
        Token token = ctx.getStart();
        IRTry irTry = new IRTry(token.getLine(), token.getCharPositionInLine());

        // Try block
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            IRStatement irStmt = visitStatement(stmt);
            if (irStmt != null) {
                irTry.addTryStatement(irStmt);
            }
        }

        // Catch clauses
        for (JvmBasicParser.CatchClauseContext catchCtx : ctx.catchClause()) {
            String exceptionVar = catchCtx.IDENTIFIER().getText();
            IRType exceptionType = visitTypeName(catchCtx.typeName());
            List<IRStatement> catchBody = new ArrayList<>();
            for (JvmBasicParser.StatementContext stmt : catchCtx.statement()) {
                IRStatement irStmt = visitStatement(stmt);
                if (irStmt != null) {
                    catchBody.add(irStmt);
                }
            }
            irTry.addCatch(exceptionVar, exceptionType, catchBody);
        }

        // Finally clause
        if (ctx.finallyClause() != null) {
            for (JvmBasicParser.StatementContext stmt : ctx.finallyClause().statement()) {
                IRStatement irStmt = visitStatement(stmt);
                if (irStmt != null) {
                    irTry.addFinallyStatement(irStmt);
                }
            }
        }

        return irTry;
    }

    public IRThrow visitThrowStatement(JvmBasicParser.ThrowStatementContext ctx) {
        IRExpression exception = visitExpression(ctx.expression());
        Token token = ctx.THROW().getSymbol();
        return new IRThrow(exception, token.getLine(), token.getCharPositionInLine());
    }

    public IRStatement visitSelectStatement(JvmBasicParser.SelectStatementContext ctx) {
        // Convert select/case to nested if/else
        IRExpression selector = visitExpression(ctx.expression());
        Token token = ctx.getStart();

        IRIf rootIf = null;
        IRIf currentIf = null;

        for (JvmBasicParser.CaseClauseContext caseCtx : ctx.caseClause()) {
            // Build condition: selector == value1 or selector == value2 ...
            List<JvmBasicParser.ExpressionContext> caseValues = caseCtx.expressionList().expression();
            IRExpression condition = null;

            for (JvmBasicParser.ExpressionContext valueExpr : caseValues) {
                IRExpression caseValue = visitExpression(valueExpr);
                IRExpression eq = new IRBinaryOp(
                    IRBinaryOp.Operator.EQ, selector, caseValue,
                    IRType.Primitive.BOOLEAN, token.getLine(), token.getCharPositionInLine()
                );
                if (condition == null) {
                    condition = eq;
                } else {
                    condition = new IRBinaryOp(
                        IRBinaryOp.Operator.OR, condition, eq,
                        IRType.Primitive.BOOLEAN, token.getLine(), token.getCharPositionInLine()
                    );
                }
            }

            IRIf caseIf = new IRIf(condition, token.getLine(), token.getCharPositionInLine());
            for (JvmBasicParser.StatementContext stmt : caseCtx.statement()) {
                IRStatement irStmt = visitStatement(stmt);
                if (irStmt != null) {
                    caseIf.addThenStatement(irStmt);
                }
            }

            if (rootIf == null) {
                rootIf = caseIf;
                currentIf = caseIf;
            } else {
                currentIf.addElseStatement(caseIf);
                currentIf = caseIf;
            }
        }

        // Handle case else
        if (ctx.caseElseClause() != null && currentIf != null) {
            for (JvmBasicParser.StatementContext stmt : ctx.caseElseClause().statement()) {
                IRStatement irStmt = visitStatement(stmt);
                if (irStmt != null) {
                    currentIf.addElseStatement(irStmt);
                }
            }
        }

        return rootIf;
    }

    public IRExprStmt visitExpressionStatement(JvmBasicParser.ExpressionStatementContext ctx) {
        IRExpression expr = visitExpression(ctx.expression());
        return new IRExprStmt(expr, expr.getLine(), expr.getColumn());
    }

    // ========================================================================
    // Expressions
    // ========================================================================

    public IRExpression visitExpression(JvmBasicParser.ExpressionContext ctx) {
        if (ctx instanceof JvmBasicParser.BaseExprContext baseCtx) {
            return visitConditionalOrExpression(baseCtx.conditionalOrExpression());
        } else if (ctx instanceof JvmBasicParser.TernaryExprContext ternCtx) {
            IRExpression condition = visitExpression(ternCtx.expression(0));
            IRExpression thenExpr = visitExpression(ternCtx.expression(1));
            IRExpression elseExpr = visitExpression(ternCtx.expression(2));
            Token token = ternCtx.getStart();
            // Represent as special binary op or create dedicated IR node
            // For now, use a special construct
            return new IRTernary(condition, thenExpr, elseExpr, inferType(thenExpr),
                                token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.LambdaExprContext lambdaCtx) {
            return visitLambdaExpression(lambdaCtx);
        } else if (ctx instanceof JvmBasicParser.AwaitExprContext awaitCtx) {
            // Await expression - for now just return the inner expression
            return visitExpression(awaitCtx.expression());
        }

        // Fallback - should not reach here if ctx is properly typed
        Token token = ctx.getStart();
        return new IRNullLiteral(null, token.getLine(), token.getCharPositionInLine());
    }

    public IRExpression visitConditionalOrExpression(JvmBasicParser.ConditionalOrExpressionContext ctx) {
        IRExpression result = visitConditionalAndExpression(ctx.conditionalAndExpression(0));

        for (int i = 1; i < ctx.conditionalAndExpression().size(); i++) {
            IRExpression right = visitConditionalAndExpression(ctx.conditionalAndExpression(i));
            result = new IRBinaryOp(IRBinaryOp.Operator.OR, result, right, IRType.Primitive.BOOLEAN,
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitConditionalAndExpression(JvmBasicParser.ConditionalAndExpressionContext ctx) {
        IRExpression result = visitBitwiseOrExpression(ctx.bitwiseOrExpression(0));

        for (int i = 1; i < ctx.bitwiseOrExpression().size(); i++) {
            IRExpression right = visitBitwiseOrExpression(ctx.bitwiseOrExpression(i));
            result = new IRBinaryOp(IRBinaryOp.Operator.AND, result, right, IRType.Primitive.BOOLEAN,
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitBitwiseOrExpression(JvmBasicParser.BitwiseOrExpressionContext ctx) {
        IRExpression result = visitBitwiseXorExpression(ctx.bitwiseXorExpression(0));

        for (int i = 1; i < ctx.bitwiseXorExpression().size(); i++) {
            IRExpression right = visitBitwiseXorExpression(ctx.bitwiseXorExpression(i));
            result = new IRBinaryOp(IRBinaryOp.Operator.BIT_OR, result, right, inferType(result),
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitBitwiseXorExpression(JvmBasicParser.BitwiseXorExpressionContext ctx) {
        IRExpression result = visitBitwiseAndExpression(ctx.bitwiseAndExpression(0));

        for (int i = 1; i < ctx.bitwiseAndExpression().size(); i++) {
            IRExpression right = visitBitwiseAndExpression(ctx.bitwiseAndExpression(i));
            result = new IRBinaryOp(IRBinaryOp.Operator.BIT_XOR, result, right, inferType(result),
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitBitwiseAndExpression(JvmBasicParser.BitwiseAndExpressionContext ctx) {
        IRExpression result = visitEqualityExpression(ctx.equalityExpression(0));

        for (int i = 1; i < ctx.equalityExpression().size(); i++) {
            IRExpression right = visitEqualityExpression(ctx.equalityExpression(i));
            result = new IRBinaryOp(IRBinaryOp.Operator.BIT_AND, result, right, inferType(result),
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitEqualityExpression(JvmBasicParser.EqualityExpressionContext ctx) {
        IRExpression result = visitRelationalExpression(ctx.relationalExpression(0));

        for (int i = 1; i < ctx.relationalExpression().size(); i++) {
            IRExpression right = visitRelationalExpression(ctx.relationalExpression(i));
            // Get operator - check for EQ or NE
            String opText = ctx.getChild(2 * i - 1).getText();
            IRBinaryOp.Operator op = opText.equals("=") ? IRBinaryOp.Operator.EQ : IRBinaryOp.Operator.NE;
            result = new IRBinaryOp(op, result, right, IRType.Primitive.BOOLEAN,
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitRelationalExpression(JvmBasicParser.RelationalExpressionContext ctx) {
        IRExpression result = visitShiftExpression(ctx.shiftExpression(0));

        for (int i = 1; i < ctx.shiftExpression().size(); i++) {
            IRExpression right = visitShiftExpression(ctx.shiftExpression(i));
            String opText = ctx.getChild(2 * i - 1).getText();
            IRBinaryOp.Operator op = switch (opText) {
                case "<" -> IRBinaryOp.Operator.LT;
                case ">" -> IRBinaryOp.Operator.GT;
                case "<=" -> IRBinaryOp.Operator.LE;
                case ">=" -> IRBinaryOp.Operator.GE;
                default -> IRBinaryOp.Operator.EQ;
            };
            result = new IRBinaryOp(op, result, right, IRType.Primitive.BOOLEAN,
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitShiftExpression(JvmBasicParser.ShiftExpressionContext ctx) {
        IRExpression result = visitAdditiveExpression(ctx.additiveExpression(0));

        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            IRExpression right = visitAdditiveExpression(ctx.additiveExpression(i));
            String opText = ctx.getChild(2 * i - 1).getText();
            IRBinaryOp.Operator op = opText.equals("<<") ? IRBinaryOp.Operator.SHL : IRBinaryOp.Operator.SHR;
            result = new IRBinaryOp(op, result, right, inferType(result),
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx) {
        IRExpression result = visitMultiplicativeExpression(ctx.multiplicativeExpression(0));

        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            IRExpression right = visitMultiplicativeExpression(ctx.multiplicativeExpression(i));
            String opText = ctx.getChild(2 * i - 1).getText();

            // Determine operator - & is always concat, + is concat if either operand is string
            IRBinaryOp.Operator op;
            if (opText.equals("&")) {
                op = IRBinaryOp.Operator.CONCAT;
            } else if (opText.equals("+") && (isStringType(result.getType()) || isStringType(right.getType()))) {
                // + with strings is string concatenation (modern syntax)
                op = IRBinaryOp.Operator.CONCAT;
            } else {
                op = switch (opText) {
                    case "+" -> IRBinaryOp.Operator.ADD;
                    case "-" -> IRBinaryOp.Operator.SUB;
                    default -> IRBinaryOp.Operator.ADD;
                };
            }

            IRType type = op == IRBinaryOp.Operator.CONCAT ? IRType.Reference.STRING : inferType(result);
            result = new IRBinaryOp(op, result, right, type, result.getLine(), result.getColumn());
        }

        return result;
    }

    private boolean isStringType(IRType type) {
        return type instanceof IRType.Reference ref &&
               (ref.equals(IRType.Reference.STRING) || ref.name().equals("java.lang.String"));
    }

    public IRExpression visitMultiplicativeExpression(JvmBasicParser.MultiplicativeExpressionContext ctx) {
        IRExpression result = visitPowerExpression(ctx.powerExpression(0));

        for (int i = 1; i < ctx.powerExpression().size(); i++) {
            IRExpression right = visitPowerExpression(ctx.powerExpression(i));
            String opText = ctx.getChild(2 * i - 1).getText().toLowerCase();
            IRBinaryOp.Operator op = switch (opText) {
                case "*" -> IRBinaryOp.Operator.MUL;
                case "/" -> IRBinaryOp.Operator.DIV;
                case "\\" -> IRBinaryOp.Operator.INTDIV;
                case "mod" -> IRBinaryOp.Operator.MOD;
                default -> IRBinaryOp.Operator.MUL;
            };
            result = new IRBinaryOp(op, result, right, inferType(result),
                                   result.getLine(), result.getColumn());
        }

        return result;
    }

    public IRExpression visitPowerExpression(JvmBasicParser.PowerExpressionContext ctx) {
        // Handle labeled alternatives from grammar:
        // powerExpression : <assoc=right> powerExpression CARET powerExpression # PowerExpr
        //                 | unaryExpression                                      # PowerBase
        if (ctx instanceof JvmBasicParser.PowerExprContext powerCtx) {
            // Right-associative power: a^b^c = a^(b^c)
            // ANTLR handles right-associativity automatically with <assoc=right>
            IRExpression left = visitPowerExpression(powerCtx.powerExpression(0));
            IRExpression right = visitPowerExpression(powerCtx.powerExpression(1));
            return new IRBinaryOp(IRBinaryOp.Operator.POWER, left, right, IRType.Primitive.DOUBLE,
                                 left.getLine(), left.getColumn());
        } else if (ctx instanceof JvmBasicParser.PowerBaseContext baseCtx) {
            return visitUnaryExpression(baseCtx.unaryExpression());
        }

        // Fallback - should not reach here
        Token token = ctx.getStart();
        return new IRNullLiteral(null, token.getLine(), token.getCharPositionInLine());
    }

    public IRExpression visitUnaryExpression(JvmBasicParser.UnaryExpressionContext ctx) {
        if (ctx instanceof JvmBasicParser.UnaryOpExprContext unaryCtx) {
            IRExpression operand = visitUnaryExpression(unaryCtx.unaryExpression());
            String opText = unaryCtx.getChild(0).getText().toLowerCase();
            IRUnaryOp.Operator op = switch (opText) {
                case "+" -> IRUnaryOp.Operator.PLUS;
                case "-" -> IRUnaryOp.Operator.MINUS;
                case "not" -> IRUnaryOp.Operator.NOT;
                case "~" -> IRUnaryOp.Operator.BIT_NOT;
                default -> IRUnaryOp.Operator.PLUS;
            };
            IRType type = op == IRUnaryOp.Operator.NOT ? IRType.Primitive.BOOLEAN : inferType(operand);
            return new IRUnaryOp(op, operand, type, operand.getLine(), operand.getColumn());
        } else if (ctx instanceof JvmBasicParser.PostfixExprAltContext postCtx) {
            return visitPostfixExpression(postCtx.postfixExpression());
        }

        // Fallback - should not reach here if ctx is properly typed
        Token token = ctx.getStart();
        return new IRNullLiteral(null, token.getLine(), token.getCharPositionInLine());
    }

    public IRExpression visitPostfixExpression(JvmBasicParser.PostfixExpressionContext ctx) {
        IRExpression result = visitPrimaryExpression(ctx.primaryExpression());

        for (JvmBasicParser.PostfixOpContext op : ctx.postfixOp()) {
            result = applyPostfixOp(result, op);
        }

        return result;
    }

    private IRExpression applyPostfixOp(IRExpression base, JvmBasicParser.PostfixOpContext ctx) {
        if (ctx instanceof JvmBasicParser.MemberAccessContext memberCtx) {
            String memberName = memberCtx.IDENTIFIER().getText();
            Token token = memberCtx.DOT().getSymbol();
            return new IRMemberAccess(base, memberName, IRType.Reference.OBJECT,
                                     token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.MethodCallContext callCtx) {
            String methodName = callCtx.IDENTIFIER().getText();
            List<IRExpression> args = new ArrayList<>();
            if (callCtx.argumentList() != null) {
                for (JvmBasicParser.ArgumentContext arg : callCtx.argumentList().argument()) {
                    args.add(visitExpression(arg.expression()));
                }
            }
            Token token = callCtx.DOT().getSymbol();

            // Check if base is an identifier (could be static class name)
            if (base instanceof IRIdentifier id) {
                String className = id.name();
                // Known static classes (BASIC namespaces and Java classes)
                if (isKnownStaticClass(className)) {
                    return new IRMethodCall(null, className, methodName, args,
                                           guessMethodReturnType(className, methodName),
                                           true, token.getLine(), token.getCharPositionInLine());
                }
            }

            return new IRMethodCall(base, null, methodName, args,
                                   guessMethodReturnType(null, methodName),
                                   false, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.IndexAccessContext indexCtx) {
            IRExpression index = visitExpression(indexCtx.expression());
            Token token = indexCtx.LBRACKET().getSymbol();
            IRType elementType = IRType.Reference.OBJECT;
            if (base.getType() instanceof IRType.Array arr) {
                elementType = arr.elementType();
            }
            return new IRArrayAccess(base, index, elementType,
                                    token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.FunctionCallContext funcCtx) {
            // This is a function call on an expression
            List<IRExpression> args = new ArrayList<>();
            if (funcCtx.argumentList() != null) {
                for (JvmBasicParser.ArgumentContext arg : funcCtx.argumentList().argument()) {
                    args.add(visitExpression(arg.expression()));
                }
            }
            Token token = funcCtx.LPAREN().getSymbol();

            // If base is an identifier, treat as regular function call
            if (base instanceof IRIdentifier id) {
                IRType returnType = functionReturnTypes.getOrDefault(id.name(), IRType.Reference.OBJECT);
                return new IRCall(id.name(), args, returnType,
                                 token.getLine(), token.getCharPositionInLine());
            }

            // If base is a member access, convert to method call
            // e.g., Console.WriteLine becomes member access, then () makes it a method call
            if (base instanceof IRMemberAccess member) {
                String methodName = member.memberName();
                IRExpression receiver = member.object();

                // Check if receiver is an identifier (could be static class name)
                if (receiver instanceof IRIdentifier id) {
                    String className = id.name();
                    // Known static classes (BASIC namespaces and Java classes)
                    if (isKnownStaticClass(className)) {
                        return new IRMethodCall(null, className, methodName, args,
                                               guessMethodReturnType(className, methodName),
                                               true, token.getLine(), token.getCharPositionInLine());
                    }
                }

                // Instance method call
                return new IRMethodCall(receiver, null, methodName, args,
                                       guessMethodReturnType(null, methodName),
                                       false, token.getLine(), token.getCharPositionInLine());
            }

            // Otherwise, it's a call on a function value (lambda invocation)
            return new IRCall("$invoke$", args, IRType.Reference.OBJECT,
                             token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.SuperConstructorCallContext superCtx) {
            // Handle MyBase.new() - super constructor call
            List<IRExpression> args = new ArrayList<>();
            if (superCtx.argumentList() != null) {
                for (JvmBasicParser.ArgumentContext arg : superCtx.argumentList().argument()) {
                    args.add(visitExpression(arg.expression()));
                }
            }
            Token token = superCtx.DOT().getSymbol();
            return new IRMethodCall(base, null, "<init>", args,
                                   IRType.Primitive.VOID,
                                   false, token.getLine(), token.getCharPositionInLine());
        }

        return base;
    }

    public IRExpression visitPrimaryExpression(JvmBasicParser.PrimaryExpressionContext ctx) {
        if (ctx instanceof JvmBasicParser.ParenExprContext parenCtx) {
            return visitExpression(parenCtx.expression());
        } else if (ctx instanceof JvmBasicParser.LiteralExprContext litCtx) {
            return visitLiteral(litCtx.literal());
        } else if (ctx instanceof JvmBasicParser.IdentifierExprContext idCtx) {
            String name = idCtx.IDENTIFIER().getText();
            Token token = idCtx.IDENTIFIER().getSymbol();
            return new IRIdentifier(name, IRType.Reference.OBJECT,
                                   token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.MeExprContext meCtx) {
            // Deprecated: ME - use THIS instead
            Token token = meCtx.ME().getSymbol();
            return new IRIdentifier("this", IRType.Reference.OBJECT,
                                   token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.ThisExprContext thisCtx) {
            Token token = thisCtx.THIS().getSymbol();
            return new IRIdentifier("this", IRType.Reference.OBJECT,
                                   token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.MyBaseExprContext myBaseCtx) {
            // Deprecated: MYBASE - use SUPER instead
            Token token = myBaseCtx.MYBASE().getSymbol();
            return new IRIdentifier("super", IRType.Reference.OBJECT,
                                   token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.SuperExprContext superCtx) {
            Token token = superCtx.SUPER().getSymbol();
            return new IRIdentifier("super", IRType.Reference.OBJECT,
                                   token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.NewObjectExprContext newCtx) {
            IRType type = visitTypeName(newCtx.typeName());
            List<IRExpression> args = new ArrayList<>();
            if (newCtx.argumentList() != null) {
                for (JvmBasicParser.ArgumentContext arg : newCtx.argumentList().argument()) {
                    args.add(visitExpression(arg.expression()));
                }
            }
            Token token = newCtx.NEW().getSymbol();
            return new IRNewObject(type, args, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.NewArrayExprContext newArrCtx) {
            IRType elementType = visitTypeName(newArrCtx.typeName());
            IRExpression size = visitExpression(newArrCtx.expression());
            Token token = newArrCtx.NEW().getSymbol();
            return new IRNewArray(elementType, size, token.getLine(), token.getCharPositionInLine());
        }

        // Fallback
        return new IRNullLiteral(IRType.Reference.OBJECT, 0, 0);
    }

    public IRExpression visitLiteral(JvmBasicParser.LiteralContext ctx) {
        if (ctx instanceof JvmBasicParser.IntLiteralContext intCtx) {
            Token token = intCtx.INTEGER_LITERAL().getSymbol();
            int value = parseInteger(intCtx.INTEGER_LITERAL().getText());
            return new IRIntLiteral(value, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.LongLiteralContext longCtx) {
            Token token = longCtx.LONG_LITERAL().getSymbol();
            String text = longCtx.LONG_LITERAL().getText();
            long value = parseLong(text.substring(0, text.length() - 1));
            return new IRLongLiteral(value, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.FloatLiteralContext floatCtx) {
            Token token = floatCtx.FLOAT_LITERAL().getSymbol();
            float value = Float.parseFloat(floatCtx.FLOAT_LITERAL().getText().replace("F", "").replace("f", ""));
            return new IRFloatLiteral(value, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.DoubleLiteralContext doubleCtx) {
            Token token = doubleCtx.DOUBLE_LITERAL().getSymbol();
            double value = Double.parseDouble(doubleCtx.DOUBLE_LITERAL().getText().replace("D", "").replace("d", ""));
            return new IRDoubleLiteral(value, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.StringLiteralContext strCtx) {
            Token token = strCtx.STRING_LITERAL().getSymbol();
            String text = strCtx.STRING_LITERAL().getText();
            String value = unescapeString(text.substring(1, text.length() - 1));
            return new IRStringLiteral(value, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.TrueLiteralContext trueCtx) {
            Token token = trueCtx.TRUE().getSymbol();
            return new IRBoolLiteral(true, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.FalseLiteralContext falseCtx) {
            Token token = falseCtx.FALSE().getSymbol();
            return new IRBoolLiteral(false, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.NilLiteralContext nilCtx) {
            Token token = nilCtx.NIL().getSymbol();
            return new IRNullLiteral(null, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.NothingLiteralContext nothingCtx) {
            Token token = nothingCtx.NOTHING().getSymbol();
            return new IRNullLiteral(null, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.CharLiteralContext charCtx) {
            Token token = charCtx.CHAR_LITERAL().getSymbol();
            String text = charCtx.CHAR_LITERAL().getText();
            // Extract character from 'x' format
            char value = text.charAt(1);
            return new IRIntLiteral(value, token.getLine(), token.getCharPositionInLine());
        } else if (ctx instanceof JvmBasicParser.InterpolatedStringLiteralContext interpCtx) {
            Token token = interpCtx.INTERPOLATED_STRING().getSymbol();
            String text = interpCtx.INTERPOLATED_STRING().getText();
            return parseInterpolatedString(text, token.getLine(), token.getCharPositionInLine());
        }

        return new IRNullLiteral(null, 0, 0);
    }

    /**
     * Parse an interpolated string like $"Hello {name}!" into IR nodes.
     * Produces an IRInterpolatedString with alternating literal and expression parts.
     */
    private IRExpression parseInterpolatedString(String text, int line, int column) {
        // Remove $" prefix and " suffix
        String content = text.substring(2, text.length() - 1);

        List<IRExpression> parts = new ArrayList<>();
        StringBuilder literal = new StringBuilder();
        int i = 0;

        while (i < content.length()) {
            char c = content.charAt(i);
            if (c == '{') {
                // Flush any accumulated literal
                if (literal.length() > 0) {
                    parts.add(new IRStringLiteral(literal.toString(), line, column));
                    literal.setLength(0);
                }
                // Find matching closing brace
                int braceCount = 1;
                int start = i + 1;
                i++;
                while (i < content.length() && braceCount > 0) {
                    if (content.charAt(i) == '{') braceCount++;
                    else if (content.charAt(i) == '}') braceCount--;
                    if (braceCount > 0) i++;
                }
                String exprText = content.substring(start, i);
                i++; // Skip closing brace

                // Parse the expression using ANTLR
                IRExpression exprNode = parseInterpolationExpression(exprText.trim(), line, column);
                parts.add(exprNode);
            } else if (c == '\\' && i + 1 < content.length()) {
                // Handle escape sequences
                char next = content.charAt(i + 1);
                switch (next) {
                    case 'n' -> literal.append('\n');
                    case 't' -> literal.append('\t');
                    case 'r' -> literal.append('\r');
                    case '\\' -> literal.append('\\');
                    case '"' -> literal.append('"');
                    case '{' -> literal.append('{');
                    case '}' -> literal.append('}');
                    default -> { literal.append(c); literal.append(next); }
                }
                i += 2;
            } else {
                literal.append(c);
                i++;
            }
        }

        // Flush any remaining literal
        if (literal.length() > 0) {
            parts.add(new IRStringLiteral(literal.toString(), line, column));
        }

        // If only one part and it's a string, return just the string
        if (parts.size() == 1 && parts.get(0) instanceof IRStringLiteral) {
            return parts.get(0);
        }

        return new IRInterpolatedString(parts, line, column);
    }

    /**
     * Parse an expression from string interpolation using ANTLR.
     * This allows complex expressions like Str.ToUpper(name) or Math.Sqrt(count).
     */
    private IRExpression parseInterpolationExpression(String exprText, int line, int column) {
        try {
            // Create ANTLR lexer and parser for the expression
            var input = CharStreams.fromString(exprText);
            var lexer = new JvmBasicLexer(input);
            lexer.removeErrorListeners();  // Suppress error output
            var tokens = new CommonTokenStream(lexer);
            var parser = new JvmBasicParser(tokens);
            parser.removeErrorListeners();  // Suppress error output

            // Parse as expression
            JvmBasicParser.ExpressionContext exprCtx = parser.expression();

            // Check for syntax errors
            if (parser.getNumberOfSyntaxErrors() > 0) {
                // Fall back to identifier if parsing fails
                return new IRIdentifier(exprText, IRType.Reference.OBJECT, line, column);
            }

            // Visit the expression to build IR
            return visitExpression(exprCtx);
        } catch (Exception e) {
            // Fall back to identifier if parsing fails
            return new IRIdentifier(exprText, IRType.Reference.OBJECT, line, column);
        }
    }

    private IRExpression visitLambdaExpression(JvmBasicParser.LambdaExprContext ctx) {
        // TODO: Proper lambda support
        // For now, return a placeholder
        Token token = ctx.LAMBDA().getSymbol();
        return new IRNullLiteral(null, token.getLine(), token.getCharPositionInLine());
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private int parseInteger(String text) {
        text = text.replace("_", "");
        if (text.startsWith("0x") || text.startsWith("0X")) {
            return Integer.parseInt(text.substring(2), 16);
        } else if (text.startsWith("0b") || text.startsWith("0B")) {
            return Integer.parseInt(text.substring(2), 2);
        } else if (text.startsWith("0o") || text.startsWith("0O")) {
            return Integer.parseInt(text.substring(2), 8);
        }
        return Integer.parseInt(text);
    }

    private long parseLong(String text) {
        text = text.replace("_", "");
        if (text.startsWith("0x") || text.startsWith("0X")) {
            return Long.parseLong(text.substring(2), 16);
        } else if (text.startsWith("0b") || text.startsWith("0B")) {
            return Long.parseLong(text.substring(2), 2);
        }
        return Long.parseLong(text);
    }

    private String unescapeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                sb.append(switch (next) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case '\\' -> '\\';
                    case '"' -> '"';
                    case '\'' -> '\'';
                    case '0' -> '\0';
                    default -> next;
                });
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private IRType inferType(IRExpression expr) {
        return expr.getType();
    }

    /**
     * Check if the given class name is a known static class (BASIC namespace or Java class).
     */
    private boolean isKnownStaticClass(String className) {
        return switch (className) {
            // BASIC namespaces
            case "Console", "Math", "Str", "File", "Regex" -> true;
            // Java classes commonly used statically
            case "Integer", "String", "System", "Long", "Double", "Float", "Boolean" -> true;
            default -> false;
        };
    }

    private IRType guessMethodReturnType(String className, String methodName) {
        // Common method return types
        if (className != null) {
            return switch (className) {
                case "Console" -> switch (methodName) {
                    case "WriteLine", "Write" -> IRType.Primitive.VOID;
                    case "ReadLine" -> IRType.Reference.STRING;
                    default -> IRType.Reference.OBJECT;
                };
                case "Math" -> switch (methodName) {
                    case "Sqrt", "Sin", "Cos", "Tan", "Log", "Exp", "Pow", "Floor", "Ceiling", "Round",
                         "Pi", "E", "ToRadians", "ToDegrees", "Hypot", "Lerp" -> IRType.Primitive.DOUBLE;
                    case "Abs", "Max", "Min", "Sign", "Clamp" -> IRType.Primitive.INT;
                    case "Random" -> IRType.Primitive.DOUBLE;
                    case "IsNaN", "IsInfinite", "IsFinite", "ApproxEqual", "ApproxEqualRelative" -> IRType.Primitive.BOOLEAN;
                    default -> IRType.Primitive.DOUBLE;
                };
                case "Str" -> switch (methodName) {
                    case "Length", "IndexOf", "LastIndexOf", "Compare" -> IRType.Primitive.INT;
                    case "StartsWith", "EndsWith", "Contains", "IsNullOrEmpty", "IsNullOrWhiteSpace",
                         "Equals", "EqualsIgnoreCase" -> IRType.Primitive.BOOLEAN;
                    case "CharAt" -> IRType.Primitive.CHAR;
                    case "Split" -> new IRType.Array(IRType.Reference.STRING);
                    default -> IRType.Reference.STRING;  // Most Str methods return String
                };
                case "File" -> switch (methodName) {
                    case "Exists", "Delete", "CreateDirectory", "Copy", "Move" -> IRType.Primitive.BOOLEAN;
                    case "Size" -> IRType.Primitive.LONG;
                    case "ReadAllLines", "ListFiles", "ListDirectories" -> new IRType.Array(IRType.Reference.STRING);
                    default -> IRType.Reference.STRING;  // ReadAllText, etc.
                };
                case "Regex" -> switch (methodName) {
                    case "IsMatch" -> IRType.Primitive.BOOLEAN;
                    case "Matches", "Split" -> new IRType.Array(IRType.Reference.STRING);
                    default -> IRType.Reference.STRING;  // Replace, etc.
                };
                case "Integer" -> switch (methodName) {
                    case "Parse" -> IRType.Primitive.INT;
                    default -> IRType.Reference.OBJECT;
                };
                case "String" -> IRType.Reference.STRING;
                default -> IRType.Reference.OBJECT;
            };
        }

        // Instance method guesses
        return switch (methodName) {
            case "ToString", "toString" -> IRType.Reference.STRING;
            case "Length", "length", "Size", "size" -> IRType.Primitive.INT;
            default -> IRType.Reference.OBJECT;
        };
    }
}
