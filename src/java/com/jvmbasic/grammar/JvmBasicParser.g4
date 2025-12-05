// JvmBasicParser.g4 - Parser grammar for JVM BASIC
//
// A modern BASIC dialect targeting the JVM with clean syntax.
// Uses separate lexer grammar via tokenVocab option.
//
// Syntax highlights:
//   var x as Integer = 10       (variable declaration, no Dim)
//   Type? for nullable types    (e.g., String?)
//   Nil/Nothing as null aliases
//   No required newlines (clean formatting recommended)
//   All I/O via namespaced calls:
//       Console.WriteLine("Hello")
//       Console.ReadLine()
//       File.ReadAllText(path)
//   No legacy BASIC: PRINT, INPUT, WEND, CALL, REM removed
//
// Build order:
//   1. antlr4 JvmBasicLexer.g4
//   2. antlr4 JvmBasicParser.g4

parser grammar JvmBasicParser;

options {
    tokenVocab = JvmBasicLexer;
}

// ============================================================================
// Program Structure
// ============================================================================

compilationUnit
    : importDeclaration* topLevelElement* EOF
    ;

// Allow declarations and statements to be interleaved
topLevelElement
    : declaration
    | statement
    ;

// --- Imports ---
importDeclaration
    : IMPORT qualifiedName (DOT STAR)?
    ;

qualifiedName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

// ============================================================================
// Declarations
// ============================================================================

declaration
    : classDeclaration
    | interfaceDeclaration
    | enumDeclaration
    | functionDeclaration
    | subDeclaration
    | constDeclaration
    ;

// --- Class Declaration ---
classDeclaration
    : accessModifier? ABSTRACT? CLASS IDENTIFIER
      typeParameters?
      (EXTENDS typeName)?
      (IMPLEMENTS typeNameList)?
      classMember*
      END CLASS
    ;

accessModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    ;

typeParameters
    : LT typeParameter (COMMA typeParameter)* GT
    ;

typeParameter
    : IDENTIFIER (EXTENDS typeName)?
    ;

classMember
    : fieldDeclaration
    | propertyDeclaration
    | methodDeclaration
    | constructorDeclaration
    ;

fieldDeclaration
    : accessModifier? SHARED? VAR IDENTIFIER AS typeName (EQ expression)?
    ;

propertyDeclaration
    : accessModifier? PROPERTY IDENTIFIER AS typeName
      propertyAccessor*
      END PROPERTY
    ;

propertyAccessor
    : GET statement* END GET
    | SET LPAREN IDENTIFIER AS typeName RPAREN statement* END SET
    ;

constructorDeclaration
    : accessModifier? SUB NEW parameterList?
      statement*
      END SUB
    ;

methodDeclaration
    : accessModifier? SHARED? OVERRIDE? (FUNCTION | SUB) IDENTIFIER
      typeParameters?
      parameterList?
      (AS typeName)?
      statement*
      END (FUNCTION | SUB)
    ;

// --- Interface Declaration ---
interfaceDeclaration
    : accessModifier? INTERFACE IDENTIFIER typeParameters?
      (EXTENDS typeNameList)?
      interfaceMember*
      END INTERFACE
    ;

interfaceMember
    : (FUNCTION | SUB) IDENTIFIER parameterList? (AS typeName)?
    | PROPERTY IDENTIFIER AS typeName
    ;

// --- Enum Declaration ---
enumDeclaration
    : accessModifier? ENUM IDENTIFIER
      enumMember (COMMA? enumMember)*
      END ENUM
    ;

enumMember
    : IDENTIFIER (EQ INTEGER_LITERAL)?
    ;

// --- Function/Sub Declaration (Module-level) ---
functionDeclaration
    : accessModifier? FUNCTION IDENTIFIER
      typeParameters?
      parameterList?
      AS typeName
      statement*
      END FUNCTION
    ;

subDeclaration
    : accessModifier? SUB IDENTIFIER
      parameterList?
      statement*
      END SUB
    ;

// --- Constant Declaration ---
constDeclaration
    : accessModifier? CONST IDENTIFIER AS typeName EQ expression
    ;

// ============================================================================
// Parameters and Types
// ============================================================================

parameterList
    : LPAREN (parameter (COMMA parameter)*)? RPAREN
    ;

parameter
    : (BYREF | BYVAL)? IDENTIFIER AS typeName (EQ expression)?
    ;

typeName
    : primitiveType                          # PrimitiveTypeName
    | qualifiedName typeArguments?           # QualifiedTypeName
    | typeName LBRACKET RBRACKET             # ArrayTypeName
    | typeName QUESTION                      # NullableTypeName
    | FUNCTION LPAREN typeNameList? RPAREN AS typeName  # FunctionTypeName
    ;

primitiveType
    : INTEGER
    | LONG
    | FLOAT
    | DOUBLE
    | STRING
    | BOOLEAN
    | BYTE
    | CHAR
    | OBJECT
    ;
// Note: DECIMAL and BIGINT removed - use Math.BigInteger, Math.Decimal instead

typeNameList
    : typeName (COMMA typeName)*
    ;

typeArguments
    : LT typeName (COMMA typeName)* GT
    ;

// ============================================================================
// Statements
// ============================================================================

statement
    : varStatement
    | assignmentStatement
    | ifStatement
    | selectStatement
    | forStatement
    | forEachStatement
    | whileStatement
    | doStatement
    | tryStatement
    | returnStatement
    | exitStatement
    | continueStatement
    | throwStatement
    | usingStatement
    | expressionStatement
    ;

// --- Variable Declaration (modern 'var' syntax) ---
varStatement
    : VAR IDENTIFIER AS typeName (EQ expression)?
    ;

// --- Assignment ---
assignmentStatement
    : lvalue assignmentOp expression
    ;

lvalue
    : IDENTIFIER                             # SimpleLValue
    | lvalue DOT IDENTIFIER                  # MemberLValue
    | lvalue LBRACKET expression RBRACKET    # IndexLValue
    ;

assignmentOp
    : EQ
    | PLUS_EQ
    | MINUS_EQ
    | STAR_EQ
    | SLASH_EQ
    ;

// --- If Statement ---
ifStatement
    : IF expression THEN
      statement*
      elseIfClause*
      elseClause?
      END IF
    ;

elseIfClause
    : ELSEIF expression THEN statement*
    ;

elseClause
    : ELSE statement*
    ;

// --- Select/Case Statement ---
selectStatement
    : SELECT CASE expression
      caseClause*
      caseElseClause?
      END SELECT
    ;

caseClause
    : CASE expressionList statement*
    ;

caseElseClause
    : CASE ELSE statement*
    ;

expressionList
    : expression (COMMA expression)*
    ;

// --- For Loop ---
forStatement
    : FOR IDENTIFIER EQ expression TO expression (STEP expression)?
      statement*
      NEXT IDENTIFIER?
    ;

// --- For Each Loop ---
forEachStatement
    : FOR EACH IDENTIFIER IN expression
      statement*
      NEXT IDENTIFIER?
    ;

// --- While Loop ---
whileStatement
    : WHILE expression
      statement*
      END WHILE
    ;

// --- Do Loop ---
doStatement
    : DO ((WHILE | UNTIL) expression)?
      statement*
      LOOP ((WHILE | UNTIL) expression)?
    ;

// --- Try/Catch/Finally ---
tryStatement
    : TRY
      statement*
      catchClause*
      finallyClause?
      END TRY
    ;

catchClause
    : CATCH IDENTIFIER AS typeName statement*
    ;

finallyClause
    : FINALLY statement*
    ;

// --- Control Flow ---
returnStatement
    : RETURN expression?
    ;

exitStatement
    : EXIT (FOR | WHILE | DO | SUB | FUNCTION | SELECT)
    ;

continueStatement
    : CONTINUE (FOR | WHILE | DO)?
    ;

throwStatement
    : THROW expression
    ;

// --- Using Statement (auto-dispose) ---
usingStatement
    : USING IDENTIFIER EQ expression
      statement*
      END USING
    ;

// --- Expression as Statement ---
// All I/O is done via namespaced calls: Console.WriteLine(), Console.ReadLine(), etc.
expressionStatement
    : expression
    ;

// ============================================================================
// Expressions (precedence from lowest to highest)
// ============================================================================

expression
    : conditionalOrExpression                           # BaseExpr
    | LAMBDA parameterList ARROW expression             # LambdaExpr
    | AWAIT expression                                  # AwaitExpr
    | expression QUESTION expression COLON expression   # TernaryExpr
    ;

conditionalOrExpression
    : conditionalAndExpression (OR conditionalAndExpression)*
    ;

conditionalAndExpression
    : bitwiseOrExpression (AND bitwiseOrExpression)*
    ;

bitwiseOrExpression
    : bitwiseXorExpression (PIPE bitwiseXorExpression)*
    ;

bitwiseXorExpression
    : bitwiseAndExpression (XOR bitwiseAndExpression)*
    ;

bitwiseAndExpression
    : equalityExpression (AMP equalityExpression)*
    ;

equalityExpression
    : relationalExpression ((EQ | NE) relationalExpression)*
    ;

relationalExpression
    : shiftExpression ((LT | GT | LE | GE) shiftExpression)*
    ;

shiftExpression
    : additiveExpression ((SHL | SHR) additiveExpression)*
    ;

additiveExpression
    : multiplicativeExpression ((PLUS | MINUS | AMP) multiplicativeExpression)*
    ;

multiplicativeExpression
    : powerExpression ((STAR | SLASH | BACKSLASH | MOD) powerExpression)*
    ;

// Exponentiation is right-associative: 2^3^4 = 2^(3^4) = 2^81
// Uses ANTLR4's <assoc=right> with direct left recursion
powerExpression
    : <assoc=right> powerExpression CARET powerExpression   # PowerExpr
    | unaryExpression                                        # PowerBase
    ;

unaryExpression
    : (PLUS | MINUS | NOT | TILDE) unaryExpression      # UnaryOpExpr
    | postfixExpression                                  # PostfixExprAlt
    ;

postfixExpression
    : primaryExpression postfixOp*
    ;

postfixOp
    : DOT IDENTIFIER                                     # MemberAccess
    | DOT IDENTIFIER LPAREN argumentList? RPAREN         # MethodCall
    | DOT NEW LPAREN argumentList? RPAREN                # SuperConstructorCall
    | LBRACKET expression RBRACKET                       # IndexAccess
    | LPAREN argumentList? RPAREN                        # FunctionCall
    ;

primaryExpression
    : LPAREN expression RPAREN                           # ParenExpr
    | literal                                            # LiteralExpr
    | IDENTIFIER                                         # IdentifierExpr
    | ME                                                 # MeExpr
    | THIS                                               # ThisExpr
    | MYBASE                                             # MyBaseExpr
    | SUPER                                              # SuperExpr
    | NEW typeName LPAREN argumentList? RPAREN           # NewObjectExpr
    | NEW typeName LBRACKET expression RBRACKET          # NewArrayExpr
    | TYPEOF expression                                  # TypeOfExpr
    | qualifiedName DOUBLE_COLON IDENTIFIER              # MethodRefExpr
    ;

argumentList
    : argument (COMMA argument)*
    ;

argument
    : (IDENTIFIER EQ)? expression
    ;

// ============================================================================
// Literals
// ============================================================================

literal
    : INTEGER_LITERAL                                    # IntLiteral
    | LONG_LITERAL                                       # LongLiteral
    | FLOAT_LITERAL                                      # FloatLiteral
    | DOUBLE_LITERAL                                     # DoubleLiteral
    | STRING_LITERAL                                     # StringLiteral
    | INTERPOLATED_STRING                                # InterpolatedStringLiteral
    | CHAR_LITERAL                                       # CharLiteral
    | TRUE                                               # TrueLiteral
    | FALSE                                              # FalseLiteral
    | NIL                                                # NilLiteral
    | NOTHING                                            # NothingLiteral
    ;
