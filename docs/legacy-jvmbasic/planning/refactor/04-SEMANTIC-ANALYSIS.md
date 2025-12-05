# Semantic Analysis

## Overview

Semantic analysis is performed in multiple passes over the AST to build symbol tables, resolve types, and validate the program.

## Analysis Passes

### Pass 1: Declaration Collection

Collect all top-level declarations (classes, interfaces, functions, subs, enums) into a global symbol table. This enables forward references.

```java
public class DeclarationCollector extends AstVisitor<Void> {

    private final SymbolTable globalScope = new SymbolTable();

    @Override
    public Void visit(ClassDecl classDecl) {
        ClassSymbol symbol = new ClassSymbol(
            classDecl.name(),
            classDecl.access(),
            classDecl.isAbstract()
        );
        globalScope.define(classDecl.name(), symbol);
        return null;
    }

    @Override
    public Void visit(FunctionDecl funcDecl) {
        FunctionSymbol symbol = new FunctionSymbol(
            funcDecl.name(),
            funcDecl.parameters().stream()
                .map(p -> p.type())
                .toList(),
            funcDecl.returnType()
        );
        globalScope.define(funcDecl.name(), symbol);
        return null;
    }

    // Similar for InterfaceDecl, SubDecl, EnumDecl
}
```

### Pass 2: Type Resolution

Resolve all type references to their actual type definitions. This handles:
- Qualified names (Package.ClassName)
- Generic type arguments
- Import resolution

```java
public class TypeResolver extends AstVisitor<Void> {

    private final SymbolTable globalScope;
    private final List<ImportDecl> imports;

    @Override
    public Void visit(NamedType type) {
        // Try to resolve the type name
        Symbol symbol = resolveTypeName(type.name());
        if (symbol == null) {
            error("Unknown type: " + type.name(), type.location());
        }
        type.setResolvedSymbol(symbol);

        // Resolve type arguments
        for (TypeNode arg : type.typeArguments()) {
            visit(arg);
        }
        return null;
    }

    private Symbol resolveTypeName(String name) {
        // 1. Check global scope
        Symbol symbol = globalScope.lookup(name);
        if (symbol != null) return symbol;

        // 2. Check imports
        for (ImportDecl imp : imports) {
            if (imp.isWildcard()) {
                // Try package.name
                symbol = globalScope.lookup(imp.qualifiedName() + "." + name);
                if (symbol != null) return symbol;
            } else if (imp.qualifiedName().endsWith("." + name)) {
                return globalScope.lookup(imp.qualifiedName());
            }
        }

        // 3. Check java.lang (implicit import)
        return globalScope.lookup("java.lang." + name);
    }
}
```

### Pass 3: Member Resolution

For each class/interface, resolve inherited members and build the complete member table.

```java
public class MemberResolver extends AstVisitor<Void> {

    @Override
    public Void visit(ClassDecl classDecl) {
        ClassSymbol symbol = (ClassSymbol) globalScope.lookup(classDecl.name());

        // Collect inherited members
        if (classDecl.superClass().isPresent()) {
            ClassSymbol superSymbol = resolveClass(classDecl.superClass().get());
            symbol.inheritFrom(superSymbol);
        }

        // Collect interface methods
        for (TypeNode iface : classDecl.interfaces()) {
            InterfaceSymbol ifaceSymbol = resolveInterface(iface);
            symbol.implementInterface(ifaceSymbol);
        }

        // Add own members
        for (ClassMember member : classDecl.members()) {
            switch (member) {
                case FieldDecl field ->
                    symbol.addField(new FieldSymbol(field));
                case PropertyDecl prop ->
                    symbol.addProperty(new PropertySymbol(prop));
                case MethodDecl method ->
                    symbol.addMethod(new MethodSymbol(method));
                case ConstructorDecl ctor ->
                    symbol.addConstructor(new ConstructorSymbol(ctor));
            }
        }

        // Validate: abstract methods are implemented
        validateAbstractMethods(symbol);

        return null;
    }
}
```

### Pass 4: Type Checking

Check that all expressions and statements are type-correct.

```java
public class TypeChecker extends AstVisitor<Type> {

    private SymbolTable currentScope;
    private Type currentReturnType;  // For checking return statements

    @Override
    public Type visit(DimStmt dim) {
        Type declaredType = resolveType(dim.type());

        if (dim.initializer().isPresent()) {
            Type initType = visit(dim.initializer().get());
            if (!isAssignable(declaredType, initType)) {
                error("Cannot assign " + initType + " to " + declaredType, dim.location());
            }
        }

        currentScope.define(dim.name(), new VariableSymbol(dim.name(), declaredType));
        return null;
    }

    @Override
    public Type visit(IfStmt ifStmt) {
        Type condType = visit(ifStmt.condition());
        if (condType != PrimitiveType.BOOLEAN) {
            error("If condition must be Boolean, got " + condType, ifStmt.location());
        }

        // Enter new scope for then-branch
        currentScope = new SymbolTable(currentScope);
        for (Statement stmt : ifStmt.thenBranch()) {
            visit(stmt);
        }
        currentScope = currentScope.parent();

        // Check elseif clauses
        for (ElseIfClause clause : ifStmt.elseIfClauses()) {
            Type elseIfCond = visit(clause.condition());
            if (elseIfCond != PrimitiveType.BOOLEAN) {
                error("ElseIf condition must be Boolean", clause.location());
            }
            currentScope = new SymbolTable(currentScope);
            for (Statement stmt : clause.body()) {
                visit(stmt);
            }
            currentScope = currentScope.parent();
        }

        // Check else branch
        if (ifStmt.elseBranch().isPresent()) {
            currentScope = new SymbolTable(currentScope);
            for (Statement stmt : ifStmt.elseBranch().get()) {
                visit(stmt);
            }
            currentScope = currentScope.parent();
        }

        return null;
    }

    @Override
    public Type visit(ReturnStmt ret) {
        if (ret.value().isEmpty()) {
            if (currentReturnType != null && currentReturnType != PrimitiveType.VOID) {
                error("Function must return a value", ret.location());
            }
        } else {
            Type valueType = visit(ret.value().get());
            if (currentReturnType == null || currentReturnType == PrimitiveType.VOID) {
                error("Sub cannot return a value", ret.location());
            } else if (!isAssignable(currentReturnType, valueType)) {
                error("Cannot return " + valueType + " from function returning " + currentReturnType,
                      ret.location());
            }
        }
        return null;
    }

    @Override
    public Type visit(BinaryExpr bin) {
        Type left = visit(bin.left());
        Type right = visit(bin.right());

        return switch (bin.operator()) {
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO -> {
                if (!isNumeric(left) || !isNumeric(right)) {
                    error("Arithmetic operators require numeric types", bin.location());
                }
                yield promoteNumeric(left, right);
            }

            case EQUAL, NOT_EQUAL -> {
                if (!isComparable(left, right)) {
                    error("Cannot compare " + left + " with " + right, bin.location());
                }
                yield PrimitiveType.BOOLEAN;
            }

            case LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL -> {
                if (!isOrdered(left, right)) {
                    error("Cannot order " + left + " with " + right, bin.location());
                }
                yield PrimitiveType.BOOLEAN;
            }

            case AND, OR, XOR -> {
                if (left != PrimitiveType.BOOLEAN || right != PrimitiveType.BOOLEAN) {
                    error("Logical operators require Boolean operands", bin.location());
                }
                yield PrimitiveType.BOOLEAN;
            }

            case CONCAT -> PrimitiveType.STRING;
        };
    }
}
```

### Pass 5: Flow Analysis

Check for:
- Unreachable code after return/throw
- Variables used before assignment
- All code paths return a value

```java
public class FlowAnalyzer extends AstVisitor<FlowState> {

    @Override
    public FlowState visit(FunctionDecl func) {
        FlowState state = new FlowState();

        // Track assigned variables
        for (Parameter param : func.parameters()) {
            state.markAssigned(param.name());
        }

        for (Statement stmt : func.body()) {
            state = visit(stmt, state);
            if (state.isTerminated()) {
                // Warn about unreachable code
                break;
            }
        }

        // Check all paths return
        if (func.returnType() != PrimitiveType.VOID && !state.isTerminated()) {
            error("Not all code paths return a value", func.location());
        }

        return state;
    }

    @Override
    public FlowState visit(IfStmt ifStmt, FlowState state) {
        FlowState thenState = state.copy();
        for (Statement stmt : ifStmt.thenBranch()) {
            thenState = visit(stmt, thenState);
        }

        FlowState elseState = state.copy();
        if (ifStmt.elseBranch().isPresent()) {
            for (Statement stmt : ifStmt.elseBranch().get()) {
                elseState = visit(stmt, elseState);
            }
        }

        // Merge states: only definitely assigned in both branches
        return FlowState.merge(thenState, elseState);
    }
}
```

## Symbol Tables

```java
public class SymbolTable {
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols = new HashMap<>();

    public SymbolTable() {
        this.parent = null;
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public void define(String name, Symbol symbol) {
        if (symbols.containsKey(name)) {
            throw new SemanticError("Symbol already defined: " + name);
        }
        symbols.put(name, symbol);
    }

    public Symbol lookup(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) return symbol;
        if (parent != null) return parent.lookup(name);
        return null;
    }

    public Symbol lookupLocal(String name) {
        return symbols.get(name);
    }

    public SymbolTable parent() {
        return parent;
    }
}

public sealed interface Symbol permits
    ClassSymbol, InterfaceSymbol, EnumSymbol,
    FunctionSymbol, SubSymbol,
    FieldSymbol, PropertySymbol, MethodSymbol, ConstructorSymbol,
    VariableSymbol, ParameterSymbol {}
```

## Error Reporting

```java
public class SemanticError extends RuntimeException {
    private final SourceLocation location;

    public SemanticError(String message, SourceLocation location) {
        super(message);
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%d: error: %s",
            location.file(),
            location.line(),
            location.column(),
            getMessage());
    }
}

public class ErrorCollector {
    private final List<SemanticError> errors = new ArrayList<>();
    private final List<SemanticError> warnings = new ArrayList<>();

    public void error(String message, SourceLocation location) {
        errors.add(new SemanticError(message, location));
    }

    public void warning(String message, SourceLocation location) {
        warnings.add(new SemanticError(message, location));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void report(PrintStream out) {
        for (SemanticError error : errors) {
            out.println(error);
        }
        for (SemanticError warning : warnings) {
            out.println("warning: " + warning);
        }
    }
}
```
