# AST Node Definitions

## Overview

The AST (Abstract Syntax Tree) is built from the ANTLR4 parse tree using the Visitor pattern. Each node class is immutable and contains source location information for error reporting.

## Java Implementation

```java
package com.jvmbasic.ast;

import java.util.List;
import java.util.Optional;

// ============================================================================
// Base Classes
// ============================================================================

public sealed interface AstNode permits Declaration, Statement, Expression, TypeNode {
    SourceLocation location();
}

public record SourceLocation(String file, int line, int column) {}

// ============================================================================
// Type Nodes
// ============================================================================

public sealed interface TypeNode extends AstNode
    permits PrimitiveType, NamedType, ArrayType, NullableType, FunctionType, GenericType {}

public record PrimitiveType(
    SourceLocation location,
    PrimitiveKind kind
) implements TypeNode {}

public enum PrimitiveKind {
    INTEGER, LONG, FLOAT, DOUBLE, STRING, BOOLEAN, BYTE, CHAR, VOID
}

public record NamedType(
    SourceLocation location,
    String name,
    List<TypeNode> typeArguments
) implements TypeNode {}

public record ArrayType(
    SourceLocation location,
    TypeNode elementType
) implements TypeNode {}

public record NullableType(
    SourceLocation location,
    TypeNode innerType
) implements TypeNode {}

public record FunctionType(
    SourceLocation location,
    List<TypeNode> parameterTypes,
    TypeNode returnType
) implements TypeNode {}

public record GenericType(
    SourceLocation location,
    String name,
    Optional<TypeNode> constraint
) implements TypeNode {}

// ============================================================================
// Declarations
// ============================================================================

public sealed interface Declaration extends AstNode
    permits ClassDecl, InterfaceDecl, FunctionDecl, SubDecl, EnumDecl, ImportDecl {}

public record ClassDecl(
    SourceLocation location,
    AccessModifier access,
    boolean isAbstract,
    String name,
    List<GenericType> typeParameters,
    Optional<TypeNode> superClass,
    List<TypeNode> interfaces,
    List<ClassMember> members
) implements Declaration {}

public enum AccessModifier { PUBLIC, PRIVATE, PROTECTED, INTERNAL }

public sealed interface ClassMember extends AstNode
    permits FieldDecl, PropertyDecl, MethodDecl, ConstructorDecl {}

public record FieldDecl(
    SourceLocation location,
    AccessModifier access,
    boolean isShared,
    String name,
    TypeNode type,
    Optional<Expression> initializer
) implements ClassMember {}

public record PropertyDecl(
    SourceLocation location,
    AccessModifier access,
    String name,
    TypeNode type,
    Optional<List<Statement>> getter,
    Optional<PropertySetter> setter
) implements ClassMember {}

public record PropertySetter(
    String parameterName,
    TypeNode parameterType,
    List<Statement> body
) {}

public record MethodDecl(
    SourceLocation location,
    AccessModifier access,
    boolean isShared,
    boolean isFunction,  // true = Function (returns value), false = Sub
    String name,
    List<GenericType> typeParameters,
    List<Parameter> parameters,
    Optional<TypeNode> returnType,
    List<Statement> body
) implements ClassMember {}

public record ConstructorDecl(
    SourceLocation location,
    AccessModifier access,
    List<Parameter> parameters,
    List<Statement> body
) implements ClassMember {}

public record Parameter(
    SourceLocation location,
    boolean isByRef,
    String name,
    TypeNode type,
    Optional<Expression> defaultValue
) {}

public record InterfaceDecl(
    SourceLocation location,
    AccessModifier access,
    String name,
    List<GenericType> typeParameters,
    List<TypeNode> superInterfaces,
    List<InterfaceMember> members
) implements Declaration {}

public sealed interface InterfaceMember extends AstNode
    permits InterfaceMethod, InterfaceProperty {}

public record InterfaceMethod(
    SourceLocation location,
    boolean isFunction,
    String name,
    List<Parameter> parameters,
    Optional<TypeNode> returnType
) implements InterfaceMember {}

public record InterfaceProperty(
    SourceLocation location,
    String name,
    TypeNode type
) implements InterfaceMember {}

public record FunctionDecl(
    SourceLocation location,
    AccessModifier access,
    String name,
    List<GenericType> typeParameters,
    List<Parameter> parameters,
    TypeNode returnType,
    List<Statement> body
) implements Declaration {}

public record SubDecl(
    SourceLocation location,
    AccessModifier access,
    String name,
    List<Parameter> parameters,
    List<Statement> body
) implements Declaration {}

public record EnumDecl(
    SourceLocation location,
    AccessModifier access,
    String name,
    List<EnumMember> members
) implements Declaration {}

public record EnumMember(
    SourceLocation location,
    String name,
    Optional<Integer> value
) {}

public record ImportDecl(
    SourceLocation location,
    String qualifiedName,
    boolean isWildcard
) implements Declaration {}

// ============================================================================
// Statements
// ============================================================================

public sealed interface Statement extends AstNode permits
    DimStmt, AssignStmt, IfStmt, SelectStmt, ForStmt, ForEachStmt,
    WhileStmt, DoStmt, TryStmt, ReturnStmt, ExitStmt, ContinueStmt,
    ThrowStmt, UsingStmt, ExpressionStmt {}

public record DimStmt(
    SourceLocation location,
    String name,
    TypeNode type,
    Optional<Expression> initializer
) implements Statement {}

public record AssignStmt(
    SourceLocation location,
    Expression target,
    AssignOp operator,
    Expression value
) implements Statement {}

public enum AssignOp { ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, TIMES_ASSIGN, DIVIDE_ASSIGN }

public record IfStmt(
    SourceLocation location,
    Expression condition,
    List<Statement> thenBranch,
    List<ElseIfClause> elseIfClauses,
    Optional<List<Statement>> elseBranch
) implements Statement {}

public record ElseIfClause(
    SourceLocation location,
    Expression condition,
    List<Statement> body
) {}

public record SelectStmt(
    SourceLocation location,
    Expression selector,
    List<CaseClause> cases,
    Optional<List<Statement>> elseCase
) implements Statement {}

public record CaseClause(
    SourceLocation location,
    List<Expression> values,
    List<Statement> body
) {}

public record ForStmt(
    SourceLocation location,
    String variable,
    Expression start,
    Expression end,
    Optional<Expression> step,
    List<Statement> body
) implements Statement {}

public record ForEachStmt(
    SourceLocation location,
    String variable,
    Expression collection,
    List<Statement> body
) implements Statement {}

public record WhileStmt(
    SourceLocation location,
    Expression condition,
    List<Statement> body
) implements Statement {}

public record DoStmt(
    SourceLocation location,
    Optional<Expression> preCondition,
    boolean preConditionIsWhile,  // true = While, false = Until
    List<Statement> body,
    Optional<Expression> postCondition,
    boolean postConditionIsWhile
) implements Statement {}

public record TryStmt(
    SourceLocation location,
    List<Statement> tryBody,
    List<CatchClause> catchClauses,
    Optional<List<Statement>> finallyBody
) implements Statement {}

public record CatchClause(
    SourceLocation location,
    String variable,
    TypeNode exceptionType,
    List<Statement> body
) {}

public record ReturnStmt(
    SourceLocation location,
    Optional<Expression> value
) implements Statement {}

public record ExitStmt(
    SourceLocation location,
    ExitKind kind
) implements Statement {}

public enum ExitKind { FOR, WHILE, DO, SUB, FUNCTION, SELECT }

public record ContinueStmt(
    SourceLocation location,
    ContinueKind kind
) implements Statement {}

public enum ContinueKind { FOR, WHILE, DO }

public record ThrowStmt(
    SourceLocation location,
    Expression exception
) implements Statement {}

public record UsingStmt(
    SourceLocation location,
    String variable,
    Expression resource,
    List<Statement> body
) implements Statement {}

public record ExpressionStmt(
    SourceLocation location,
    Expression expression
) implements Statement {}

// ============================================================================
// Expressions
// ============================================================================

public sealed interface Expression extends AstNode permits
    LiteralExpr, IdentifierExpr, MeExpr, MyBaseExpr, ParenExpr,
    BinaryExpr, UnaryExpr, CallExpr, MethodCallExpr, MemberAccessExpr,
    IndexExpr, NewExpr, NewArrayExpr, CastExpr, TypeOfExpr,
    TernaryExpr, LambdaExpr, AwaitExpr {}

public record LiteralExpr(
    SourceLocation location,
    Object value,  // Integer, Long, Float, Double, String, Character, Boolean, or null (Nothing)
    TypeNode type
) implements Expression {}

public record IdentifierExpr(
    SourceLocation location,
    String name
) implements Expression {}

public record MeExpr(SourceLocation location) implements Expression {}

public record MyBaseExpr(SourceLocation location) implements Expression {}

public record ParenExpr(
    SourceLocation location,
    Expression inner
) implements Expression {}

public record BinaryExpr(
    SourceLocation location,
    Expression left,
    BinaryOp operator,
    Expression right
) implements Expression {}

public enum BinaryOp {
    // Arithmetic
    ADD, SUBTRACT, MULTIPLY, DIVIDE, INTEGER_DIVIDE, MODULO,
    // Comparison
    EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL,
    // Logical
    AND, OR, XOR,
    // Bitwise
    BIT_AND, BIT_OR, BIT_XOR, SHIFT_LEFT, SHIFT_RIGHT,
    // String
    CONCAT
}

public record UnaryExpr(
    SourceLocation location,
    UnaryOp operator,
    Expression operand
) implements Expression {}

public enum UnaryOp { PLUS, MINUS, NOT, BIT_NOT }

public record CallExpr(
    SourceLocation location,
    Expression callee,
    List<Argument> arguments
) implements Expression {}

public record Argument(
    Optional<String> name,  // Named argument
    Expression value
) {}

public record MethodCallExpr(
    SourceLocation location,
    Expression object,
    String methodName,
    List<Argument> arguments
) implements Expression {}

public record MemberAccessExpr(
    SourceLocation location,
    Expression object,
    String memberName
) implements Expression {}

public record IndexExpr(
    SourceLocation location,
    Expression array,
    Expression index
) implements Expression {}

public record NewExpr(
    SourceLocation location,
    TypeNode type,
    List<Argument> arguments
) implements Expression {}

public record NewArrayExpr(
    SourceLocation location,
    TypeNode elementType,
    Expression size
) implements Expression {}

public record CastExpr(
    SourceLocation location,
    TypeNode targetType,
    Expression expression
) implements Expression {}

public record TypeOfExpr(
    SourceLocation location,
    Expression expression
) implements Expression {}

public record TernaryExpr(
    SourceLocation location,
    Expression condition,
    Expression thenExpr,
    Expression elseExpr
) implements Expression {}

public record LambdaExpr(
    SourceLocation location,
    List<Parameter> parameters,
    Expression body  // Could also be List<Statement> for multi-line lambdas
) implements Expression {}

public record AwaitExpr(
    SourceLocation location,
    Expression expression
) implements Expression {}
```

## AST Builder (Visitor)

```java
package com.jvmbasic.ast;

import com.jvmbasic.parser.ObjectBasicBaseVisitor;
import com.jvmbasic.parser.ObjectBasicParser;
import org.antlr.v4.runtime.Token;

public class AstBuilder extends ObjectBasicBaseVisitor<AstNode> {

    private SourceLocation loc(Token token) {
        return new SourceLocation(
            token.getInputStream().getSourceName(),
            token.getLine(),
            token.getCharPositionInLine()
        );
    }

    @Override
    public ClassDecl visitClassDeclaration(ObjectBasicParser.ClassDeclarationContext ctx) {
        return new ClassDecl(
            loc(ctx.CLASS().getSymbol()),
            visitAccessModifier(ctx.accessModifier()),
            ctx.ABSTRACT() != null,
            ctx.IDENTIFIER().getText(),
            visitTypeParameters(ctx.typeParameters()),
            Optional.ofNullable(ctx.typeName()).map(this::visitTypeName),
            visitTypeNameList(ctx.typeNameList()),
            ctx.classMember().stream()
                .map(this::visitClassMember)
                .toList()
        );
    }

    // ... more visitor methods for each grammar rule
}
```

## Key Design Decisions

1. **Sealed interfaces** - Exhaustive pattern matching in Java 17+
2. **Records** - Immutable data classes with built-in equals/hashCode
3. **Optional** - No null values, explicit optionality
4. **Source locations** - Every node tracks its position for errors
5. **Visitor pattern** - Clean separation between parse tree and AST
