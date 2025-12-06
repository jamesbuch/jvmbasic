package com.jvmbasic.sir;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.decl.*;
import com.jvmbasic.ir.expr.*;
import com.jvmbasic.ir.stmt.*;

import java.util.*;

/**
 * Lowers tree-based IR to stack-based IR (SIR).
 *
 * This pass converts the high-level tree IR into a linear sequence of
 * SSA-style instructions with explicit virtual registers.
 */
public class IRLowering {

    private SIRModule module;
    private SIRFunction currentFunction;
    private SIRBasicBlock currentBlock;

    /**
     * Lower an IR compilation unit to a SIR module.
     */
    public SIRModule lower(IRCompilationUnit unit) {
        module = new SIRModule(unit.getName());

        // Lower declared functions
        for (IRFunction func : unit.getFunctions()) {
            SIRFunction sirFunc = lowerFunction(func);
            module.addFunction(sirFunc);
        }

        // Lower classes
        for (IRClass cls : unit.getClasses()) {
            SIRClass sirClass = lowerClass(cls);
            module.addClass(sirClass);
        }

        // Lower top-level statements into main function
        if (!unit.getStatements().isEmpty()) {
            SIRFunction main = createMainFunction(unit.getStatements());
            module.setMainFunction(main);
        }

        return module;
    }

    /**
     * Lower a function declaration.
     */
    private SIRFunction lowerFunction(IRFunction func) {
        // Build parameter list
        List<SIRFunction.Parameter> params = new ArrayList<>();
        int slot = func.isStatic() ? 0 : 1;  // Reserve slot 0 for 'this' in instance methods

        for (IRParameter param : func.getParameters()) {
            params.add(new SIRFunction.Parameter(param.getName(), param.getType(), slot));
            slot += slotWidth(param.getType());
        }

        currentFunction = new SIRFunction(
            func.getName(),
            params,
            func.getReturnType(),
            func.isStatic()
        );

        // Create entry block
        currentBlock = currentFunction.createBlock("entry");

        // Lower body statements
        for (IRStatement stmt : func.getBody()) {
            lowerStatement(stmt);
        }

        // Ensure function ends with return
        if (!currentBlock.isTerminated()) {
            IRType retType = func.getReturnType();
            if (retType == null || retType.equals(IRType.Primitive.VOID)) {
                emit(new SIRInstruction.Return(-1, null));
            } else {
                // Return default value (shouldn't happen in well-formed IR)
                int defaultVal = emitDefaultValue(retType);
                emit(new SIRInstruction.Return(defaultVal, retType));
            }
        }

        return currentFunction;
    }

    /**
     * Lower a class declaration.
     */
    private SIRClass lowerClass(IRClass cls) {
        String superClass = cls.getSuperClass() != null ?
            cls.getSuperClass().replace('.', '/') : "java/lang/Object";

        SIRClass sirClass = new SIRClass(
            cls.getName().replace('.', '/'),
            superClass,
            cls.isAbstract()
        );

        // Add interfaces
        for (String iface : cls.getInterfaces()) {
            sirClass.addInterface(iface.replace('.', '/'));
        }

        // Add fields
        for (IRVariable field : cls.getFields()) {
            sirClass.addField(new SIRClass.Field(
                field.getName(),
                field.getType(),
                false,  // TODO: check if static
                0  // TODO: access modifiers
            ));
        }

        // Lower methods
        for (IRFunction method : cls.getMethods()) {
            SIRFunction sirMethod = lowerFunction(method);
            sirClass.addMethod(sirMethod);
        }

        return sirClass;
    }

    /**
     * Create main function from top-level statements.
     */
    private SIRFunction createMainFunction(List<IRStatement> statements) {
        // main(String[] args)
        List<SIRFunction.Parameter> params = List.of(
            new SIRFunction.Parameter("args", new IRType.Array(IRType.Reference.STRING), 0)
        );

        currentFunction = new SIRFunction("main", params, IRType.Primitive.VOID, true);
        currentBlock = currentFunction.createBlock("entry");

        // Lower all statements
        for (IRStatement stmt : statements) {
            lowerStatement(stmt);
        }

        // Add return
        if (!currentBlock.isTerminated()) {
            emit(new SIRInstruction.Return(-1, null));
        }

        return currentFunction;
    }

    // ========================================================================
    // Statement Lowering
    // ========================================================================

    private void lowerStatement(IRStatement stmt) {
        if (stmt instanceof IRVarDecl varDecl) {
            lowerVarDecl(varDecl);
        } else if (stmt instanceof IRAssignment assign) {
            lowerAssignment(assign);
        } else if (stmt instanceof IRIf ifStmt) {
            lowerIf(ifStmt);
        } else if (stmt instanceof IRWhile whileStmt) {
            lowerWhile(whileStmt);
        } else if (stmt instanceof IRFor forStmt) {
            lowerFor(forStmt);
        } else if (stmt instanceof IRForEach forEach) {
            lowerForEach(forEach);
        } else if (stmt instanceof IRReturn ret) {
            lowerReturn(ret);
        } else if (stmt instanceof IRExprStmt exprStmt) {
            lowerExprStmt(exprStmt);
        } else if (stmt instanceof IRBlock block) {
            for (IRStatement s : block.getStatements()) {
                lowerStatement(s);
            }
        } else if (stmt instanceof IRThrow throwStmt) {
            lowerThrow(throwStmt);
        } else if (stmt instanceof IRTry tryStmt) {
            lowerTry(tryStmt);
        }
    }

    private void lowerVarDecl(IRVarDecl varDecl) {
        emit(new SIRInstruction.Comment("var " + varDecl.getName()));

        int slot = currentFunction.allocSlot(varDecl.getName(), varDecl.getType());

        if (varDecl.getInitializer() != null) {
            int valueReg = lowerExpression(varDecl.getInitializer());
            emit(new SIRInstruction.Store(slot, valueReg, varDecl.getName(), varDecl.getType()));
        }
    }

    private void lowerAssignment(IRAssignment assign) {
        IRExpression target = assign.getTarget();
        int valueReg = lowerExpression(assign.getValue());

        if (target instanceof IRIdentifier id) {
            // Simple variable assignment
            int slot = currentFunction.getSlot(id.name());
            if (slot >= 0) {
                emit(new SIRInstruction.Store(slot, valueReg, id.name(),
                    currentFunction.getLocalType(id.name())));
            }
        } else if (target instanceof IRMemberAccess mem) {
            // Field assignment
            int objReg = lowerExpression(mem.object());
            String owner = getOwnerClass(mem.object());
            emit(new SIRInstruction.PutField(objReg, owner, mem.memberName(), valueReg, mem.getType()));
        } else if (target instanceof IRArrayAccess arr) {
            // Array element assignment
            int arrayReg = lowerExpression(arr.array());
            int indexReg = lowerExpression(arr.index());
            emit(new SIRInstruction.ArrayStore(arrayReg, indexReg, valueReg));
        }
    }

    private void lowerIf(IRIf ifStmt) {
        String thenLabel = currentFunction.newLabel("then");
        String elseLabel = currentFunction.newLabel("else");
        String endLabel = currentFunction.newLabel("endif");

        // Evaluate condition
        int condReg = lowerExpression(ifStmt.getCondition());
        emit(new SIRInstruction.IfFalse(condReg, elseLabel));

        // Then branch
        currentBlock = currentFunction.createBlock(thenLabel);
        for (IRStatement s : ifStmt.getThenBranch()) {
            lowerStatement(s);
        }
        if (!currentBlock.isTerminated()) {
            emit(new SIRInstruction.Goto(endLabel));
        }

        // Else branch
        currentBlock = currentFunction.createBlock(elseLabel);
        if (ifStmt.getElseBranch() != null) {
            for (IRStatement s : ifStmt.getElseBranch()) {
                lowerStatement(s);
            }
        }
        if (!currentBlock.isTerminated()) {
            emit(new SIRInstruction.Goto(endLabel));
        }

        // End
        currentBlock = currentFunction.createBlock(endLabel);
    }

    private void lowerWhile(IRWhile whileStmt) {
        String condLabel = currentFunction.newLabel("while_cond");
        String bodyLabel = currentFunction.newLabel("while_body");
        String endLabel = currentFunction.newLabel("while_end");

        emit(new SIRInstruction.Goto(condLabel));

        // Condition
        currentBlock = currentFunction.createBlock(condLabel);
        int condReg = lowerExpression(whileStmt.getCondition());
        emit(new SIRInstruction.IfFalse(condReg, endLabel));

        // Body
        currentBlock = currentFunction.createBlock(bodyLabel);
        for (IRStatement s : whileStmt.getBody()) {
            lowerStatement(s);
        }
        emit(new SIRInstruction.Goto(condLabel));

        // End
        currentBlock = currentFunction.createBlock(endLabel);
    }

    private void lowerFor(IRFor forStmt) {
        emit(new SIRInstruction.Comment("for " + forStmt.getVariable()));

        String condLabel = currentFunction.newLabel("for_cond");
        String bodyLabel = currentFunction.newLabel("for_body");
        String stepLabel = currentFunction.newLabel("for_step");
        String endLabel = currentFunction.newLabel("for_end");

        // Initialize loop variable
        int slot = currentFunction.allocSlot(forStmt.getVariable(), IRType.Primitive.INT);
        int startReg = lowerExpression(forStmt.getStart());
        emit(new SIRInstruction.Store(slot, startReg, forStmt.getVariable(), IRType.Primitive.INT));

        emit(new SIRInstruction.Goto(condLabel));

        // Condition: i <= end
        currentBlock = currentFunction.createBlock(condLabel);
        int iReg = currentFunction.allocRegister();
        emit(new SIRInstruction.Load(iReg, slot, forStmt.getVariable(), IRType.Primitive.INT));
        int endReg = lowerExpression(forStmt.getEnd());
        int cmpReg = currentFunction.allocRegister();
        emit(new SIRInstruction.ICmp(cmpReg, SIRInstruction.CmpOp.LE, iReg, endReg));
        emit(new SIRInstruction.IfFalse(cmpReg, endLabel));

        // Body
        currentBlock = currentFunction.createBlock(bodyLabel);
        for (IRStatement s : forStmt.getBody()) {
            lowerStatement(s);
        }
        emit(new SIRInstruction.Goto(stepLabel));

        // Step: i = i + step
        currentBlock = currentFunction.createBlock(stepLabel);
        int loadReg = currentFunction.allocRegister();
        emit(new SIRInstruction.Load(loadReg, slot, forStmt.getVariable(), IRType.Primitive.INT));
        int stepReg;
        if (forStmt.getStep() != null) {
            stepReg = lowerExpression(forStmt.getStep());
        } else {
            stepReg = currentFunction.allocRegister();
            emit(new SIRInstruction.IConst(stepReg, 1));
        }
        int newValReg = currentFunction.allocRegister();
        emit(new SIRInstruction.IAdd(newValReg, loadReg, stepReg));
        emit(new SIRInstruction.Store(slot, newValReg, forStmt.getVariable(), IRType.Primitive.INT));
        emit(new SIRInstruction.Goto(condLabel));

        // End
        currentBlock = currentFunction.createBlock(endLabel);
    }

    private void lowerForEach(IRForEach forEach) {
        emit(new SIRInstruction.Comment("for each " + forEach.getVariable()));
        // TODO: Implement for-each lowering (iterator pattern)
        // For now, just process the body as a placeholder
        for (IRStatement s : forEach.getBody()) {
            lowerStatement(s);
        }
    }

    private void lowerReturn(IRReturn ret) {
        if (ret.getValue() != null) {
            int valueReg = lowerExpression(ret.getValue());
            emit(new SIRInstruction.Return(valueReg, ret.getValue().getType()));
        } else {
            emit(new SIRInstruction.Return(-1, null));
        }
    }

    private void lowerExprStmt(IRExprStmt exprStmt) {
        int resultReg = lowerExpression(exprStmt.getExpression());
        // Pop the result if it's not void
        IRType type = exprStmt.getExpression().getType();
        if (type != null && !type.equals(IRType.Primitive.VOID)) {
            emit(new SIRInstruction.Pop(resultReg));
        }
    }

    private void lowerThrow(IRThrow throwStmt) {
        int exReg = lowerExpression(throwStmt.getException());
        emit(new SIRInstruction.Throw(exReg));
    }

    private void lowerTry(IRTry tryStmt) {
        // TODO: Implement try/catch lowering
        // For now, just process the try body
        emit(new SIRInstruction.Comment("try"));
        for (IRStatement s : tryStmt.getTryBlock()) {
            lowerStatement(s);
        }
    }

    // ========================================================================
    // Expression Lowering
    // ========================================================================

    /**
     * Lower an expression and return the register containing the result.
     */
    private int lowerExpression(IRExpression expr) {
        if (expr instanceof IRIntLiteral lit) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.IConst(r, lit.value()));
            return r;
        } else if (expr instanceof IRLongLiteral lit) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.LConst(r, lit.value()));
            return r;
        } else if (expr instanceof IRFloatLiteral lit) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.FConst(r, lit.value()));
            return r;
        } else if (expr instanceof IRDoubleLiteral lit) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.DConst(r, lit.value()));
            return r;
        } else if (expr instanceof IRStringLiteral lit) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.SConst(r, lit.value()));
            return r;
        } else if (expr instanceof IRBoolLiteral lit) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.IConst(r, lit.value() ? 1 : 0));
            return r;
        } else if (expr instanceof IRNullLiteral) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.NullConst(r));
            return r;
        } else if (expr instanceof IRIdentifier id) {
            return lowerIdentifier(id);
        } else if (expr instanceof IRBinaryOp binOp) {
            return lowerBinaryOp(binOp);
        } else if (expr instanceof IRUnaryOp unOp) {
            return lowerUnaryOp(unOp);
        } else if (expr instanceof IRCall call) {
            return lowerCall(call);
        } else if (expr instanceof IRMethodCall methodCall) {
            return lowerMethodCall(methodCall);
        } else if (expr instanceof IRMemberAccess memAccess) {
            return lowerMemberAccess(memAccess);
        } else if (expr instanceof IRArrayAccess arrAccess) {
            return lowerArrayAccess(arrAccess);
        } else if (expr instanceof IRNewObject newObj) {
            return lowerNewObject(newObj);
        } else if (expr instanceof IRNewArray newArr) {
            return lowerNewArray(newArr);
        } else if (expr instanceof IRCast cast) {
            return lowerCast(cast);
        } else if (expr instanceof IRTernary ternary) {
            return lowerTernary(ternary);
        } else if (expr instanceof IRInterpolatedString interp) {
            return lowerInterpolatedString(interp);
        }

        // Unknown expression type
        int r = currentFunction.allocRegister();
        emit(new SIRInstruction.NullConst(r));
        return r;
    }

    private int lowerIdentifier(IRIdentifier id) {
        String name = id.name();

        // Check if it's 'this' or 'super'
        if (name.equals("this") || name.equals("Me")) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.Load(r, 0, "this", IRType.Reference.OBJECT));
            return r;
        }

        // Check local variable
        int slot = currentFunction.getSlot(name);
        if (slot >= 0) {
            int r = currentFunction.allocRegister();
            IRType type = currentFunction.getLocalType(name);
            emit(new SIRInstruction.Load(r, slot, name, type));
            return r;
        }

        // Could be a static field or class reference - return placeholder
        int r = currentFunction.allocRegister();
        emit(new SIRInstruction.Comment("unresolved: " + name));
        emit(new SIRInstruction.NullConst(r));
        return r;
    }

    private int lowerBinaryOp(IRBinaryOp binOp) {
        int leftReg = lowerExpression(binOp.left());
        int rightReg = lowerExpression(binOp.right());
        int r = currentFunction.allocRegister();

        IRType leftType = binOp.left().getType();
        IRBinaryOp.Operator op = binOp.op();

        // Determine operation based on type and operator
        switch (op) {
            case ADD -> {
                if (isInt(leftType)) emit(new SIRInstruction.IAdd(r, leftReg, rightReg));
                else if (isLong(leftType)) emit(new SIRInstruction.LAdd(r, leftReg, rightReg));
                else if (isFloat(leftType)) emit(new SIRInstruction.FAdd(r, leftReg, rightReg));
                else if (isDouble(leftType)) emit(new SIRInstruction.DAdd(r, leftReg, rightReg));
                else emit(new SIRInstruction.IAdd(r, leftReg, rightReg));  // Default to int
            }
            case SUB -> {
                if (isInt(leftType)) emit(new SIRInstruction.ISub(r, leftReg, rightReg));
                else if (isLong(leftType)) emit(new SIRInstruction.LSub(r, leftReg, rightReg));
                else if (isFloat(leftType)) emit(new SIRInstruction.FSub(r, leftReg, rightReg));
                else if (isDouble(leftType)) emit(new SIRInstruction.DSub(r, leftReg, rightReg));
                else emit(new SIRInstruction.ISub(r, leftReg, rightReg));
            }
            case MUL -> {
                if (isInt(leftType)) emit(new SIRInstruction.IMul(r, leftReg, rightReg));
                else if (isLong(leftType)) emit(new SIRInstruction.LMul(r, leftReg, rightReg));
                else if (isFloat(leftType)) emit(new SIRInstruction.FMul(r, leftReg, rightReg));
                else if (isDouble(leftType)) emit(new SIRInstruction.DMul(r, leftReg, rightReg));
                else emit(new SIRInstruction.IMul(r, leftReg, rightReg));
            }
            case DIV -> {
                if (isInt(leftType)) emit(new SIRInstruction.IDiv(r, leftReg, rightReg));
                else if (isLong(leftType)) emit(new SIRInstruction.LDiv(r, leftReg, rightReg));
                else if (isFloat(leftType)) emit(new SIRInstruction.FDiv(r, leftReg, rightReg));
                else if (isDouble(leftType)) emit(new SIRInstruction.DDiv(r, leftReg, rightReg));
                else emit(new SIRInstruction.IDiv(r, leftReg, rightReg));
            }
            case INTDIV -> emit(new SIRInstruction.IDiv(r, leftReg, rightReg));
            case MOD -> {
                if (isInt(leftType)) emit(new SIRInstruction.IMod(r, leftReg, rightReg));
                else if (isLong(leftType)) emit(new SIRInstruction.LMod(r, leftReg, rightReg));
                else emit(new SIRInstruction.IMod(r, leftReg, rightReg));
            }
            case POWER -> emit(new SIRInstruction.Power(r, leftReg, rightReg));
            case EQ -> emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.EQ, leftReg, rightReg));
            case NE -> emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.NE, leftReg, rightReg));
            case LT -> emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.LT, leftReg, rightReg));
            case LE -> emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.LE, leftReg, rightReg));
            case GT -> emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.GT, leftReg, rightReg));
            case GE -> emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.GE, leftReg, rightReg));
            case AND -> emit(new SIRInstruction.IAnd(r, leftReg, rightReg));
            case OR -> emit(new SIRInstruction.IOr(r, leftReg, rightReg));
            case XOR -> emit(new SIRInstruction.IXor(r, leftReg, rightReg));
            case BIT_AND -> emit(new SIRInstruction.IAnd(r, leftReg, rightReg));
            case BIT_OR -> emit(new SIRInstruction.IOr(r, leftReg, rightReg));
            case BIT_XOR -> emit(new SIRInstruction.IXor(r, leftReg, rightReg));
            case SHL -> emit(new SIRInstruction.IShl(r, leftReg, rightReg));
            case SHR -> emit(new SIRInstruction.IShr(r, leftReg, rightReg));
            case CONCAT -> emit(new SIRInstruction.Concat(r, leftReg, rightReg));
            default -> {
                emit(new SIRInstruction.Comment("unknown op: " + op));
                emit(new SIRInstruction.IConst(r, 0));
            }
        }

        return r;
    }

    private int lowerUnaryOp(IRUnaryOp unOp) {
        int operandReg = lowerExpression(unOp.operand());
        int r = currentFunction.allocRegister();

        IRType type = unOp.operand().getType();

        switch (unOp.op()) {
            case MINUS, NEG -> {
                if (isInt(type)) emit(new SIRInstruction.INeg(r, operandReg));
                else if (isLong(type)) emit(new SIRInstruction.LNeg(r, operandReg));
                else if (isFloat(type)) emit(new SIRInstruction.FNeg(r, operandReg));
                else if (isDouble(type)) emit(new SIRInstruction.DNeg(r, operandReg));
                else emit(new SIRInstruction.INeg(r, operandReg));
            }
            case PLUS -> {
                // Unary plus is a no-op, just return the operand register
                return operandReg;
            }
            case NOT -> {
                // Logical NOT: result = (operand == 0) ? 1 : 0
                int zeroReg = currentFunction.allocRegister();
                emit(new SIRInstruction.IConst(zeroReg, 0));
                emit(new SIRInstruction.ICmp(r, SIRInstruction.CmpOp.EQ, operandReg, zeroReg));
            }
            case BIT_NOT -> emit(new SIRInstruction.INot(r, operandReg));
        }

        return r;
    }

    private int lowerCall(IRCall call) {
        // Lower arguments
        int[] argRegs = new int[call.arguments().size()];
        for (int i = 0; i < call.arguments().size(); i++) {
            argRegs[i] = lowerExpression(call.arguments().get(i));
        }

        int r = currentFunction.allocRegister();
        IRType returnType = call.getType();

        // Determine owner class (default to current module for now)
        String owner = module.getName().replace('.', '/');

        emit(new SIRInstruction.InvokeStatic(r, owner, call.name(), argRegs, returnType));
        return r;
    }

    private int lowerMethodCall(IRMethodCall methodCall) {
        int objReg = -1;
        String owner = methodCall.className();

        // Lower target object if not static
        if (methodCall.receiver() != null) {
            objReg = lowerExpression(methodCall.receiver());
            owner = getOwnerClass(methodCall.receiver());
        }

        // Lower arguments
        int[] argRegs = new int[methodCall.arguments().size()];
        for (int i = 0; i < methodCall.arguments().size(); i++) {
            argRegs[i] = lowerExpression(methodCall.arguments().get(i));
        }

        int r = currentFunction.allocRegister();
        IRType returnType = methodCall.getType();

        // Handle Console specially
        if (owner != null && (owner.equals("Console") || owner.equals("java/lang/System"))) {
            return lowerConsoleCall(methodCall.methodName(), argRegs, returnType);
        }

        // Map BASIC namespace classes to runtime library classes
        if (owner != null) {
            owner = switch (owner) {
                case "Math" -> "com/jvmbasic/runtime/BasicMath";
                case "Str" -> "com/jvmbasic/runtime/BasicStr";
                case "File" -> "com/jvmbasic/runtime/BasicFile";
                case "Regex" -> "com/jvmbasic/runtime/BasicRegex";
                default -> owner;
            };
        }

        if (methodCall.isStatic() || objReg < 0) {
            emit(new SIRInstruction.InvokeStatic(r, owner != null ? owner : "java/lang/Object",
                methodCall.methodName(), argRegs, returnType));
        } else {
            emit(new SIRInstruction.InvokeVirtual(r, objReg, owner != null ? owner : "java/lang/Object",
                methodCall.methodName(), argRegs, returnType));
        }
        return r;
    }

    private int lowerConsoleCall(String method, int[] argRegs, IRType returnType) {
        int r = currentFunction.allocRegister();

        if (method.equals("WriteLine") || method.equals("Write")) {
            // System.out.println / print
            int outReg = currentFunction.allocRegister();
            emit(new SIRInstruction.GetStatic(outReg, "java/lang/System", "out",
                new IRType.Reference("java/io/PrintStream")));

            String javaMethod = method.equals("WriteLine") ? "println" : "print";

            if (argRegs.length > 0) {
                emit(new SIRInstruction.InvokeVirtual(r, outReg, "java/io/PrintStream",
                    javaMethod, argRegs, IRType.Primitive.VOID));
            } else {
                emit(new SIRInstruction.InvokeVirtual(r, outReg, "java/io/PrintStream",
                    javaMethod, new int[0], IRType.Primitive.VOID));
            }
        } else if (method.equals("ReadLine")) {
            // TODO: Implement Console.ReadLine
            emit(new SIRInstruction.Comment("Console.ReadLine not yet implemented"));
            emit(new SIRInstruction.SConst(r, ""));
        }

        return r;
    }

    private int lowerMemberAccess(IRMemberAccess memAccess) {
        int objReg = lowerExpression(memAccess.object());
        int r = currentFunction.allocRegister();
        String owner = getOwnerClass(memAccess.object());
        emit(new SIRInstruction.GetField(r, objReg, owner, memAccess.memberName(), memAccess.getType()));
        return r;
    }

    private int lowerArrayAccess(IRArrayAccess arrAccess) {
        int arrayReg = lowerExpression(arrAccess.array());
        int indexReg = lowerExpression(arrAccess.index());
        int r = currentFunction.allocRegister();
        emit(new SIRInstruction.ArrayLoad(r, arrayReg, indexReg, arrAccess.getType()));
        return r;
    }

    private int lowerNewObject(IRNewObject newObj) {
        String className;
        IRType type = newObj.type();
        if (type instanceof IRType.Reference ref) {
            className = ref.name().replace('.', '/');
        } else {
            className = "java/lang/Object";
        }

        // NEW and DUP
        int objReg = currentFunction.allocRegister();
        emit(new SIRInstruction.New(objReg, className));

        int dupReg = currentFunction.allocRegister();
        emit(new SIRInstruction.Dup(dupReg, objReg));

        // Lower constructor arguments
        int[] argRegs = new int[newObj.arguments().size()];
        for (int i = 0; i < newObj.arguments().size(); i++) {
            argRegs[i] = lowerExpression(newObj.arguments().get(i));
        }

        // INVOKESPECIAL <init>
        emit(new SIRInstruction.InvokeSpecial(dupReg, className, "<init>", argRegs));

        return objReg;
    }

    private int lowerNewArray(IRNewArray newArr) {
        int sizeReg = lowerExpression(newArr.size());
        int r = currentFunction.allocRegister();
        emit(new SIRInstruction.NewArray(r, newArr.elementType(), sizeReg));
        return r;
    }

    private int lowerCast(IRCast cast) {
        int valueReg = lowerExpression(cast.expression());
        int r = currentFunction.allocRegister();

        IRType fromType = cast.expression().getType();
        IRType toType = cast.targetType();

        // Insert appropriate conversion instruction
        if (isInt(fromType) && isLong(toType)) {
            emit(new SIRInstruction.I2L(r, valueReg));
        } else if (isInt(fromType) && isFloat(toType)) {
            emit(new SIRInstruction.I2F(r, valueReg));
        } else if (isInt(fromType) && isDouble(toType)) {
            emit(new SIRInstruction.I2D(r, valueReg));
        } else if (isLong(fromType) && isInt(toType)) {
            emit(new SIRInstruction.L2I(r, valueReg));
        } else if (isLong(fromType) && isFloat(toType)) {
            emit(new SIRInstruction.L2F(r, valueReg));
        } else if (isLong(fromType) && isDouble(toType)) {
            emit(new SIRInstruction.L2D(r, valueReg));
        } else if (isFloat(fromType) && isInt(toType)) {
            emit(new SIRInstruction.F2I(r, valueReg));
        } else if (isFloat(fromType) && isLong(toType)) {
            emit(new SIRInstruction.F2L(r, valueReg));
        } else if (isFloat(fromType) && isDouble(toType)) {
            emit(new SIRInstruction.F2D(r, valueReg));
        } else if (isDouble(fromType) && isInt(toType)) {
            emit(new SIRInstruction.D2I(r, valueReg));
        } else if (isDouble(fromType) && isLong(toType)) {
            emit(new SIRInstruction.D2L(r, valueReg));
        } else if (isDouble(fromType) && isFloat(toType)) {
            emit(new SIRInstruction.D2F(r, valueReg));
        } else if (toType instanceof IRType.Reference) {
            // Object cast
            emit(new SIRInstruction.CheckCast(r, valueReg, toType));
        } else {
            // No conversion needed
            return valueReg;
        }

        return r;
    }

    private int lowerTernary(IRTernary ternary) {
        String trueLabel = currentFunction.newLabel("ternary_true");
        String falseLabel = currentFunction.newLabel("ternary_false");
        String endLabel = currentFunction.newLabel("ternary_end");

        int condReg = lowerExpression(ternary.condition());
        emit(new SIRInstruction.IfFalse(condReg, falseLabel));

        // True branch
        currentBlock = currentFunction.createBlock(trueLabel);
        int trueReg = lowerExpression(ternary.thenExpr());
        int trueResultSlot = currentFunction.allocSlot("$ternary_result", ternary.getType());
        emit(new SIRInstruction.Store(trueResultSlot, trueReg, "$ternary_result", ternary.getType()));
        emit(new SIRInstruction.Goto(endLabel));

        // False branch
        currentBlock = currentFunction.createBlock(falseLabel);
        int falseReg = lowerExpression(ternary.elseExpr());
        emit(new SIRInstruction.Store(trueResultSlot, falseReg, "$ternary_result", ternary.getType()));
        emit(new SIRInstruction.Goto(endLabel));

        // End - load result
        currentBlock = currentFunction.createBlock(endLabel);
        int r = currentFunction.allocRegister();
        emit(new SIRInstruction.Load(r, trueResultSlot, "$ternary_result", ternary.getType()));

        return r;
    }

    private int lowerInterpolatedString(IRInterpolatedString interp) {
        List<IRExpression> parts = interp.parts();

        if (parts.isEmpty()) {
            int r = currentFunction.allocRegister();
            emit(new SIRInstruction.SConst(r, ""));
            return r;
        }

        // Start with first part
        int resultReg = lowerExpression(parts.get(0));

        // Convert to string if needed
        if (!isString(parts.get(0).getType())) {
            int strReg = currentFunction.allocRegister();
            emit(new SIRInstruction.ToString(strReg, resultReg, parts.get(0).getType()));
            resultReg = strReg;
        }

        // Concatenate remaining parts
        for (int i = 1; i < parts.size(); i++) {
            int partReg = lowerExpression(parts.get(i));

            // Convert to string if needed
            if (!isString(parts.get(i).getType())) {
                int strReg = currentFunction.allocRegister();
                emit(new SIRInstruction.ToString(strReg, partReg, parts.get(i).getType()));
                partReg = strReg;
            }

            int concatReg = currentFunction.allocRegister();
            emit(new SIRInstruction.Concat(concatReg, resultReg, partReg));
            resultReg = concatReg;
        }

        return resultReg;
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void emit(SIRInstruction inst) {
        currentBlock.addInstruction(inst);
    }

    private int emitDefaultValue(IRType type) {
        int r = currentFunction.allocRegister();
        if (isInt(type) || type.equals(IRType.Primitive.BOOLEAN)) {
            emit(new SIRInstruction.IConst(r, 0));
        } else if (isLong(type)) {
            emit(new SIRInstruction.LConst(r, 0L));
        } else if (isFloat(type)) {
            emit(new SIRInstruction.FConst(r, 0.0f));
        } else if (isDouble(type)) {
            emit(new SIRInstruction.DConst(r, 0.0));
        } else {
            emit(new SIRInstruction.NullConst(r));
        }
        return r;
    }

    private String getOwnerClass(IRExpression expr) {
        IRType type = expr.getType();
        if (type instanceof IRType.Reference ref) {
            return ref.name().replace('.', '/');
        }
        if (expr instanceof IRIdentifier id) {
            // Could be a class name like "Console"
            return id.name();
        }
        return "java/lang/Object";
    }

    private int slotWidth(IRType type) {
        if (type.equals(IRType.Primitive.LONG) || type.equals(IRType.Primitive.DOUBLE)) {
            return 2;
        }
        return 1;
    }

    private boolean isInt(IRType type) {
        return type != null && type.equals(IRType.Primitive.INT);
    }

    private boolean isLong(IRType type) {
        return type != null && type.equals(IRType.Primitive.LONG);
    }

    private boolean isFloat(IRType type) {
        return type != null && type.equals(IRType.Primitive.FLOAT);
    }

    private boolean isDouble(IRType type) {
        return type != null && type.equals(IRType.Primitive.DOUBLE);
    }

    private boolean isString(IRType type) {
        return type != null && (type.equals(IRType.Reference.STRING) ||
               (type instanceof IRType.Reference ref && ref.name().equals("java.lang.String")));
    }
}
