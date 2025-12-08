// Generated from JvmBasicParser.g4 by ANTLR 4.13.2
package com.jvmbasic.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JvmBasicParser}.
 */
public interface JvmBasicParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(JvmBasicParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(JvmBasicParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#topLevelElement}.
	 * @param ctx the parse tree
	 */
	void enterTopLevelElement(JvmBasicParser.TopLevelElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#topLevelElement}.
	 * @param ctx the parse tree
	 */
	void exitTopLevelElement(JvmBasicParser.TopLevelElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(JvmBasicParser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(JvmBasicParser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(JvmBasicParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(JvmBasicParser.QualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(JvmBasicParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(JvmBasicParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#accessModifier}.
	 * @param ctx the parse tree
	 */
	void enterAccessModifier(JvmBasicParser.AccessModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#accessModifier}.
	 * @param ctx the parse tree
	 */
	void exitAccessModifier(JvmBasicParser.AccessModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameters(JvmBasicParser.TypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameters(JvmBasicParser.TypeParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(JvmBasicParser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(JvmBasicParser.TypeParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#classMember}.
	 * @param ctx the parse tree
	 */
	void enterClassMember(JvmBasicParser.ClassMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#classMember}.
	 * @param ctx the parse tree
	 */
	void exitClassMember(JvmBasicParser.ClassMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(JvmBasicParser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(JvmBasicParser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#propertyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPropertyDeclaration(JvmBasicParser.PropertyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#propertyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPropertyDeclaration(JvmBasicParser.PropertyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#propertyAccessor}.
	 * @param ctx the parse tree
	 */
	void enterPropertyAccessor(JvmBasicParser.PropertyAccessorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#propertyAccessor}.
	 * @param ctx the parse tree
	 */
	void exitPropertyAccessor(JvmBasicParser.PropertyAccessorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(JvmBasicParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(JvmBasicParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(JvmBasicParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(JvmBasicParser.MethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(JvmBasicParser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(JvmBasicParser.InterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#interfaceMember}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMember(JvmBasicParser.InterfaceMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#interfaceMember}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMember(JvmBasicParser.InterfaceMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclaration(JvmBasicParser.EnumDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclaration(JvmBasicParser.EnumDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#enumMember}.
	 * @param ctx the parse tree
	 */
	void enterEnumMember(JvmBasicParser.EnumMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#enumMember}.
	 * @param ctx the parse tree
	 */
	void exitEnumMember(JvmBasicParser.EnumMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#subDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSubDeclaration(JvmBasicParser.SubDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#subDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#constDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstDeclaration(JvmBasicParser.ConstDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#constDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstDeclaration(JvmBasicParser.ConstDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(JvmBasicParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(JvmBasicParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(JvmBasicParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(JvmBasicParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterArrayTypeName(JvmBasicParser.ArrayTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitArrayTypeName(JvmBasicParser.ArrayTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NullableTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterNullableTypeName(JvmBasicParser.NullableTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NullableTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitNullableTypeName(JvmBasicParser.NullableTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionTypeName(JvmBasicParser.FunctionTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionTypeName(JvmBasicParser.FunctionTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code QualifiedTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedTypeName(JvmBasicParser.QualifiedTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code QualifiedTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedTypeName(JvmBasicParser.QualifiedTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimitiveTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveTypeName(JvmBasicParser.PrimitiveTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimitiveTypeName}
	 * labeled alternative in {@link JvmBasicParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveTypeName(JvmBasicParser.PrimitiveTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(JvmBasicParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(JvmBasicParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#typeNameList}.
	 * @param ctx the parse tree
	 */
	void enterTypeNameList(JvmBasicParser.TypeNameListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#typeNameList}.
	 * @param ctx the parse tree
	 */
	void exitTypeNameList(JvmBasicParser.TypeNameListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(JvmBasicParser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(JvmBasicParser.TypeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(JvmBasicParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(JvmBasicParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#varStatement}.
	 * @param ctx the parse tree
	 */
	void enterVarStatement(JvmBasicParser.VarStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#varStatement}.
	 * @param ctx the parse tree
	 */
	void exitVarStatement(JvmBasicParser.VarStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#assignmentStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStatement(JvmBasicParser.AssignmentStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#assignmentStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStatement(JvmBasicParser.AssignmentStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterMemberLValue(JvmBasicParser.MemberLValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitMemberLValue(JvmBasicParser.MemberLValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IndexLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterIndexLValue(JvmBasicParser.IndexLValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IndexLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitIndexLValue(JvmBasicParser.IndexLValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ThisLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterThisLValue(JvmBasicParser.ThisLValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ThisLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitThisLValue(JvmBasicParser.ThisLValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterSimpleLValue(JvmBasicParser.SimpleLValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleLValue}
	 * labeled alternative in {@link JvmBasicParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitSimpleLValue(JvmBasicParser.SimpleLValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#assignmentOp}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOp(JvmBasicParser.AssignmentOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#assignmentOp}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOp(JvmBasicParser.AssignmentOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(JvmBasicParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(JvmBasicParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#elseIfClause}.
	 * @param ctx the parse tree
	 */
	void enterElseIfClause(JvmBasicParser.ElseIfClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#elseIfClause}.
	 * @param ctx the parse tree
	 */
	void exitElseIfClause(JvmBasicParser.ElseIfClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#elseClause}.
	 * @param ctx the parse tree
	 */
	void enterElseClause(JvmBasicParser.ElseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#elseClause}.
	 * @param ctx the parse tree
	 */
	void exitElseClause(JvmBasicParser.ElseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(JvmBasicParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(JvmBasicParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#caseClause}.
	 * @param ctx the parse tree
	 */
	void enterCaseClause(JvmBasicParser.CaseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#caseClause}.
	 * @param ctx the parse tree
	 */
	void exitCaseClause(JvmBasicParser.CaseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#caseElseClause}.
	 * @param ctx the parse tree
	 */
	void enterCaseElseClause(JvmBasicParser.CaseElseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#caseElseClause}.
	 * @param ctx the parse tree
	 */
	void exitCaseElseClause(JvmBasicParser.CaseElseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(JvmBasicParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(JvmBasicParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(JvmBasicParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(JvmBasicParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#forEachStatement}.
	 * @param ctx the parse tree
	 */
	void enterForEachStatement(JvmBasicParser.ForEachStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#forEachStatement}.
	 * @param ctx the parse tree
	 */
	void exitForEachStatement(JvmBasicParser.ForEachStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(JvmBasicParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(JvmBasicParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#doStatement}.
	 * @param ctx the parse tree
	 */
	void enterDoStatement(JvmBasicParser.DoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#doStatement}.
	 * @param ctx the parse tree
	 */
	void exitDoStatement(JvmBasicParser.DoStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryStatement(JvmBasicParser.TryStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryStatement(JvmBasicParser.TryStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(JvmBasicParser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(JvmBasicParser.CatchClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#finallyClause}.
	 * @param ctx the parse tree
	 */
	void enterFinallyClause(JvmBasicParser.FinallyClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#finallyClause}.
	 * @param ctx the parse tree
	 */
	void exitFinallyClause(JvmBasicParser.FinallyClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(JvmBasicParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(JvmBasicParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#exitStatement}.
	 * @param ctx the parse tree
	 */
	void enterExitStatement(JvmBasicParser.ExitStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#exitStatement}.
	 * @param ctx the parse tree
	 */
	void exitExitStatement(JvmBasicParser.ExitStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(JvmBasicParser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(JvmBasicParser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void enterThrowStatement(JvmBasicParser.ThrowStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void exitThrowStatement(JvmBasicParser.ThrowStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssertStatement(JvmBasicParser.AssertStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssertStatement(JvmBasicParser.AssertStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#usingStatement}.
	 * @param ctx the parse tree
	 */
	void enterUsingStatement(JvmBasicParser.UsingStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#usingStatement}.
	 * @param ctx the parse tree
	 */
	void exitUsingStatement(JvmBasicParser.UsingStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(JvmBasicParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(JvmBasicParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BaseExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBaseExpr(JvmBasicParser.BaseExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BaseExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBaseExpr(JvmBasicParser.BaseExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AwaitExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAwaitExpr(JvmBasicParser.AwaitExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AwaitExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAwaitExpr(JvmBasicParser.AwaitExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LambdaExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLambdaExpr(JvmBasicParser.LambdaExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LambdaExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLambdaExpr(JvmBasicParser.LambdaExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TernaryExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTernaryExpr(JvmBasicParser.TernaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TernaryExpr}
	 * labeled alternative in {@link JvmBasicParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTernaryExpr(JvmBasicParser.TernaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalOrExpression(JvmBasicParser.ConditionalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalOrExpression(JvmBasicParser.ConditionalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(JvmBasicParser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(JvmBasicParser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#bitwiseOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseOrExpression(JvmBasicParser.BitwiseOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#bitwiseOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseOrExpression(JvmBasicParser.BitwiseOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#bitwiseXorExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseXorExpression(JvmBasicParser.BitwiseXorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#bitwiseXorExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseXorExpression(JvmBasicParser.BitwiseXorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#bitwiseAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseAndExpression(JvmBasicParser.BitwiseAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#bitwiseAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseAndExpression(JvmBasicParser.BitwiseAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(JvmBasicParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(JvmBasicParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(JvmBasicParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(JvmBasicParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression(JvmBasicParser.ShiftExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression(JvmBasicParser.ShiftExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(JvmBasicParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(JvmBasicParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PowerExpr}
	 * labeled alternative in {@link JvmBasicParser#powerExpression}.
	 * @param ctx the parse tree
	 */
	void enterPowerExpr(JvmBasicParser.PowerExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PowerExpr}
	 * labeled alternative in {@link JvmBasicParser#powerExpression}.
	 * @param ctx the parse tree
	 */
	void exitPowerExpr(JvmBasicParser.PowerExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PowerBase}
	 * labeled alternative in {@link JvmBasicParser#powerExpression}.
	 * @param ctx the parse tree
	 */
	void enterPowerBase(JvmBasicParser.PowerBaseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PowerBase}
	 * labeled alternative in {@link JvmBasicParser#powerExpression}.
	 * @param ctx the parse tree
	 */
	void exitPowerBase(JvmBasicParser.PowerBaseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryOpExpr}
	 * labeled alternative in {@link JvmBasicParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOpExpr(JvmBasicParser.UnaryOpExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryOpExpr}
	 * labeled alternative in {@link JvmBasicParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOpExpr(JvmBasicParser.UnaryOpExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PostfixExprAlt}
	 * labeled alternative in {@link JvmBasicParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExprAlt(JvmBasicParser.PostfixExprAltContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PostfixExprAlt}
	 * labeled alternative in {@link JvmBasicParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExprAlt(JvmBasicParser.PostfixExprAltContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression(JvmBasicParser.PostfixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression(JvmBasicParser.PostfixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberAccess}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccess(JvmBasicParser.MemberAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberAccess}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccess(JvmBasicParser.MemberAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(JvmBasicParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(JvmBasicParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SuperConstructorCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterSuperConstructorCall(JvmBasicParser.SuperConstructorCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SuperConstructorCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitSuperConstructorCall(JvmBasicParser.SuperConstructorCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IndexAccess}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterIndexAccess(JvmBasicParser.IndexAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IndexAccess}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitIndexAccess(JvmBasicParser.IndexAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(JvmBasicParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link JvmBasicParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(JvmBasicParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#memberName}.
	 * @param ctx the parse tree
	 */
	void enterMemberName(JvmBasicParser.MemberNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#memberName}.
	 * @param ctx the parse tree
	 */
	void exitMemberName(JvmBasicParser.MemberNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(JvmBasicParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(JvmBasicParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(JvmBasicParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(JvmBasicParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierExpr(JvmBasicParser.IdentifierExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierExpr(JvmBasicParser.IdentifierExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BigIntNamespaceExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterBigIntNamespaceExpr(JvmBasicParser.BigIntNamespaceExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BigIntNamespaceExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitBigIntNamespaceExpr(JvmBasicParser.BigIntNamespaceExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DecimalNamespaceExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterDecimalNamespaceExpr(JvmBasicParser.DecimalNamespaceExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DecimalNamespaceExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitDecimalNamespaceExpr(JvmBasicParser.DecimalNamespaceExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MeExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterMeExpr(JvmBasicParser.MeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MeExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitMeExpr(JvmBasicParser.MeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ThisExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(JvmBasicParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ThisExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(JvmBasicParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MyBaseExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterMyBaseExpr(JvmBasicParser.MyBaseExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MyBaseExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitMyBaseExpr(JvmBasicParser.MyBaseExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SuperExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterSuperExpr(JvmBasicParser.SuperExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SuperExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitSuperExpr(JvmBasicParser.SuperExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewObjectExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterNewObjectExpr(JvmBasicParser.NewObjectExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewObjectExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitNewObjectExpr(JvmBasicParser.NewObjectExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewArrayExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterNewArrayExpr(JvmBasicParser.NewArrayExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewArrayExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitNewArrayExpr(JvmBasicParser.NewArrayExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeOfExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterTypeOfExpr(JvmBasicParser.TypeOfExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeOfExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitTypeOfExpr(JvmBasicParser.TypeOfExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MethodRefExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterMethodRefExpr(JvmBasicParser.MethodRefExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MethodRefExpr}
	 * labeled alternative in {@link JvmBasicParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitMethodRefExpr(JvmBasicParser.MethodRefExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(JvmBasicParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(JvmBasicParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JvmBasicParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(JvmBasicParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link JvmBasicParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(JvmBasicParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(JvmBasicParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(JvmBasicParser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LongLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLongLiteral(JvmBasicParser.LongLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LongLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLongLiteral(JvmBasicParser.LongLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(JvmBasicParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(JvmBasicParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DoubleLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterDoubleLiteral(JvmBasicParser.DoubleLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DoubleLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitDoubleLiteral(JvmBasicParser.DoubleLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(JvmBasicParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(JvmBasicParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InterpolatedStringLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterInterpolatedStringLiteral(JvmBasicParser.InterpolatedStringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InterpolatedStringLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitInterpolatedStringLiteral(JvmBasicParser.InterpolatedStringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CharLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterCharLiteral(JvmBasicParser.CharLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CharLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitCharLiteral(JvmBasicParser.CharLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterTrueLiteral(JvmBasicParser.TrueLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitTrueLiteral(JvmBasicParser.TrueLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFalseLiteral(JvmBasicParser.FalseLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFalseLiteral(JvmBasicParser.FalseLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NilLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterNilLiteral(JvmBasicParser.NilLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NilLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitNilLiteral(JvmBasicParser.NilLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NothingLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterNothingLiteral(JvmBasicParser.NothingLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NothingLiteral}
	 * labeled alternative in {@link JvmBasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitNothingLiteral(JvmBasicParser.NothingLiteralContext ctx);
}