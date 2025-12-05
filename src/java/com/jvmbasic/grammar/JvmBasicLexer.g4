// JvmBasicLexer.g4 - Lexer grammar for JVM BASIC
//
// A modern BASIC dialect targeting the JVM with clean syntax.
// UTF-8 Unicode support throughout.
//
// Design principles:
//   Case-insensitive keywords
//   Newlines sent to HIDDEN channel (not required for parsing)
//   Full Unicode identifier support
//   Modern escape sequences in strings
//   Separated from parser for clean architecture
//   No legacy BASIC keywords (PRINT, INPUT, WEND, CALL, REM removed)
//   var replaces Dim for variable declarations
//   Comments: apostrophe, double-slash, block comments
//
// Dependencies (in lib/):
//   antlr-4.13.2-complete.jar
//   asm-9.9.jar
//   bcel-6.11.0.jar

lexer grammar JvmBasicLexer;

// ============================================================================
// Channels
// ============================================================================

channels { WHITESPACE_CHANNEL, COMMENTS_CHANNEL }

// ============================================================================
// Keywords - Case Insensitive
// ============================================================================

// Variable declaration (modern style - replaces Dim)
VAR         : V A R ;

// Type keywords
AS          : A S ;
INTEGER     : I N T E G E R ;
LONG        : L O N G ;
FLOAT       : F L O A T ;
DOUBLE      : D O U B L E ;
STRING      : S T R I N G ;
BOOLEAN     : B O O L E A N ;
BYTE        : B Y T E ;
CHAR        : C H A R ;
DECIMAL     : D E C I M A L ;
BIGINT      : B I G I N T ;
OBJECT      : O B J E C T ;

// Null aliases
NIL         : N I L ;
NOTHING     : N O T H I N G ;

// Boolean literals
TRUE        : T R U E ;
FALSE       : F A L S E ;

// Control flow
IF          : I F ;
THEN        : T H E N ;
ELSE        : E L S E ;
ELSEIF      : E L S E I F ;
END         : E N D ;
FOR         : F O R ;
TO          : T O ;
STEP        : S T E P ;
NEXT        : N E X T ;
WHILE       : W H I L E ;
DO          : D O ;
LOOP        : L O O P ;
UNTIL       : U N T I L ;
SELECT      : S E L E C T ;
CASE        : C A S E ;
EXIT        : E X I T ;
CONTINUE    : C O N T I N U E ;
RETURN      : R E T U R N ;
EACH        : E A C H ;
IN          : I N ;

// Functions and procedures
FUNCTION    : F U N C T I O N ;
SUB         : S U B ;
BYREF       : B Y R E F ;
BYVAL       : B Y V A L ;

// OOP keywords
CLASS       : C L A S S ;
INTERFACE   : I N T E R F A C E ;
EXTENDS     : E X T E N D S ;
IMPLEMENTS  : I M P L E M E N T S ;
NEW         : N E W ;
ME          : M E ;
MYBASE      : M Y B A S E ;
PUBLIC      : P U B L I C ;
PRIVATE     : P R I V A T E ;
PROTECTED   : P R O T E C T E D ;
SHARED      : S H A R E D ;
STATIC      : S T A T I C ;
ABSTRACT    : A B S T R A C T ;
OVERRIDE    : O V E R R I D E ;
PROPERTY    : P R O P E R T Y ;
GET         : G E T ;
SET         : S E T ;

// Exception handling
TRY         : T R Y ;
CATCH       : C A T C H ;
FINALLY     : F I N A L L Y ;
THROW       : T H R O W ;

// Other keywords
IMPORT      : I M P O R T ;
ENUM        : E N U M ;
CONST       : C O N S T ;
TYPEOF      : T Y P E O F ;
USING       : U S I N G ;
ASYNC       : A S Y N C ;
AWAIT       : A W A I T ;
LAMBDA      : L A M B D A ;

// Logical operators (keywords)
AND         : A N D ;
OR          : O R ;
XOR         : X O R ;
NOT         : N O T ;
MOD         : M O D ;

// ============================================================================
// Operators
// ============================================================================

// Arithmetic
PLUS        : '+' ;
MINUS       : '-' ;
STAR        : '*' ;
SLASH       : '/' ;
BACKSLASH   : '\\' ;   // Integer division
CARET       : '^' ;    // Power

// Comparison
EQ          : '=' ;
NE          : '<>' ;
LT          : '<' ;
GT          : '>' ;
LE          : '<=' ;
GE          : '>=' ;

// Assignment (compound)
PLUS_EQ     : '+=' ;
MINUS_EQ    : '-=' ;
STAR_EQ     : '*=' ;
SLASH_EQ    : '/=' ;

// Bitwise
AMP         : '&' ;    // Bitwise AND / String concat
PIPE        : '|' ;    // Bitwise OR
TILDE       : '~' ;    // Bitwise NOT
SHL         : '<<' ;   // Shift left
SHR         : '>>' ;   // Shift right

// Other
ARROW       : '=>' ;   // Lambda arrow
QUESTION    : '?' ;    // Nullable type suffix
COLON       : ':' ;    // Type annotation, ternary
DOUBLE_COLON: '::' ;   // Method reference

// ============================================================================
// Delimiters
// ============================================================================

LPAREN      : '(' ;
RPAREN      : ')' ;
LBRACKET    : '[' ;
RBRACKET    : ']' ;
LBRACE      : '{' ;
RBRACE      : '}' ;
COMMA       : ',' ;
DOT         : '.' ;
SEMI        : ';' ;    // Optional statement separator

// ============================================================================
// Literals
// ============================================================================

// Integer literals (decimal, hex, binary, octal)
INTEGER_LITERAL
    : DECIMAL_DIGITS
    | '0' [xX] HEX_DIGITS
    | '0' [bB] BINARY_DIGITS
    | '0' [oO] OCTAL_DIGITS
    ;

// Long literals (suffix L or l)
LONG_LITERAL
    : DECIMAL_DIGITS [Ll]
    | '0' [xX] HEX_DIGITS [Ll]
    | '0' [bB] BINARY_DIGITS [Ll]
    ;

// Float literals (suffix F or f, or decimal point)
FLOAT_LITERAL
    : DECIMAL_DIGITS '.' DECIMAL_DIGITS? EXPONENT? [Ff]?
    | '.' DECIMAL_DIGITS EXPONENT? [Ff]?
    | DECIMAL_DIGITS EXPONENT [Ff]?
    | DECIMAL_DIGITS [Ff]
    ;

// Double literals (suffix D or d, or decimal without suffix)
DOUBLE_LITERAL
    : DECIMAL_DIGITS '.' DECIMAL_DIGITS? EXPONENT? [Dd]
    | '.' DECIMAL_DIGITS EXPONENT? [Dd]
    | DECIMAL_DIGITS EXPONENT [Dd]
    | DECIMAL_DIGITS [Dd]
    ;

// String literal (double-quoted, supports escapes)
STRING_LITERAL
    : '"' STRING_CHAR* '"'
    ;

// Interpolated string literal
INTERPOLATED_STRING
    : '$"' (INTERPOLATED_CHAR | INTERPOLATION)* '"'
    ;

// Character literal (single-quoted)
CHAR_LITERAL
    : '\'' CHAR_CONTENT '\''
    ;

// ============================================================================
// Identifiers - Unicode Support
// ============================================================================

IDENTIFIER
    : LETTER (LETTER | UNICODE_DIGIT)*
    ;

// ============================================================================
// Whitespace and Comments - Hidden Channel
// ============================================================================

// Newlines go to hidden channel - ANTLR automatically tracks line numbers
NEWLINE
    : ('\r'? '\n' | '\r')+ -> channel(WHITESPACE_CHANNEL)
    ;

// Spaces and tabs
WS
    : [ \t]+ -> channel(WHITESPACE_CHANNEL)
    ;

// Line continuation (underscore at end of line)
LINE_CONTINUATION
    : '_' [ \t]* ('\r'? '\n' | '\r') -> channel(WHITESPACE_CHANNEL)
    ;

// Single-line comment (apostrophe only - no REM)
LINE_COMMENT
    : '\'' ~[\r\n]* -> channel(COMMENTS_CHANNEL)
    ;

// C-style single-line comment
LINE_COMMENT_SLASH
    : '//' ~[\r\n]* -> channel(COMMENTS_CHANNEL)
    ;

// Block comment (/* ... */)
BLOCK_COMMENT
    : '/*' .*? '*/' -> channel(COMMENTS_CHANNEL)
    ;

// ============================================================================
// Fragment Rules - Building Blocks
// ============================================================================

// Digits
fragment DECIMAL_DIGITS : [0-9] ([0-9_]* [0-9])? ;
fragment HEX_DIGITS     : [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? ;
fragment BINARY_DIGITS  : [01] ([01_]* [01])? ;
fragment OCTAL_DIGITS   : [0-7] ([0-7_]* [0-7])? ;
fragment EXPONENT       : [Ee] [+-]? DECIMAL_DIGITS ;

// String content
fragment STRING_CHAR
    : ~["\\\r\n]
    | ESCAPE_SEQUENCE
    ;

fragment CHAR_CONTENT
    : ~['\\\r\n]
    | ESCAPE_SEQUENCE
    ;

fragment INTERPOLATED_CHAR
    : ~["\\$\r\n]
    | ESCAPE_SEQUENCE
    | '$$'  // Escaped dollar sign
    ;

fragment INTERPOLATION
    : '{' ~[}]* '}'
    ;

// Escape sequences
fragment ESCAPE_SEQUENCE
    : '\\' [btnfr"'\\]           // Common escapes
    | '\\0'                       // Null
    | '\\' OCTAL_DIGIT OCTAL_DIGIT? OCTAL_DIGIT?  // Octal
    | '\\x' HEX_DIGIT HEX_DIGIT   // Hex byte
    | UNICODE_ESCAPE              // Unicode
    ;

fragment UNICODE_ESCAPE
    : '\\u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    | '\\U' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

fragment OCTAL_DIGIT : [0-7] ;
fragment HEX_DIGIT   : [0-9a-fA-F] ;

// Unicode identifier support
fragment LETTER
    : UNICODE_LETTER
    | '_'
    ;

// Unicode character classes for full UTF-8 support
fragment UNICODE_LETTER : [\p{L}] ;    // Any Unicode letter
fragment UNICODE_DIGIT  : [\p{Nd}] ;   // Any Unicode decimal digit

// ============================================================================
// Case-Insensitive Letter Fragments
// ============================================================================

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
