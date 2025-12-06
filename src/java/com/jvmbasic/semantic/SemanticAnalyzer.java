package com.jvmbasic.semantic;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.decl.*;
import com.jvmbasic.ir.expr.*;
import com.jvmbasic.ir.stmt.*;

import java.util.*;

/**
 * Semantic Analyzer for JVM BASIC 2.0
 *
 * Performs semantic analysis on the IR (Intermediate Representation) tree,
 * validating types, checking variable declarations, and detecting errors
 * before code generation.
 *
 * Analysis phases:
 * 1. Symbol collection - gather all declarations
 * 2. Type inference - determine types of expressions
 * 3. Type checking - validate assignments and operations
 * 4. Reference checking - verify all references are defined
 */
public class SemanticAnalyzer {

    private final List<SemanticError> errors = new ArrayList<>();
    private final List<SemanticWarning> warnings = new ArrayList<>();

    // Symbol tables - scoped for nested contexts
    private final Deque<SymbolTable> scopeStack = new ArrayDeque<>();

    // Known namespaces and their methods
    private static final Set<String> KNOWN_NAMESPACES = Set.of(
        "Console", "Math", "Str", "File", "Regex"
    );

    // Current function being analyzed (for return type checking)
    private IRType currentFunctionReturnType = null;
    private String currentFunctionName = null;

    public SemanticAnalyzer() {
        // Initialize with global scope
        scopeStack.push(new SymbolTable("global"));
    }

    /**
     * Analyze a compilation unit and return analysis results.
     */
    public SemanticResult analyze(IRCompilationUnit unit) {
        // Reset state
        errors.clear();
        warnings.clear();
        scopeStack.clear();
        scopeStack.push(new SymbolTable("global"));

        // Phase 1: Collect all top-level declarations
        collectDeclarations(unit);

        // Phase 2: Analyze function bodies
        for (IRFunction func : unit.getFunctions()) {
            analyzeFunction(func);
        }

        // Phase 3: Analyze top-level statements
        for (IRStatement stmt : unit.getStatements()) {
            analyzeStatement(stmt);
        }

        // Phase 4: Analyze classes
        for (IRClass cls : unit.getClasses()) {
            analyzeClass(cls);
        }

        return new SemanticResult(errors, warnings);
    }

    // ========================================================================
    // Phase 1: Declaration Collection
    // ========================================================================

    private void collectDeclarations(IRCompilationUnit unit) {
        // Collect functions
        for (IRFunction func : unit.getFunctions()) {
            String name = func.getName();
            if (currentScope().hasSymbol(name)) {
                error(func.getLine(), func.getColumn(),
                    "Function '" + name + "' is already defined");
            } else {
                currentScope().defineFunction(name, func.getReturnType(),
                    func.getParameters().stream()
                        .map(IRParameter::getType)
                        .toList());
            }
        }

        // Collect classes
        for (IRClass cls : unit.getClasses()) {
            String name = cls.getName();
            if (currentScope().hasSymbol(name)) {
                error(cls.getLine(), cls.getColumn(),
                    "Class '" + name + "' is already defined");
            } else {
                currentScope().defineClass(name, cls);
            }
        }
    }

    // ========================================================================
    // Phase 2: Function Analysis
    // ========================================================================

    private void analyzeFunction(IRFunction func) {
        // Enter new scope for function
        pushScope("function:" + func.getName());
        currentFunctionReturnType = func.getReturnType();
        currentFunctionName = func.getName();

        // Add parameters to scope
        for (IRParameter param : func.getParameters()) {
            if (currentScope().hasLocalSymbol(param.getName())) {
                error(param.getLine(), param.getColumn(),
                    "Duplicate parameter name: " + param.getName());
            } else {
                currentScope().defineVariable(param.getName(), param.getType(), true);
            }
        }

        // Analyze body statements
        boolean hasReturn = false;
        for (IRStatement stmt : func.getBody()) {
            analyzeStatement(stmt);
            if (stmt instanceof IRReturn) {
                hasReturn = true;
            }
        }

        // Check return statement for non-void functions
        IRType returnType = func.getReturnType();
        if (returnType != null && !returnType.equals(IRType.Primitive.VOID) && !hasReturn) {
            warning(func.getLine(), func.getColumn(),
                "Function '" + func.getName() + "' may not return a value on all paths");
        }

        currentFunctionReturnType = null;
        currentFunctionName = null;
        popScope();
    }

    // ========================================================================
    // Phase 3: Statement Analysis
    // ========================================================================

    private void analyzeStatement(IRStatement stmt) {
        if (stmt instanceof IRVarDecl varDecl) {
            analyzeVarDecl(varDecl);
        } else if (stmt instanceof IRAssignment assign) {
            analyzeAssignment(assign);
        } else if (stmt instanceof IRIf ifStmt) {
            analyzeIf(ifStmt);
        } else if (stmt instanceof IRWhile whileStmt) {
            analyzeWhile(whileStmt);
        } else if (stmt instanceof IRFor forStmt) {
            analyzeFor(forStmt);
        } else if (stmt instanceof IRForEach forEachStmt) {
            analyzeForEach(forEachStmt);
        } else if (stmt instanceof IRReturn returnStmt) {
            analyzeReturn(returnStmt);
        } else if (stmt instanceof IRExprStmt exprStmt) {
            analyzeExpression(exprStmt.getExpression());
        } else if (stmt instanceof IRTry tryStmt) {
            analyzeTry(tryStmt);
        } else if (stmt instanceof IRThrow throwStmt) {
            analyzeThrow(throwStmt);
        } else if (stmt instanceof IRBlock block) {
            pushScope("block");
            for (IRStatement s : block.getStatements()) {
                analyzeStatement(s);
            }
            popScope();
        }
    }

    private void analyzeVarDecl(IRVarDecl varDecl) {
        String name = varDecl.getName();
        IRType declaredType = varDecl.getType();

        // Check for duplicate declaration in current scope
        if (currentScope().hasLocalSymbol(name)) {
            error(varDecl.getLine(), varDecl.getColumn(),
                "Variable '" + name + "' is already defined in this scope");
            return;
        }

        // Analyze initializer if present
        if (varDecl.getInitializer() != null) {
            IRType initType = analyzeExpression(varDecl.getInitializer());

            // Type compatibility check
            if (!isAssignableFrom(declaredType, initType)) {
                error(varDecl.getLine(), varDecl.getColumn(),
                    "Cannot assign " + typeToString(initType) + " to " + typeToString(declaredType));
            }
        }

        // Add to symbol table
        currentScope().defineVariable(name, declaredType, varDecl.getInitializer() != null);
    }

    private void analyzeAssignment(IRAssignment assign) {
        IRExpression target = assign.getTarget();
        IRExpression value = assign.getValue();

        // Get target type
        IRType targetType = analyzeExpression(target);
        IRType valueType = analyzeExpression(value);

        // Check assignability
        if (targetType != null && valueType != null && !isAssignableFrom(targetType, valueType)) {
            error(assign.getLine(), assign.getColumn(),
                "Cannot assign " + typeToString(valueType) + " to " + typeToString(targetType));
        }

        // Mark variable as initialized
        if (target instanceof IRIdentifier id) {
            Symbol sym = lookupSymbol(id.name());
            if (sym != null) {
                sym.setInitialized(true);
            }
        }
    }

    private void analyzeIf(IRIf ifStmt) {
        // Check condition is boolean
        IRType condType = analyzeExpression(ifStmt.getCondition());
        if (!isBooleanType(condType)) {
            error(ifStmt.getLine(), ifStmt.getColumn(),
                "If condition must be Boolean, got " + typeToString(condType));
        }

        // Analyze then branch
        pushScope("if-then");
        for (IRStatement stmt : ifStmt.getThenBranch()) {
            analyzeStatement(stmt);
        }
        popScope();

        // Analyze else-if branches
        for (var elseIf : ifStmt.getElseIfs()) {
            IRType elseIfCondType = analyzeExpression(elseIf.condition());
            if (!isBooleanType(elseIfCondType)) {
                error(ifStmt.getLine(), ifStmt.getColumn(),
                    "ElseIf condition must be Boolean");
            }
            pushScope("elseif");
            for (IRStatement stmt : elseIf.body()) {
                analyzeStatement(stmt);
            }
            popScope();
        }

        // Analyze else branch
        if (ifStmt.getElseBranch() != null) {
            pushScope("else");
            for (IRStatement stmt : ifStmt.getElseBranch()) {
                analyzeStatement(stmt);
            }
            popScope();
        }
    }

    private void analyzeWhile(IRWhile whileStmt) {
        IRType condType = analyzeExpression(whileStmt.getCondition());
        if (!isBooleanType(condType)) {
            error(whileStmt.getLine(), whileStmt.getColumn(),
                "While condition must be Boolean, got " + typeToString(condType));
        }

        pushScope("while");
        for (IRStatement stmt : whileStmt.getBody()) {
            analyzeStatement(stmt);
        }
        popScope();
    }

    private void analyzeFor(IRFor forStmt) {
        pushScope("for");

        // Define loop variable
        currentScope().defineVariable(forStmt.getVariable(), IRType.Primitive.INT, true);

        // Analyze start and end expressions
        IRType startType = analyzeExpression(forStmt.getStart());
        IRType endType = analyzeExpression(forStmt.getEnd());

        if (!isNumericType(startType)) {
            error(forStmt.getLine(), forStmt.getColumn(),
                "For loop start value must be numeric, got " + typeToString(startType));
        }
        if (!isNumericType(endType)) {
            error(forStmt.getLine(), forStmt.getColumn(),
                "For loop end value must be numeric, got " + typeToString(endType));
        }

        // Analyze step if present
        if (forStmt.getStep() != null) {
            IRType stepType = analyzeExpression(forStmt.getStep());
            if (!isNumericType(stepType)) {
                error(forStmt.getLine(), forStmt.getColumn(),
                    "For loop step value must be numeric, got " + typeToString(stepType));
            }
        }

        // Analyze body
        for (IRStatement stmt : forStmt.getBody()) {
            analyzeStatement(stmt);
        }

        popScope();
    }

    private void analyzeForEach(IRForEach forEachStmt) {
        pushScope("foreach");

        // Analyze collection expression
        IRType collType = analyzeExpression(forEachStmt.getCollection());

        // Determine element type
        IRType elementType = forEachStmt.getElementType();
        if (collType instanceof IRType.Array arrType) {
            elementType = arrType.elementType();
        }

        // Define loop variable
        currentScope().defineVariable(forEachStmt.getVariable(), elementType, true);

        // Analyze body
        for (IRStatement stmt : forEachStmt.getBody()) {
            analyzeStatement(stmt);
        }

        popScope();
    }

    private void analyzeReturn(IRReturn returnStmt) {
        if (currentFunctionReturnType == null) {
            // We're in top-level code
            if (returnStmt.getValue() != null) {
                warning(returnStmt.getLine(), returnStmt.getColumn(),
                    "Return value ignored in top-level code");
            }
            return;
        }

        if (returnStmt.getValue() == null) {
            // Return without value
            if (currentFunctionReturnType != null &&
                !currentFunctionReturnType.equals(IRType.Primitive.VOID)) {
                error(returnStmt.getLine(), returnStmt.getColumn(),
                    "Function '" + currentFunctionName + "' must return a value");
            }
        } else {
            // Return with value
            IRType returnType = analyzeExpression(returnStmt.getValue());
            if (!isAssignableFrom(currentFunctionReturnType, returnType)) {
                error(returnStmt.getLine(), returnStmt.getColumn(),
                    "Cannot return " + typeToString(returnType) +
                    " from function expecting " + typeToString(currentFunctionReturnType));
            }
        }
    }

    private void analyzeTry(IRTry tryStmt) {
        pushScope("try");
        for (IRStatement stmt : tryStmt.getTryBlock()) {
            analyzeStatement(stmt);
        }
        popScope();

        for (var catchClause : tryStmt.getCatchClauses()) {
            pushScope("catch");
            currentScope().defineVariable(catchClause.variable(), catchClause.exceptionType(), true);
            for (IRStatement stmt : catchClause.body()) {
                analyzeStatement(stmt);
            }
            popScope();
        }

        if (tryStmt.getFinallyBlock() != null) {
            pushScope("finally");
            for (IRStatement stmt : tryStmt.getFinallyBlock()) {
                analyzeStatement(stmt);
            }
            popScope();
        }
    }

    private void analyzeThrow(IRThrow throwStmt) {
        IRType exType = analyzeExpression(throwStmt.getException());
        // Should be Throwable subclass - for now just check it's a reference type
        if (exType != null && exType instanceof IRType.Primitive) {
            error(throwStmt.getLine(), throwStmt.getColumn(),
                "Cannot throw primitive type " + typeToString(exType));
        }
    }

    // ========================================================================
    // Phase 4: Class Analysis
    // ========================================================================

    private void analyzeClass(IRClass cls) {
        pushScope("class:" + cls.getName());

        // Check superclass exists if specified
        if (cls.getSuperClass() != null) {
            if (!isKnownType(cls.getSuperClass())) {
                warning(cls.getLine(), cls.getColumn(),
                    "Unknown superclass: " + cls.getSuperClass());
            }
        }

        // Check interfaces exist
        for (String iface : cls.getInterfaces()) {
            if (!isKnownType(iface)) {
                warning(cls.getLine(), cls.getColumn(),
                    "Unknown interface: " + iface);
            }
        }

        // Add fields to scope
        for (IRVariable field : cls.getFields()) {
            currentScope().defineVariable(field.getName(), field.getType(), false);
        }

        // Analyze methods
        for (IRFunction method : cls.getMethods()) {
            analyzeFunction(method);
        }

        popScope();
    }

    // ========================================================================
    // Expression Analysis
    // ========================================================================

    private IRType analyzeExpression(IRExpression expr) {
        if (expr == null) return null;

        if (expr instanceof IRIntLiteral) {
            return IRType.Primitive.INT;
        } else if (expr instanceof IRLongLiteral) {
            return IRType.Primitive.LONG;
        } else if (expr instanceof IRFloatLiteral) {
            return IRType.Primitive.FLOAT;
        } else if (expr instanceof IRDoubleLiteral) {
            return IRType.Primitive.DOUBLE;
        } else if (expr instanceof IRStringLiteral) {
            return IRType.Reference.STRING;
        } else if (expr instanceof IRBoolLiteral) {
            return IRType.Primitive.BOOLEAN;
        } else if (expr instanceof IRNullLiteral) {
            return IRType.Reference.OBJECT;
        } else if (expr instanceof IRIdentifier id) {
            return analyzeIdentifier(id);
        } else if (expr instanceof IRBinaryOp binOp) {
            return analyzeBinaryOp(binOp);
        } else if (expr instanceof IRUnaryOp unOp) {
            return analyzeUnaryOp(unOp);
        } else if (expr instanceof IRCall call) {
            return analyzeCall(call);
        } else if (expr instanceof IRMethodCall methodCall) {
            return analyzeMethodCall(methodCall);
        } else if (expr instanceof IRMemberAccess memberAccess) {
            return analyzeMemberAccess(memberAccess);
        } else if (expr instanceof IRArrayAccess arrayAccess) {
            return analyzeArrayAccess(arrayAccess);
        } else if (expr instanceof IRNewObject newObj) {
            return analyzeNewObject(newObj);
        } else if (expr instanceof IRNewArray newArr) {
            return analyzeNewArray(newArr);
        } else if (expr instanceof IRTernary ternary) {
            return analyzeTernary(ternary);
        } else if (expr instanceof IRInterpolatedString interp) {
            return analyzeInterpolatedString(interp);
        } else if (expr instanceof IRCast cast) {
            return analyzeCast(cast);
        }

        return expr.getType();
    }

    private IRType analyzeIdentifier(IRIdentifier id) {
        String name = id.name();

        // Check if it's a known namespace (not an error)
        if (KNOWN_NAMESPACES.contains(name)) {
            return new IRType.Reference(name);
        }

        // Look up in symbol table
        Symbol sym = lookupSymbol(name);
        if (sym == null) {
            error(id.getLine(), id.getColumn(),
                "Undefined variable: " + name);
            return IRType.Reference.OBJECT;  // Continue analysis with placeholder
        }

        // Check if initialized
        if (!sym.isInitialized()) {
            warning(id.getLine(), id.getColumn(),
                "Variable '" + name + "' may not be initialized");
        }

        return sym.getType();
    }

    private IRType analyzeBinaryOp(IRBinaryOp binOp) {
        IRType leftType = analyzeExpression(binOp.left());
        IRType rightType = analyzeExpression(binOp.right());

        IRBinaryOp.Operator op = binOp.op();

        // Comparison operators return Boolean
        if (op == IRBinaryOp.Operator.EQ || op == IRBinaryOp.Operator.NE ||
            op == IRBinaryOp.Operator.LT || op == IRBinaryOp.Operator.LE ||
            op == IRBinaryOp.Operator.GT || op == IRBinaryOp.Operator.GE) {
            return IRType.Primitive.BOOLEAN;
        }

        // Logical operators require Boolean operands
        if (op == IRBinaryOp.Operator.AND || op == IRBinaryOp.Operator.OR ||
            op == IRBinaryOp.Operator.XOR) {
            if (!isBooleanType(leftType) || !isBooleanType(rightType)) {
                error(binOp.left().getLine(), binOp.left().getColumn(),
                    "Logical operators require Boolean operands");
            }
            return IRType.Primitive.BOOLEAN;
        }

        // Concatenation returns String
        if (op == IRBinaryOp.Operator.CONCAT) {
            return IRType.Reference.STRING;
        }

        // Arithmetic operators require numeric types
        if (op == IRBinaryOp.Operator.ADD || op == IRBinaryOp.Operator.SUB ||
            op == IRBinaryOp.Operator.MUL || op == IRBinaryOp.Operator.DIV ||
            op == IRBinaryOp.Operator.MOD || op == IRBinaryOp.Operator.POWER) {

            // Special case: + with String is concatenation
            if (op == IRBinaryOp.Operator.ADD &&
                (isStringType(leftType) || isStringType(rightType))) {
                return IRType.Reference.STRING;
            }

            if (!isNumericType(leftType) || !isNumericType(rightType)) {
                error(binOp.left().getLine(), binOp.left().getColumn(),
                    "Arithmetic operators require numeric operands");
            }

            // Return wider numeric type
            return widenNumericType(leftType, rightType);
        }

        // Bitwise operators
        if (op == IRBinaryOp.Operator.BIT_AND || op == IRBinaryOp.Operator.BIT_OR ||
            op == IRBinaryOp.Operator.BIT_XOR || op == IRBinaryOp.Operator.SHL ||
            op == IRBinaryOp.Operator.SHR) {
            if (!isIntegralType(leftType) || !isIntegralType(rightType)) {
                error(binOp.left().getLine(), binOp.left().getColumn(),
                    "Bitwise operators require integral operands");
            }
            return widenNumericType(leftType, rightType);
        }

        return binOp.getType();
    }

    private IRType analyzeUnaryOp(IRUnaryOp unOp) {
        IRType operandType = analyzeExpression(unOp.operand());

        switch (unOp.op()) {
            case NOT:
                if (!isBooleanType(operandType)) {
                    error(unOp.operand().getLine(), unOp.operand().getColumn(),
                        "NOT operator requires Boolean operand");
                }
                return IRType.Primitive.BOOLEAN;

            case NEG:
            case MINUS:
            case PLUS:
                if (!isNumericType(operandType)) {
                    error(unOp.operand().getLine(), unOp.operand().getColumn(),
                        "Unary +/- requires numeric operand");
                }
                return operandType;

            case BIT_NOT:
                if (!isIntegralType(operandType)) {
                    error(unOp.operand().getLine(), unOp.operand().getColumn(),
                        "Bitwise NOT requires integral operand");
                }
                return operandType;
        }

        return operandType;
    }

    private IRType analyzeCall(IRCall call) {
        // Look up function
        Symbol sym = lookupSymbol(call.name());
        if (sym == null || sym.getKind() != Symbol.Kind.FUNCTION) {
            error(call.getLine(), call.getColumn(),
                "Undefined function: " + call.name());
            return IRType.Reference.OBJECT;
        }

        // Check argument count
        if (call.arguments().size() != sym.getParameterTypes().size()) {
            error(call.getLine(), call.getColumn(),
                "Function '" + call.name() + "' expects " + sym.getParameterTypes().size() +
                " arguments but got " + call.arguments().size());
        } else {
            // Check argument types
            for (int i = 0; i < call.arguments().size(); i++) {
                IRType argType = analyzeExpression(call.arguments().get(i));
                IRType paramType = sym.getParameterTypes().get(i);
                if (!isAssignableFrom(paramType, argType)) {
                    error(call.arguments().get(i).getLine(), call.arguments().get(i).getColumn(),
                        "Argument " + (i + 1) + " type mismatch: expected " +
                        typeToString(paramType) + ", got " + typeToString(argType));
                }
            }
        }

        return sym.getType();
    }

    private IRType analyzeMethodCall(IRMethodCall methodCall) {
        String className = methodCall.className();

        // Analyze receiver if present
        if (methodCall.receiver() != null) {
            analyzeExpression(methodCall.receiver());
        }

        // Analyze arguments
        for (IRExpression arg : methodCall.arguments()) {
            analyzeExpression(arg);
        }

        // Return type inference for known namespaces
        if (className != null && KNOWN_NAMESPACES.contains(className)) {
            return inferNamespaceMethodType(className, methodCall.methodName());
        }

        return methodCall.getType();
    }

    private IRType analyzeMemberAccess(IRMemberAccess memberAccess) {
        analyzeExpression(memberAccess.object());
        return memberAccess.getType();
    }

    private IRType analyzeArrayAccess(IRArrayAccess arrayAccess) {
        IRType arrayType = analyzeExpression(arrayAccess.array());
        IRType indexType = analyzeExpression(arrayAccess.index());

        // Check array type
        if (!(arrayType instanceof IRType.Array)) {
            error(arrayAccess.array().getLine(), arrayAccess.array().getColumn(),
                "Cannot index non-array type " + typeToString(arrayType));
            return IRType.Reference.OBJECT;
        }

        // Check index is integral
        if (!isIntegralType(indexType)) {
            error(arrayAccess.index().getLine(), arrayAccess.index().getColumn(),
                "Array index must be integral, got " + typeToString(indexType));
        }

        return ((IRType.Array) arrayType).elementType();
    }

    private IRType analyzeNewObject(IRNewObject newObj) {
        // Analyze constructor arguments
        for (IRExpression arg : newObj.arguments()) {
            analyzeExpression(arg);
        }
        return newObj.type();
    }

    private IRType analyzeNewArray(IRNewArray newArr) {
        IRType sizeType = analyzeExpression(newArr.size());
        if (!isIntegralType(sizeType)) {
            error(newArr.getLine(), newArr.getColumn(),
                "Array size must be integral, got " + typeToString(sizeType));
        }
        return new IRType.Array(newArr.elementType());
    }

    private IRType analyzeTernary(IRTernary ternary) {
        IRType condType = analyzeExpression(ternary.condition());
        if (!isBooleanType(condType)) {
            error(ternary.condition().getLine(), ternary.condition().getColumn(),
                "Ternary condition must be Boolean");
        }

        IRType thenType = analyzeExpression(ternary.thenExpr());
        IRType elseType = analyzeExpression(ternary.elseExpr());

        // Result type is common supertype
        return commonType(thenType, elseType);
    }

    private IRType analyzeInterpolatedString(IRInterpolatedString interp) {
        for (IRExpression part : interp.parts()) {
            analyzeExpression(part);
        }
        return IRType.Reference.STRING;
    }

    private IRType analyzeCast(IRCast cast) {
        analyzeExpression(cast.expression());
        return cast.targetType();
    }

    // ========================================================================
    // Type Utilities
    // ========================================================================

    private boolean isAssignableFrom(IRType target, IRType source) {
        if (target == null || source == null) return true;  // Unknown types
        if (target.equals(source)) return true;

        // Null can be assigned to any reference type
        if (source.equals(IRType.Reference.OBJECT) && target instanceof IRType.Reference) {
            return true;
        }

        // Numeric widening conversions
        if (isNumericType(target) && isNumericType(source)) {
            return getNumericRank(target) >= getNumericRank(source);
        }

        // Object hierarchy (simplified)
        if (target.equals(IRType.Reference.OBJECT) && source instanceof IRType.Reference) {
            return true;
        }

        return false;
    }

    private boolean isBooleanType(IRType type) {
        return type != null && type.equals(IRType.Primitive.BOOLEAN);
    }

    private boolean isNumericType(IRType type) {
        if (type == null) return false;
        if (type instanceof IRType.Primitive p) {
            return p == IRType.Primitive.INT || p == IRType.Primitive.LONG ||
                   p == IRType.Primitive.FLOAT || p == IRType.Primitive.DOUBLE ||
                   p == IRType.Primitive.BYTE || p == IRType.Primitive.CHAR;
        }
        return false;
    }

    private boolean isIntegralType(IRType type) {
        if (type == null) return false;
        if (type instanceof IRType.Primitive p) {
            return p == IRType.Primitive.INT || p == IRType.Primitive.LONG ||
                   p == IRType.Primitive.BYTE || p == IRType.Primitive.CHAR;
        }
        return false;
    }

    private boolean isStringType(IRType type) {
        return type != null && (type.equals(IRType.Reference.STRING) ||
               (type instanceof IRType.Reference ref && ref.name().equals("java.lang.String")));
    }

    private int getNumericRank(IRType type) {
        if (type instanceof IRType.Primitive p) {
            return switch (p.kind()) {
                case BYTE -> 1;
                case CHAR -> 2;
                case INT -> 3;
                case LONG -> 4;
                case FLOAT -> 5;
                case DOUBLE -> 6;
                default -> 0;
            };
        }
        return 0;
    }

    private IRType widenNumericType(IRType a, IRType b) {
        if (getNumericRank(a) >= getNumericRank(b)) {
            return a;
        }
        return b;
    }

    private IRType commonType(IRType a, IRType b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.equals(b)) return a;

        if (isNumericType(a) && isNumericType(b)) {
            return widenNumericType(a, b);
        }

        // For reference types, return Object as common supertype
        return IRType.Reference.OBJECT;
    }

    private boolean isKnownType(String typeName) {
        // Check built-in types and known classes
        return typeName != null && (
            typeName.equals("Object") || typeName.equals("String") ||
            typeName.equals("Exception") || typeName.equals("Throwable") ||
            lookupSymbol(typeName) != null
        );
    }

    private IRType inferNamespaceMethodType(String namespace, String methodName) {
        return switch (namespace) {
            case "Console" -> switch (methodName) {
                case "WriteLine", "Write" -> IRType.Primitive.VOID;
                case "ReadLine" -> IRType.Reference.STRING;
                default -> IRType.Reference.OBJECT;
            };
            case "Math" -> switch (methodName) {
                case "Sqrt", "Sin", "Cos", "Tan", "Log", "Exp", "Pow", "Floor", "Ceiling",
                     "Pi", "E", "ToRadians", "ToDegrees", "Hypot", "Lerp", "Random" -> IRType.Primitive.DOUBLE;
                case "Abs", "Max", "Min", "Sign", "Clamp", "Round" -> IRType.Primitive.INT;
                case "IsNaN", "IsInfinite", "IsFinite", "ApproxEqual" -> IRType.Primitive.BOOLEAN;
                default -> IRType.Primitive.DOUBLE;
            };
            case "Str" -> switch (methodName) {
                case "Length", "IndexOf", "LastIndexOf", "Compare" -> IRType.Primitive.INT;
                case "StartsWith", "EndsWith", "Contains", "IsNullOrEmpty",
                     "IsNullOrWhiteSpace", "Equals", "EqualsIgnoreCase" -> IRType.Primitive.BOOLEAN;
                case "CharAt" -> IRType.Primitive.CHAR;
                case "Split" -> new IRType.Array(IRType.Reference.STRING);
                default -> IRType.Reference.STRING;
            };
            case "File" -> switch (methodName) {
                case "Exists", "Delete", "CreateDirectory", "Copy", "Move" -> IRType.Primitive.BOOLEAN;
                case "Size" -> IRType.Primitive.LONG;
                case "ReadAllLines", "ListFiles", "ListDirectories" -> new IRType.Array(IRType.Reference.STRING);
                default -> IRType.Reference.STRING;
            };
            case "Regex" -> switch (methodName) {
                case "IsMatch" -> IRType.Primitive.BOOLEAN;
                case "Matches", "Split" -> new IRType.Array(IRType.Reference.STRING);
                default -> IRType.Reference.STRING;
            };
            default -> IRType.Reference.OBJECT;
        };
    }

    private String typeToString(IRType type) {
        if (type == null) return "unknown";
        return type.toString();
    }

    // ========================================================================
    // Scope Management
    // ========================================================================

    private SymbolTable currentScope() {
        return scopeStack.peek();
    }

    private void pushScope(String name) {
        scopeStack.push(new SymbolTable(name, currentScope()));
    }

    private void popScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }

    private Symbol lookupSymbol(String name) {
        for (SymbolTable scope : scopeStack) {
            Symbol sym = scope.getSymbol(name);
            if (sym != null) {
                return sym;
            }
        }
        return null;
    }

    // ========================================================================
    // Error Reporting
    // ========================================================================

    private void error(int line, int column, String message) {
        errors.add(new SemanticError(line, column, message));
    }

    private void warning(int line, int column, String message) {
        warnings.add(new SemanticWarning(line, column, message));
    }
}
