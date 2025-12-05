# ANTLR4 Grammar Specification

## File: ObjectBasic.g4

```antlr
grammar ObjectBasic;

// ============================================================================
// Parser Rules
// ============================================================================

compilationUnit
    : importDeclaration* declaration* statement* EOF
    ;

// --- Imports ---
importDeclaration
    : IMPORT qualifiedName (DOT STAR)? NEWLINE
    ;

qualifiedName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

// --- Declarations ---
declaration
    : classDeclaration
    | interfaceDeclaration
    | functionDeclaration
    | subDeclaration
    | enumDeclaration
    ;

// --- Class Declaration ---
classDeclaration
    : accessModifier? ABSTRACT? CLASS IDENTIFIER
      typeParameters?
      (EXTENDS typeName)?
      (IMPLEMENTS typeNameList)?
      NEWLINE
      classMember*
      END CLASS NEWLINE
    ;

accessModifier
    : PUBLIC | PRIVATE | PROTECTED
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
    : accessModifier? SHARED? DIM IDENTIFIER AS typeName (ASSIGN expression)? NEWLINE
    ;

propertyDeclaration
    : accessModifier? PROPERTY IDENTIFIER AS typeName NEWLINE
      propertyAccessor*
      END PROPERTY NEWLINE
    ;

propertyAccessor
    : GET NEWLINE statement* END GET NEWLINE
    | SET LPAREN IDENTIFIER AS typeName RPAREN NEWLINE statement* END SET NEWLINE
    ;

constructorDeclaration
    : accessModifier? SUB NEW parameterList? NEWLINE
      statement*
      END SUB NEWLINE
    ;

methodDeclaration
    : accessModifier? SHARED? (FUNCTION | SUB) IDENTIFIER
      typeParameters?
      parameterList?
      (AS typeName)?
      NEWLINE
      statement*
      END (FUNCTION | SUB) NEWLINE
    ;

// --- Interface Declaration ---
interfaceDeclaration
    : accessModifier? INTERFACE IDENTIFIER typeParameters?
      (EXTENDS typeNameList)?
      NEWLINE
      interfaceMember*
      END INTERFACE NEWLINE
    ;

interfaceMember
    : (FUNCTION | SUB) IDENTIFIER parameterList? (AS typeName)? NEWLINE
    | PROPERTY IDENTIFIER AS typeName NEWLINE
    ;

// --- Function/Sub Declaration (Module-level) ---
functionDeclaration
    : accessModifier? FUNCTION IDENTIFIER
      typeParameters?
      parameterList?
      AS typeName
      NEWLINE
      statement*
      END FUNCTION NEWLINE
    ;

subDeclaration
    : accessModifier? SUB IDENTIFIER
      parameterList?
      NEWLINE
      statement*
      END SUB NEWLINE
    ;

// --- Enum Declaration ---
enumDeclaration
    : accessModifier? ENUM IDENTIFIER NEWLINE
      enumMember (COMMA? NEWLINE enumMember)*
      END ENUM NEWLINE
    ;

enumMember
    : IDENTIFIER (ASSIGN INTEGER_LITERAL)?
    ;

// --- Parameters ---
parameterList
    : LPAREN (parameter (COMMA parameter)*)? RPAREN
    ;

parameter
    : BYREF? IDENTIFIER AS typeName (ASSIGN expression)?
    ;

// --- Type Names ---
typeName
    : primitiveType
    | qualifiedName typeArguments?
    | arrayType
    | nullableType
    | functionType
    ;

primitiveType
    : INTEGER | LONG | FLOAT | DOUBLE | STRING | BOOLEAN | BYTE | CHAR
    ;

arrayType
    : typeName LBRACKET RBRACKET
    ;

nullableType
    : typeName QUESTION
    ;

functionType
    : FUNCTION LPAREN typeNameList? RPAREN AS typeName
    ;

typeNameList
    : typeName (COMMA typeName)*
    ;

typeArguments
    : LT typeName (COMMA typeName)* GT
    ;

// --- Statements ---
statement
    : dimStatement
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
    | expressionStatement
    | usingStatement
    ;

dimStatement
    : DIM IDENTIFIER AS typeName (ASSIGN expression)? NEWLINE
    ;

assignmentStatement
    : lvalue assignmentOperator expression NEWLINE
    ;

lvalue
    : IDENTIFIER
    | memberAccess
    | indexAccess
    ;

assignmentOperator
    : ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | STAR_ASSIGN | SLASH_ASSIGN
    ;

ifStatement
    : IF expression THEN NEWLINE
      statement*
      elseIfClause*
      elseClause?
      END IF NEWLINE
    ;

elseIfClause
    : ELSEIF expression THEN NEWLINE statement*
    ;

elseClause
    : ELSE NEWLINE statement*
    ;

selectStatement
    : SELECT CASE expression NEWLINE
      caseClause*
      caseElseClause?
      END SELECT NEWLINE
    ;

caseClause
    : CASE expressionList NEWLINE statement*
    ;

caseElseClause
    : CASE ELSE NEWLINE statement*
    ;

expressionList
    : expression (COMMA expression)*
    ;

forStatement
    : FOR IDENTIFIER ASSIGN expression TO expression (STEP expression)? NEWLINE
      statement*
      NEXT IDENTIFIER? NEWLINE
    ;

forEachStatement
    : FOR EACH IDENTIFIER IN expression NEWLINE
      statement*
      NEXT IDENTIFIER? NEWLINE
    ;

whileStatement
    : WHILE expression NEWLINE
      statement*
      END WHILE NEWLINE
    ;

doStatement
    : DO (WHILE | UNTIL)? expression? NEWLINE
      statement*
      LOOP (WHILE | UNTIL)? expression? NEWLINE
    ;

tryStatement
    : TRY NEWLINE
      statement*
      catchClause*
      finallyClause?
      END TRY NEWLINE
    ;

catchClause
    : CATCH IDENTIFIER AS typeName NEWLINE statement*
    ;

finallyClause
    : FINALLY NEWLINE statement*
    ;

returnStatement
    : RETURN expression? NEWLINE
    ;

exitStatement
    : EXIT (FOR | WHILE | DO | SUB | FUNCTION | SELECT) NEWLINE
    ;

continueStatement
    : CONTINUE (FOR | WHILE | DO) NEWLINE
    ;

throwStatement
    : THROW expression NEWLINE
    ;

usingStatement
    : USING IDENTIFIER ASSIGN expression NEWLINE
      statement*
      END USING NEWLINE
    ;

expressionStatement
    : expression NEWLINE
    ;

// --- Expressions ---
expression
    : primaryExpression                                          # PrimaryExpr
    | expression DOT IDENTIFIER                                  # MemberAccessExpr
    | expression DOT IDENTIFIER LPAREN argumentList? RPAREN      # MethodCallExpr
    | expression LBRACKET expression RBRACKET                    # IndexAccessExpr
    | expression LPAREN argumentList? RPAREN                     # CallExpr
    | NEW typeName LPAREN argumentList? RPAREN                   # NewExpr
    | NEW typeName LBRACKET expression RBRACKET                  # NewArrayExpr
    | TYPEOF expression                                          # TypeOfExpr
    | CAST LT typeName GT LPAREN expression RPAREN               # CastExpr
    | (PLUS | MINUS | NOT | BITNOT) expression                   # UnaryExpr
    | expression (STAR | SLASH | MOD | BACKSLASH) expression     # MultiplicativeExpr
    | expression (PLUS | MINUS) expression                       # AdditiveExpr
    | expression (SHL | SHR) expression                          # ShiftExpr
    | expression (LT | GT | LE | GE) expression                  # RelationalExpr
    | expression (EQ | NE) expression                            # EqualityExpr
    | expression BITAND expression                               # BitAndExpr
    | expression BITXOR expression                               # BitXorExpr
    | expression BITOR expression                                # BitOrExpr
    | expression AND expression                                  # LogicalAndExpr
    | expression OR expression                                   # LogicalOrExpr
    | expression QUESTION expression COLON expression            # TernaryExpr
    | LAMBDA parameterList ARROW expression                      # LambdaExpr
    | AWAIT expression                                           # AwaitExpr
    ;

primaryExpression
    : LPAREN expression RPAREN                                   # ParenExpr
    | literal                                                    # LiteralExpr
    | IDENTIFIER                                                 # IdentifierExpr
    | ME                                                         # MeExpr
    | MYBASE                                                     # MyBaseExpr
    ;

memberAccess
    : expression DOT IDENTIFIER
    ;

indexAccess
    : expression LBRACKET expression RBRACKET
    ;

argumentList
    : argument (COMMA argument)*
    ;

argument
    : (IDENTIFIER ASSIGN)? expression
    ;

literal
    : INTEGER_LITERAL
    | LONG_LITERAL
    | FLOAT_LITERAL
    | DOUBLE_LITERAL
    | STRING_LITERAL
    | CHAR_LITERAL
    | TRUE
    | FALSE
    | NOTHING
    ;

// ============================================================================
// Lexer Rules
// ============================================================================

// --- Keywords ---
ABSTRACT    : A B S T R A C T ;
AND         : A N D ;
AS          : A S ;
ASYNC       : A S Y N C ;
AWAIT       : A W A I T ;
BOOLEAN     : B O O L E A N ;
BYREF       : B Y R E F ;
BYTE        : B Y T E ;
CASE        : C A S E ;
CAST        : C A S T ;
CATCH       : C A T C H ;
CHAR        : C H A R ;
CLASS       : C L A S S ;
CONTINUE    : C O N T I N U E ;
DIM         : D I M ;
DO          : D O ;
DOUBLE      : D O U B L E ;
EACH        : E A C H ;
ELSE        : E L S E ;
ELSEIF      : E L S E I F ;
END         : E N D ;
ENUM        : E N U M ;
EXIT        : E X I T ;
EXTENDS     : E X T E N D S ;
FALSE       : F A L S E ;
FINALLY     : F I N A L L Y ;
FLOAT       : F L O A T ;
FOR         : F O R ;
FUNCTION    : F U N C T I O N ;
GET         : G E T ;
IF          : I F ;
IMPLEMENTS  : I M P L E M E N T S ;
IMPORT      : I M P O R T ;
IN          : I N ;
INTEGER     : I N T E G E R ;
INTERFACE   : I N T E R F A C E ;
LAMBDA      : L A M B D A ;
LONG        : L O N G ;
LOOP        : L O O P ;
ME          : M E ;
MOD         : M O D ;
MYBASE      : M Y B A S E ;
NEW         : N E W ;
NEXT        : N E X T ;
NOT         : N O T ;
NOTHING     : N O T H I N G ;
OR          : O R ;
PRIVATE     : P R I V A T E ;
PROPERTY    : P R O P E R T Y ;
PROTECTED   : P R O T E C T E D ;
PUBLIC      : P U B L I C ;
RETURN      : R E T U R N ;
SELECT      : S E L E C T ;
SET         : S E T ;
SHARED      : S H A R E D ;
STEP        : S T E P ;
STRING      : S T R I N G ;
SUB         : S U B ;
THEN        : T H E N ;
THROW       : T H R O W ;
TO          : T O ;
TRUE        : T R U E ;
TRY         : T R Y ;
TYPEOF      : T Y P E O F ;
UNTIL       : U N T I L ;
USING       : U S I N G ;
WHILE       : W H I L E ;
XOR         : X O R ;

// --- Operators ---
PLUS        : '+' ;
MINUS       : '-' ;
STAR        : '*' ;
SLASH       : '/' ;
BACKSLASH   : '\\' ;
EQ          : '=' ;
NE          : '<>' ;
LT          : '<' ;
GT          : '>' ;
LE          : '<=' ;
GE          : '>=' ;
ASSIGN      : '=' ;
PLUS_ASSIGN : '+=' ;
MINUS_ASSIGN: '-=' ;
STAR_ASSIGN : '*=' ;
SLASH_ASSIGN: '/=' ;
BITAND      : '&' ;
BITOR       : '|' ;
BITXOR      : '^' ;
BITNOT      : '~' ;
SHL         : '<<' ;
SHR         : '>>' ;
ARROW       : '=>' ;

// --- Delimiters ---
LPAREN      : '(' ;
RPAREN      : ')' ;
LBRACKET    : '[' ;
RBRACKET    : ']' ;
LBRACE      : '{' ;
RBRACE      : '}' ;
COMMA       : ',' ;
DOT         : '.' ;
COLON       : ':' ;
QUESTION    : '?' ;

// --- Literals ---
INTEGER_LITERAL
    : DIGIT+
    | '0x' HEX_DIGIT+
    | '0b' [01]+
    ;

LONG_LITERAL
    : DIGIT+ [Ll]
    ;

FLOAT_LITERAL
    : DIGIT+ '.' DIGIT* EXPONENT? [Ff]?
    | '.' DIGIT+ EXPONENT? [Ff]?
    | DIGIT+ EXPONENT [Ff]?
    | DIGIT+ [Ff]
    ;

DOUBLE_LITERAL
    : DIGIT+ '.' DIGIT* EXPONENT? [Dd]
    | '.' DIGIT+ EXPONENT? [Dd]
    | DIGIT+ EXPONENT [Dd]
    | DIGIT+ [Dd]
    ;

STRING_LITERAL
    : '"' STRING_CHAR* '"'
    ;

CHAR_LITERAL
    : '\'' CHAR_CHAR '\''
    ;

fragment STRING_CHAR
    : ~["\\\r\n]
    | ESCAPE_SEQ
    ;

fragment CHAR_CHAR
    : ~['\\\r\n]
    | ESCAPE_SEQ
    ;

fragment ESCAPE_SEQ
    : '\\' [btnfr"'\\]
    | '\\u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

fragment EXPONENT
    : [Ee] [+-]? DIGIT+
    ;

fragment DIGIT
    : [0-9]
    ;

fragment HEX_DIGIT
    : [0-9a-fA-F]
    ;

// --- Identifiers ---
IDENTIFIER
    : LETTER (LETTER | DIGIT)*
    ;

fragment LETTER
    : [a-zA-Z_]
    ;

// --- Case Insensitivity Fragments ---
fragment A : [Aa] ;
fragment B : [Bb] ;
fragment C : [Cc] ;
fragment D : [Dd] ;
fragment E : [Ee] ;
fragment F : [Ff] ;
fragment G : [Gg] ;
fragment H : [Hh] ;
fragment I : [Ii] ;
fragment J : [Jj] ;
fragment K : [Kk] ;
fragment L : [Ll] ;
fragment M : [Mm] ;
fragment N : [Nn] ;
fragment O : [Oo] ;
fragment P : [Pp] ;
fragment Q : [Qq] ;
fragment R : [Rr] ;
fragment S : [Ss] ;
fragment T : [Tt] ;
fragment U : [Uu] ;
fragment V : [Vv] ;
fragment W : [Ww] ;
fragment X : [Xx] ;
fragment Y : [Yy] ;
fragment Z : [Zz] ;

// --- Whitespace and Comments ---
NEWLINE
    : ('\r'? '\n' | '\r')+
    ;

WS
    : [ \t]+ -> skip
    ;

LINE_CONTINUATION
    : '_' [ \t]* ('\r'? '\n' | '\r') -> skip
    ;

COMMENT
    : '\'' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;
```

## Key Grammar Features

### 1. Case Insensitivity
Keywords use fragment rules (e.g., `A : [Aa]`) to match both cases.

### 2. Expression Precedence
Operators are ordered by precedence in the grammar (multiplicative before additive, etc.).

### 3. Statement Termination
All statements end with NEWLINE, allowing multi-line statements via `_` continuation.

### 4. Type System
- Primitive types: Integer, Long, Float, Double, String, Boolean, Byte, Char
- Array types: `Type[]`
- Nullable types: `Type?`
- Generic types: `List<T>`, `Map<K, V>`
- Function types: `Function(Int, String) As Boolean`

### 5. Object-Oriented Features
- Classes with inheritance and interfaces
- Properties with get/set accessors
- Constructors with `Sub New`
- Static members with `Shared` keyword
- Access modifiers: Public, Private, Protected
