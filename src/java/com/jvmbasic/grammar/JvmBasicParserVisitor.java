// Generated from JvmBasicParser.g4 by ANTLR 4.13.2
package com.jvmbasic.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JvmBasicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JvmBasicParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(JvmBasicParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#topLevelElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopLevelElement(JvmBasicParser.TopLevelElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#importDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDeclaration(JvmBasicParser.ImportDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(JvmBasicParser.QualifiedNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(JvmBasicParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#accessModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccessModifier(JvmBasicParser.AccessModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#typeParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParameters(JvmBasicParser.TypeParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#typeParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParameter(JvmBasicParser.TypeParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#classMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassMember(JvmBasicParser.ClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDeclaration(JvmBasicParser.FieldDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#propertyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyDeclaration(JvmBasicParser.PropertyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#propertyAccessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyAccessor(JvmBasicParser.PropertyAccessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDeclaration(JvmBasicParser.ConstructorDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(JvmBasicParser.MethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#abstractMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbstractMethodDeclaration(JvmBasicParser.AbstractMethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceDeclaration(JvmBasicParser.InterfaceDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#interfaceMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceMember(JvmBasicParser.InterfaceMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#enumDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumDeclaration(JvmBasicParser.EnumDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#enumMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumMember(JvmBasicParser.EnumMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#subDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#constDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDeclaration(JvmBasicParser.ConstDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(JvmBasicParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(JvmBasicParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayTypeName(JvmBasicParser.ArrayTypeNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullableTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullableTypeName(JvmBasicParser.NullableTypeNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionTypeName(JvmBasicParser.FunctionTypeNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code QualifiedTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedTypeName(JvmBasicParser.QualifiedTypeNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrimitiveTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveTypeName(JvmBasicParser.PrimitiveTypeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(JvmBasicParser.PrimitiveTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#typeNameList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeNameList(JvmBasicParser.TypeNameListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#typeArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArguments(JvmBasicParser.TypeArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(JvmBasicParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#varStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarStatement(JvmBasicParser.VarStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(JvmBasicParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MemberLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberLValue(JvmBasicParser.MemberLValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IndexLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexLValue(JvmBasicParser.IndexLValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ThisLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisLValue(JvmBasicParser.ThisLValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleLValue(JvmBasicParser.SimpleLValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#assignmentOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentOp(JvmBasicParser.AssignmentOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(JvmBasicParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#elseIfClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfClause(JvmBasicParser.ElseIfClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#elseClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseClause(JvmBasicParser.ElseClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectStatement(JvmBasicParser.SelectStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#caseClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseClause(JvmBasicParser.CaseClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#caseElseClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseElseClause(JvmBasicParser.CaseElseClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(JvmBasicParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(JvmBasicParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#forEachStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForEachStatement(JvmBasicParser.ForEachStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(JvmBasicParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#doStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoStatement(JvmBasicParser.DoStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#tryStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTryStatement(JvmBasicParser.TryStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#catchClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatchClause(JvmBasicParser.CatchClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#finallyClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFinallyClause(JvmBasicParser.FinallyClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(JvmBasicParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#exitStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExitStatement(JvmBasicParser.ExitStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(JvmBasicParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#throwStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThrowStatement(JvmBasicParser.ThrowStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#assertStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertStatement(JvmBasicParser.AssertStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#usingStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUsingStatement(JvmBasicParser.UsingStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(JvmBasicParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BaseExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseExpr(JvmBasicParser.BaseExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AwaitExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAwaitExpr(JvmBasicParser.AwaitExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LambdaExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaExpr(JvmBasicParser.LambdaExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TernaryExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernaryExpr(JvmBasicParser.TernaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalOrExpression(JvmBasicParser.ConditionalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalAndExpression(JvmBasicParser.ConditionalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#bitwiseOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseOrExpression(JvmBasicParser.BitwiseOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#bitwiseXorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseXorExpression(JvmBasicParser.BitwiseXorExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#bitwiseAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseAndExpression(JvmBasicParser.BitwiseAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(JvmBasicParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(JvmBasicParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#shiftExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShiftExpression(JvmBasicParser.ShiftExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(JvmBasicParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PowerExpr}
	 * labeled alternative in {@link JvmBasicParser#powerExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerExpr(JvmBasicParser.PowerExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PowerBase}
	 * labeled alternative in {@link JvmBasicParser#powerExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerBase(JvmBasicParser.PowerBaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryOpExpr}
	 * labeled alternative in {@link JvmBasicParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOpExpr(JvmBasicParser.UnaryOpExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PostfixExprAlt}
	 * labeled alternative in {@link JvmBasicParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixExprAlt(JvmBasicParser.PostfixExprAltContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#postfixExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixExpression(JvmBasicParser.PostfixExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MemberAccess}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberAccess(JvmBasicParser.MemberAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(JvmBasicParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SuperConstructorCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperConstructorCall(JvmBasicParser.SuperConstructorCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IndexAccess}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexAccess(JvmBasicParser.IndexAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(JvmBasicParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#memberName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberName(JvmBasicParser.MemberNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(JvmBasicParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpr(JvmBasicParser.LiteralExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdentifierExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierExpr(JvmBasicParser.IdentifierExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BigIntNamespaceExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBigIntNamespaceExpr(JvmBasicParser.BigIntNamespaceExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DecimalNamespaceExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalNamespaceExpr(JvmBasicParser.DecimalNamespaceExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MeExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMeExpr(JvmBasicParser.MeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ThisExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisExpr(JvmBasicParser.ThisExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MyBaseExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMyBaseExpr(JvmBasicParser.MyBaseExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SuperExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperExpr(JvmBasicParser.SuperExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewObjectExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewObjectExpr(JvmBasicParser.NewObjectExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewArrayExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArrayExpr(JvmBasicParser.NewArrayExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeOfExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeOfExpr(JvmBasicParser.TypeOfExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MethodRefExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodRefExpr(JvmBasicParser.MethodRefExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(JvmBasicParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JvmBasicParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(JvmBasicParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(JvmBasicParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LongLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLongLiteral(JvmBasicParser.LongLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(JvmBasicParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DoubleLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleLiteral(JvmBasicParser.DoubleLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(JvmBasicParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InterpolatedStringLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterpolatedStringLiteral(JvmBasicParser.InterpolatedStringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CharLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharLiteral(JvmBasicParser.CharLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueLiteral(JvmBasicParser.TrueLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseLiteral(JvmBasicParser.FalseLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NilLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNilLiteral(JvmBasicParser.NilLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NothingLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNothingLiteral(JvmBasicParser.NothingLiteralContext ctx);
}