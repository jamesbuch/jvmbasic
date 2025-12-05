# ANTLR4 Parse Trees Guide

## Overview

ANTLR4 automatically generates parse trees during parsing. Every rule invocation creates a node in the tree, capturing the complete structure of the parsed input. This document covers how to work with parse trees in our JVM BASIC compiler.

## Parse Tree Hierarchy

```
ParseTree (interface)
├── TerminalNode     - Leaf node containing a Token
├── ErrorNode        - Represents parse errors
└── RuleNode         - Internal node for parser rules
    └── ParserRuleContext - Generated context class for each rule
```

## Accessing Line Numbers and Positions

ANTLR automatically tracks line numbers - we don't need explicit NEWLINE tokens for this. Every token and context provides position information:

```java
// From any ParserRuleContext (rule context)
public void enterVarStatement(JvmBasicParser.VarStatementContext ctx) {
    // Get starting position
    Token start = ctx.getStart();
    int line = start.getLine();                    // 1-indexed line number
    int column = start.getCharPositionInLine();    // 0-indexed column
    int startIndex = start.getStartIndex();        // Character offset in input

    // Get ending position
    Token stop = ctx.getStop();
    int endLine = stop.getLine();
    int endColumn = stop.getCharPositionInLine();
    int stopIndex = stop.getStopIndex();

    // Get full text of the rule
    String text = ctx.getText();
}
```

## Listeners vs Visitors

ANTLR provides two patterns for traversing parse trees:

### Listeners (Event-driven)

- Automatic traversal - ANTLR walks the tree for you
- Enter/exit methods called for each rule
- Cannot return values from methods
- Good for: transformations, collecting statistics, generating output

```java
public class JvmBasicListener extends JvmBasicParserBaseListener {

    @Override
    public void enterVarStatement(JvmBasicParser.VarStatementContext ctx) {
        // Called when entering a var statement
        String varName = ctx.IDENTIFIER().getText();
        System.out.println("Declared variable: " + varName);
    }

    @Override
    public void exitVarStatement(JvmBasicParser.VarStatementContext ctx) {
        // Called when exiting the var statement
    }
}

// Usage
ParseTreeWalker walker = new ParseTreeWalker();
walker.walk(new JvmBasicListener(), parseTree);
```

### Visitors (Active traversal)

- You control traversal explicitly
- Can return values from each visit method
- Must call visit() on children manually
- Good for: interpreters, code generation, AST building

```java
public class JvmBasicVisitor extends JvmBasicParserBaseVisitor<Object> {

    @Override
    public Object visitVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();

        // Visit initializer if present
        if (ctx.expression() != null) {
            Object initValue = visit(ctx.expression());
            return new VarDecl(name, type, initValue);
        }
        return new VarDecl(name, type, null);
    }

    @Override
    public Object visitAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx) {
        Object left = visit(ctx.multiplicativeExpression(0));

        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            Object right = visit(ctx.multiplicativeExpression(i));
            Token op = ctx.getChild(2 * i - 1).getPayload(); // Get operator token
            // Combine left and right based on operator
        }
        return left;
    }
}

// Usage
JvmBasicVisitor visitor = new JvmBasicVisitor();
Object result = visitor.visit(parseTree);
```

## Generated Context Classes

For each parser rule, ANTLR generates a context class with accessors:

```antlr
// Grammar rule
varStatement
    : VAR IDENTIFIER AS typeName (EQ expression)?
    ;
```

Generates:

```java
public static class VarStatementContext extends ParserRuleContext {
    // Token accessors
    public TerminalNode VAR() { return getToken(JvmBasicParser.VAR, 0); }
    public TerminalNode IDENTIFIER() { return getToken(JvmBasicParser.IDENTIFIER, 0); }
    public TerminalNode AS() { return getToken(JvmBasicParser.AS, 0); }
    public TerminalNode EQ() { return getToken(JvmBasicParser.EQ, 0); }

    // Rule accessors
    public TypeNameContext typeName() { return getRuleContext(TypeNameContext.class, 0); }
    public ExpressionContext expression() { return getRuleContext(ExpressionContext.class, 0); }

    // Inherited from ParserRuleContext
    public Token getStart();      // First token
    public Token getStop();       // Last token
    public String getText();      // Complete text
    public int getRuleIndex();    // Rule number
}
```

## Multiple Rule References

When a rule references the same subrule multiple times:

```antlr
additiveExpression
    : multiplicativeExpression ((PLUS | MINUS) multiplicativeExpression)*
    ;
```

ANTLR generates:

```java
public static class AdditiveExpressionContext extends ParserRuleContext {
    // Returns single occurrence (first one)
    public MultiplicativeExpressionContext multiplicativeExpression() {...}

    // Returns nth occurrence (0-indexed)
    public MultiplicativeExpressionContext multiplicativeExpression(int i) {...}

    // Returns all occurrences
    public List<MultiplicativeExpressionContext> multiplicativeExpression() {...}

    // Token lists
    public List<TerminalNode> PLUS() {...}
    public TerminalNode PLUS(int i) {...}
}
```

## Rule Labels for Alternative Rules

Using `#` labels creates separate context classes:

```antlr
literal
    : INTEGER_LITERAL    # IntLiteral
    | STRING_LITERAL     # StringLiteral
    | TRUE               # TrueLiteral
    ;
```

Generates separate classes:

```java
public static class IntLiteralContext extends LiteralContext {...}
public static class StringLiteralContext extends LiteralContext {...}
public static class TrueLiteralContext extends LiteralContext {...}
```

This enables type-safe visitor methods:

```java
@Override
public Object visitIntLiteral(JvmBasicParser.IntLiteralContext ctx) {
    return Integer.parseInt(ctx.INTEGER_LITERAL().getText());
}

@Override
public Object visitStringLiteral(JvmBasicParser.StringLiteralContext ctx) {
    String text = ctx.STRING_LITERAL().getText();
    return text.substring(1, text.length() - 1); // Remove quotes
}
```

## Parse Tree Pattern Matching (Advanced)

ANTLR 4.2+ supports pattern matching on parse trees:

```java
// Find all variable declarations
ParseTreePattern p = parser.compileParseTreePattern(
    "var <IDENTIFIER> as <typeName>",
    JvmBasicParser.RULE_varStatement
);

for (ParseTreeMatch m : p.findAll(tree, "//varStatement")) {
    String varName = m.get("IDENTIFIER").getText();
    // Process match
}
```

## XPath for Node Selection

Query parse trees using XPath-like expressions:

```java
// Find all identifiers anywhere
for (ParseTree t : XPath.findAll(tree, "//IDENTIFIER", parser)) {
    System.out.println(t.getText());
}

// Find all function declarations
for (ParseTree t : XPath.findAll(tree, "//functionDeclaration", parser)) {
    JvmBasicParser.FunctionDeclarationContext func =
        (JvmBasicParser.FunctionDeclarationContext) t;
    System.out.println("Function: " + func.IDENTIFIER().getText());
}
```

XPath operators:
- `//` - all descendants
- `/` - direct children
- `!` - negation (not matching)

## Error Handling

Parse errors create ErrorNode instances in the tree:

```java
public class ErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                           Object offendingSymbol,
                           int line,
                           int charPositionInLine,
                           String msg,
                           RecognitionException e) {
        System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
    }
}

// Register error listener
parser.removeErrorListeners();
parser.addErrorListener(new ErrorListener());
```

## Complete Example: Building an AST

```java
public class AstBuilder extends JvmBasicParserBaseVisitor<AstNode> {

    @Override
    public AstNode visitCompilationUnit(JvmBasicParser.CompilationUnitContext ctx) {
        List<AstNode> statements = new ArrayList<>();
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            statements.add(visit(stmt));
        }
        return new ProgramNode(statements);
    }

    @Override
    public AstNode visitVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        TypeNode type = (TypeNode) visit(ctx.typeName());
        ExprNode init = ctx.expression() != null ?
            (ExprNode) visit(ctx.expression()) : null;

        // Capture source location
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();

        return new VarDeclNode(name, type, init, line, column);
    }

    @Override
    public AstNode visitAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx) {
        AstNode result = visit(ctx.multiplicativeExpression(0));

        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            AstNode right = visit(ctx.multiplicativeExpression(i));
            // Determine operator (PLUS or MINUS)
            ParseTree opNode = ctx.getChild(2 * i - 1);
            String op = opNode.getText();
            result = new BinaryOpNode(op, (ExprNode) result, (ExprNode) right);
        }
        return result;
    }
}
```

## Resources

- [ANTLR4 Listeners Documentation](https://github.com/antlr/antlr4/blob/master/doc/listeners.md)
- [ParseTreeVisitor API](https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTreeVisitor.html)
- [ParseTreeListener API](https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTreeListener.html)
- [ParserRuleContext API](https://www.antlr.org/api/Java/org/antlr/v4/runtime/ParserRuleContext.html)
- [Listeners and Visitors Tutorial (Strumenta)](https://tomassetti.me/listeners-and-visitors/)
