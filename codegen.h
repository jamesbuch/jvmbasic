#pragma once

#include "ast.h"
#include "builtin_functions.h"
#include <iostream>
#include <vector>
#include <map>
#include <memory>
#include <cstring>
#include <cmath>
#include <algorithm>

using namespace std;

using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Constant Pool Entry
using CpEntry = vector<u1>;

// Method information for multi-method generation
struct MethodInfo {
    u2 name_idx;
    u2 descriptor_idx;
    u2 access_flags;
    vector<u1> code;
    u2 max_stack;
    u2 max_locals;
};

struct ConstantPool {
    vector<CpEntry> entries;

    u2 addEntry(CpEntry e) {
        // Simple no dedup for brevity
        entries.push_back(move(e));
        return static_cast<u2>(entries.size());
    }

    u2 addUtf8(const string& s) {
        CpEntry e;
        e.push_back(1); // tag
        u2 len = static_cast<u2>(s.size());
        e.push_back(static_cast<u1>(len >> 8));
        e.push_back(static_cast<u1>(len & 0xFF));
        for (char c : s) e.push_back(static_cast<u1>(c));
        return addEntry(move(e));
    }

    u2 addString(u2 strIdx) {
        CpEntry e{8, static_cast<u1>(strIdx >> 8), static_cast<u1>(strIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addFloat(u4 bits) {
        CpEntry e{4};
        e.push_back(static_cast<u1>((bits >> 24) & 0xFF));
        e.push_back(static_cast<u1>((bits >> 16) & 0xFF));
        e.push_back(static_cast<u1>((bits >> 8) & 0xFF));
        e.push_back(static_cast<u1>(bits & 0xFF));
        return addEntry(move(e));
    }

    u2 addNameAndType(u2 nameIdx, u2 descIdx) {
        CpEntry e{12, static_cast<u1>(nameIdx >> 8), static_cast<u1>(nameIdx & 0xFF),
                  static_cast<u1>(descIdx >> 8), static_cast<u1>(descIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addClass(u2 nameIdx) {
        CpEntry e{7, static_cast<u1>(nameIdx >> 8), static_cast<u1>(nameIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addFieldRef(u2 classIdx, u2 natIdx) {
        CpEntry e{9, static_cast<u1>(classIdx >> 8), static_cast<u1>(classIdx & 0xFF),
                  static_cast<u1>(natIdx >> 8), static_cast<u1>(natIdx & 0xFF)};
        return addEntry(move(e));
    }

    u2 addMethodRef(u2 classIdx, u2 natIdx) {
        CpEntry e{10, static_cast<u1>(classIdx >> 8), static_cast<u1>(classIdx & 0xFF),
                   static_cast<u1>(natIdx >> 8), static_cast<u1>(natIdx & 0xFF)};
        return addEntry(move(e));
    }
};

// Label for branching
struct Label {
    int pos = -1;
    vector<int> patchSites;
};

// ClassFile
class ClassFile {
public:
    u4 magic = 0xCAFEBABE;
    u2 minor_version = 0;
    u2 major_version = 49; // Java 5 (no StackMapTable required, works on all modern JVMs)
    ConstantPool cp;
    u2 this_class_idx;
    u2 super_class_idx;
    u2 out_field_idx;
    u2 println_int_idx;
    u2 println_float_idx;
    u2 println_str_idx;
    u2 println_bool_idx;
    u2 print_int_idx;
    u2 print_float_idx;
    u2 print_str_idx;
    u2 print_bool_idx;
    u2 println_void_idx;
    u2 print_space_idx;
    u2 string_class_idx;
    u2 string_equals_idx;
    u2 scanner_class_idx;
    u2 scanner_init_idx;
    u2 scanner_nextline_idx;
    u2 system_in_idx;
    u2 integer_class_idx;
    u2 integer_parseint_idx;
    u2 float_class_idx;
    u2 float_parsefloat_idx;
    u2 main_name_idx;
    u2 main_desc_idx;
    u2 code_name_idx;

    vector<u1> code;  // Current method's code (for generation)
    u2 max_stack = 10;
    u2 max_locals = 1;
    u1 scanner_local = 0; // Local variable index for Scanner
    
    int labelCounter = 0;
    
    // Runtime support
    u2 basicruntime_class_idx = 0;
    map<string, u2> functionMethodRefs; // Cache of function name -> method ref index
    
    // Multiple methods support
    vector<MethodInfo> methods;
    map<string, Type> currentLocalTypes;  // Current function's local types (for load to access)

    void buildConstantPool() {
        // Utf8
        u2 simple_class = cp.addUtf8("BasicProgram");
        u2 object_class = cp.addUtf8("java/lang/Object");
        main_name_idx = cp.addUtf8("main");
        main_desc_idx = cp.addUtf8("([Ljava/lang/String;)V");
        u2 nat_main = cp.addNameAndType(main_name_idx, main_desc_idx);
        this_class_idx = cp.addClass(simple_class);
        super_class_idx = cp.addClass(object_class);

        // System.out
        u2 system_class = cp.addUtf8("java/lang/System");
        u2 out_name = cp.addUtf8("out");
        u2 out_desc = cp.addUtf8("Ljava/io/PrintStream;");
        u2 nat_out = cp.addNameAndType(out_name, out_desc);
        u2 sys_cls_idx = cp.addClass(system_class);
        out_field_idx = cp.addFieldRef(sys_cls_idx, nat_out);

        // PrintStream
        u2 ps_class = cp.addUtf8("java/io/PrintStream");
        u2 ps_cls_idx = cp.addClass(ps_class);
        u2 println_name = cp.addUtf8("println");
        u2 print_name = cp.addUtf8("print");

        // println methods (with newline)
        // println (I)V
        u2 pi_desc = cp.addUtf8("(I)V");
        u2 nat_pi = cp.addNameAndType(println_name, pi_desc);
        println_int_idx = cp.addMethodRef(ps_cls_idx, nat_pi);

        // println (F)V
        u2 pf_desc = cp.addUtf8("(F)V");
        u2 nat_pf = cp.addNameAndType(println_name, pf_desc);
        println_float_idx = cp.addMethodRef(ps_cls_idx, nat_pf);

        // println (Ljava/lang/String;)V
        u2 ps_desc = cp.addUtf8("(Ljava/lang/String;)V");
        u2 nat_ps = cp.addNameAndType(println_name, ps_desc);
        println_str_idx = cp.addMethodRef(ps_cls_idx, nat_ps);

        // println (Z)V for boolean
        u2 pb_desc = cp.addUtf8("(Z)V");
        u2 nat_pb = cp.addNameAndType(println_name, pb_desc);
        println_bool_idx = cp.addMethodRef(ps_cls_idx, nat_pb);

        // println ()V for empty newline
        u2 pv_desc = cp.addUtf8("()V");
        u2 nat_pv = cp.addNameAndType(println_name, pv_desc);
        println_void_idx = cp.addMethodRef(ps_cls_idx, nat_pv);

        // print methods (without newline)
        // print (I)V
        u2 nat_pri = cp.addNameAndType(print_name, pi_desc);
        print_int_idx = cp.addMethodRef(ps_cls_idx, nat_pri);

        // print (F)V
        u2 nat_prf = cp.addNameAndType(print_name, pf_desc);
        print_float_idx = cp.addMethodRef(ps_cls_idx, nat_prf);

        // print (Ljava/lang/String;)V
        u2 nat_prs = cp.addNameAndType(print_name, ps_desc);
        print_str_idx = cp.addMethodRef(ps_cls_idx, nat_prs);

        // print (Z)V for boolean
        u2 nat_prb = cp.addNameAndType(print_name, pb_desc);
        print_bool_idx = cp.addMethodRef(ps_cls_idx, nat_prb);

        // For comma separator: print a space
        u2 space_utf = cp.addUtf8(" ");
        print_space_idx = cp.addString(space_utf);

        // String.equals
        u2 string_class_name = cp.addUtf8("java/lang/String");
        string_class_idx = cp.addClass(string_class_name);
        u2 equals_name = cp.addUtf8("equals");
        u2 equals_desc = cp.addUtf8("(Ljava/lang/Object;)Z");
        u2 nat_equals = cp.addNameAndType(equals_name, equals_desc);
        string_equals_idx = cp.addMethodRef(string_class_idx, nat_equals);

        // Scanner for input
        u2 scanner_class_name = cp.addUtf8("java/util/Scanner");
        scanner_class_idx = cp.addClass(scanner_class_name);
        u2 init_name = cp.addUtf8("<init>");
        u2 scanner_init_desc = cp.addUtf8("(Ljava/io/InputStream;)V");
        u2 nat_scanner_init = cp.addNameAndType(init_name, scanner_init_desc);
        scanner_init_idx = cp.addMethodRef(scanner_class_idx, nat_scanner_init);
        u2 nextline_name = cp.addUtf8("nextLine");
        u2 nextline_desc = cp.addUtf8("()Ljava/lang/String;");
        u2 nat_nextline = cp.addNameAndType(nextline_name, nextline_desc);
        scanner_nextline_idx = cp.addMethodRef(scanner_class_idx, nat_nextline);

        // System.in
        u2 in_name = cp.addUtf8("in");
        u2 in_desc = cp.addUtf8("Ljava/io/InputStream;");
        u2 nat_in = cp.addNameAndType(in_name, in_desc);
        system_in_idx = cp.addFieldRef(sys_cls_idx, nat_in);

        // Integer.parseInt
        u2 integer_class_name = cp.addUtf8("java/lang/Integer");
        integer_class_idx = cp.addClass(integer_class_name);
        u2 parseint_name = cp.addUtf8("parseInt");
        u2 parseint_desc = cp.addUtf8("(Ljava/lang/String;)I");
        u2 nat_parseint = cp.addNameAndType(parseint_name, parseint_desc);
        integer_parseint_idx = cp.addMethodRef(integer_class_idx, nat_parseint);

        // Float.parseFloat
        u2 float_class_name = cp.addUtf8("java/lang/Float");
        float_class_idx = cp.addClass(float_class_name);
        u2 parsefloat_name = cp.addUtf8("parseFloat");
        u2 parsefloat_desc = cp.addUtf8("(Ljava/lang/String;)F");
        u2 nat_parsefloat = cp.addNameAndType(parsefloat_name, parsefloat_desc);
        float_parsefloat_idx = cp.addMethodRef(float_class_idx, nat_parsefloat);

        // BasicRuntime for standard library functions
        u2 basicruntime_class_name = cp.addUtf8("basicrt/BasicRuntime");
        basicruntime_class_idx = cp.addClass(basicruntime_class_name);

        code_name_idx = cp.addUtf8("Code");
    }
    
    // Get or create method reference for a function
    u2 getFunctionMethodRef(const string& funcName) {
        // Check cache
        if (functionMethodRefs.count(funcName)) {
            return functionMethodRefs[funcName];
        }
        
        // Look up function signature
        auto it = builtinFunctions.find(funcName);
        if (it == builtinFunctions.end()) {
            throw runtime_error("Unknown function: " + funcName);
        }
        
        const FunctionSig& sig = it->second;
        
        // Add to constant pool
        u2 method_name = cp.addUtf8(sig.javaMethod);
        u2 method_desc = cp.addUtf8(sig.descriptor);
        u2 nat = cp.addNameAndType(method_name, method_desc);
        u2 method_ref = cp.addMethodRef(basicruntime_class_idx, nat);
        
        // Cache it
        functionMethodRefs[funcName] = method_ref;
        return method_ref;
    }

    void emit(u1 byte) { code.push_back(byte); }
    void emit(u1 opc, u1 arg) { emit(opc); emit(arg); }
    void emit(u1 opc, u2 arg) {
        emit(opc);
        emit(static_cast<u1>(arg >> 8));
        emit(static_cast<u1>(arg & 0xFF));
    }
    void ldc(u2 idx) {
        if (idx <= 255) {
            emit(0x12, static_cast<u1>(idx));
        } else {
            emit(0x13, idx);
        }
    }

    void getstatic(u2 idx) { emit(0xB2, idx); }
    void invokevirtual(u2 idx) { emit(0xB6, idx); }
    void iconst(int i) {
        if (i == -1) emit(0x02);
        else if (i >= 0 && i <= 5) emit(0x03 + static_cast<u1>(i));
        else if (i >= -128 && i <= 127) emit(0x10, static_cast<u1>(i));
        else emit(0x11, static_cast<u2>(i));
    }
    void fconst(float f) {
        if (f == 0.0f) emit(0x0B);
        else if (f == 1.0f) emit(0x0C);
        else if (f == 2.0f) emit(0x0D);
        else {
            uint32_t bits = *reinterpret_cast<uint32_t*>(&f);
            u2 fidx = cp.addFloat(bits);
            ldc(fidx);
        }
    }
    void iload(u1 idx) {
        if (idx < 4) emit(0x1A + idx);
        else emit(0x15, idx);
    }
    void fload(u1 idx) {
        if (idx < 4) emit(0x22 + idx);
        else emit(0x17, idx);
    }
    void aload(u1 idx) {
        if (idx < 4) emit(0x2A + idx);
        else emit(0x19, idx);
    }
    void istore(u1 idx) {
        if (idx < 4) emit(0x3B + idx);
        else emit(0x36, idx);
    }
    void fstore(u1 idx) {
        if (idx < 4) emit(0x43 + idx);
        else emit(0x38, idx);
    }
    void astore(u1 idx) {
        if (idx < 4) emit(0x4B + idx);
        else emit(0x3A, idx);
    }
    void iadd() { emit(0x60); }
    void isub() { emit(0x64); }
    void imul() { emit(0x68); }
    void idiv() { emit(0x6C); }
    void fadd() { emit(0x62); }
    void fsub() { emit(0x66); }
    void fmul() { emit(0x6A); }
    void fdiv() { emit(0x6E); }
    void irem() { emit(0x70); }
    void frem() { emit(0x72); }
    void ineg() { emit(0x74); }
    void fneg() { emit(0x76); }
    void i2f() { emit(0x86); } // Added i2f instruction
    void _return() { emit(0xB1); }
    void ireturn() { emit(0xAC); }
    void freturn() { emit(0xAE); }
    void areturn() { emit(0xB0); }
    void new_(u2 idx) { emit(0xBB, idx); }
    void dup() { emit(0x59); }
    
    void ldc_string(const string& s) {
        u2 str_utf8_idx = cp.addUtf8(s);
        u2 str_idx = cp.addString(str_utf8_idx);
        if (str_idx <= 255) {
            emit(0x12, static_cast<u1>(str_idx)); // ldc
        } else {
            emit(0x13, str_idx); // ldc_w
        }
    }
    void invokespecial(u2 idx) { emit(0xB7, idx); }
    void invokestatic(u2 idx) { emit(0xB8, idx); }
    
    // Array instructions
    void newarray_int() { emit(0xBC); emit(10); }      // T_INT = 10
    void newarray_float() { emit(0xBC); emit(6); }     // T_FLOAT = 6
    void newarray_bool() { emit(0xBC); emit(4); }      // T_BOOLEAN = 4
    void anewarray(u2 idx) { emit(0xBD, idx); }        // For String arrays
    void iaload() { emit(0x2E); }
    void faload() { emit(0x30); }
    void aaload() { emit(0x32); }
    void baload() { emit(0x33); }
    void iastore() { emit(0x4F); }
    void fastore() { emit(0x51); }
    void aastore() { emit(0x53); }
    void bastore() { emit(0x54); }
    
    // Branching instructions
    void ifeq(Label& L) { emitBranch(0x99, L); }
    void ifne(Label& L) { emitBranch(0x9A, L); }
    void iflt(Label& L) { emitBranch(0x9B, L); }
    void ifge(Label& L) { emitBranch(0x9C, L); }
    void ifgt(Label& L) { emitBranch(0x9D, L); }
    void ifle(Label& L) { emitBranch(0x9E, L); }
    void if_icmpeq(Label& L) { emitBranch(0x9F, L); }
    void if_icmpne(Label& L) { emitBranch(0xA0, L); }
    void if_icmplt(Label& L) { emitBranch(0xA1, L); }
    void if_icmpge(Label& L) { emitBranch(0xA2, L); }
    void if_icmpgt(Label& L) { emitBranch(0xA3, L); }
    void if_icmple(Label& L) { emitBranch(0xA4, L); }
    void goto_(Label& L) { emitBranch(0xA7, L); }
    
    // Float comparison instructions
    void fcmpl() { emit(0x95); }
    void fcmpg() { emit(0x96); }
    
    // Label management
    int position() const { return static_cast<int>(code.size()); }
    
    void mark(Label& L) {
        L.pos = position();
        for (int site : L.patchSites) {
            patchJump(site, L.pos);
        }
        L.patchSites.clear();
    }
    
    void emitBranch(u1 opcode, Label& L) {
        emit(opcode);
        int site = position();
        emit(static_cast<u1>(0)); // Placeholder
        emit(static_cast<u1>(0)); // Placeholder
        if (L.pos >= 0) {
            patchJump(site, L.pos);
        } else {
            L.patchSites.push_back(site);
        }
    }
    
    void patchJump(int site, int target) {
        int16_t offset = static_cast<int16_t>(target - (site - 1));
        code[site] = static_cast<u1>((offset >> 8) & 0xFF);
        code[site + 1] = static_cast<u1>(offset & 0xFF);
    }
    
    void loadComparison(const CmpOp& co, map<string, u1>& varIdx) {
        // Determine the operand types (promote if needed)
        Type leftType = co.left->type;
        Type rightType = co.right->type;
        
        // Handle string comparisons
        if (leftType == Type::String && rightType == Type::String) {
            load(*co.left, varIdx);
            load(*co.right, varIdx);
            invokevirtual(string_equals_idx); // returns boolean (0 or 1)
            
            // Handle negation for != operator
            if (co.op == Op::Ne) {
                // XOR with 1 to flip the boolean
                iconst(1);
                emit(0x82); // ixor
            } else if (co.op != Op::Eq) {
                throw runtime_error("Only == and <> are supported for string comparisons");
            }
            return;
        }
        
        // Handle float comparisons with epsilon
        if (leftType == Type::Float || rightType == Type::Float) {
            load(*co.left, varIdx);
            if (leftType == Type::Int) i2f();
            load(*co.right, varIdx);
            if (rightType == Type::Int) i2f();
            
            // For float comparisons, we use fcmpg/fcmpl and then conditional branches
            // fcmpg: pushes 1 if left > right, 0 if equal, -1 if left < right (or either NaN)
            fcmpg();
            
            Label trueLabel, endLabel;
            switch (co.op) {
                case Op::Lt: iflt(trueLabel); break;
                case Op::Gt: ifgt(trueLabel); break;
                case Op::Le: ifle(trueLabel); break;
                case Op::Ge: ifge(trueLabel); break;
                case Op::Eq: ifeq(trueLabel); break;
                case Op::Ne: ifne(trueLabel); break;
                default: throw runtime_error("Unknown comparison operator");
            }
            iconst(0);
            goto_(endLabel);
            mark(trueLabel);
            iconst(1);
            mark(endLabel);
            return;
        }
        
        // Handle integer/boolean comparisons
        if ((leftType == Type::Int || leftType == Type::Bool) && 
            (rightType == Type::Int || rightType == Type::Bool)) {
            load(*co.left, varIdx);
            load(*co.right, varIdx);
            
            Label trueLabel, endLabel;
            switch (co.op) {
                case Op::Lt: if_icmplt(trueLabel); break;
                case Op::Gt: if_icmpgt(trueLabel); break;
                case Op::Le: if_icmple(trueLabel); break;
                case Op::Ge: if_icmpge(trueLabel); break;
                case Op::Eq: if_icmpeq(trueLabel); break;
                case Op::Ne: if_icmpne(trueLabel); break;
                default: throw runtime_error("Unknown comparison operator");
            }
            iconst(0);
            goto_(endLabel);
            mark(trueLabel);
            iconst(1);
            mark(endLabel);
            return;
        }
        
        throw runtime_error("Type mismatch in comparison");
    }

    void load(const Expr& e, map<string, u1>& varIdx) {
        if (e.kind == ExprKind::Num) {
            const NumLit& nl = get<NumLit>(e.data);
            if (e.type == Type::Int) {
                iconst(static_cast<int>(nl.value));
                if (e.type != Type::Float) return; // No conversion needed
                i2f(); // Convert int to float for Float-typed expression
            } else {
                fconst(static_cast<float>(nl.value));
            }
        } else if (e.kind == ExprKind::Str) {
            const StrLit& sl = get<StrLit>(e.data);
            u2 utfIdx = cp.addUtf8(sl.value);
            u2 strIdx = cp.addString(utfIdx);
            ldc(strIdx);
        } else if (e.kind == ExprKind::BoolLit) {
            const BoolLit& bl = get<BoolLit>(e.data);
            iconst(bl.value ? 1 : 0);
        } else if (e.kind == ExprKind::Var) {
            const VarRef& vr = get<VarRef>(e.data);
            u1 idx = varIdx.at(vr.name);
            
            if (vr.index) {
                // Array element access: load array, load index, load element
                aload(idx);  // Load array reference
                load(*vr.index, varIdx);  // Load index
                
                // Convert Float index to Int (for FOR loop variables)
                if (vr.index->type == Type::Float) {
                    emit(0x8B);  // f2i - float to int conversion
                }
                
                // Load element based on type
                if (e.type == Type::Int) iaload();
                else if (e.type == Type::Float) faload();
                else if (e.type == Type::Bool) baload();
                else if (e.type == Type::String) aaload();
            } else {
                // Scalar variable access or array reference
                // Check currentLocalTypes for function parameters
                Type actualType = e.type;
                auto localIt = currentLocalTypes.find(vr.name);
                if (localIt != currentLocalTypes.end()) {
                    actualType = localIt->second;
                }
                
                // Arrays are reference types - use aload
                if (actualType == Type::IntArray || actualType == Type::FloatArray ||
                    actualType == Type::StringArray || actualType == Type::BoolArray) {
                    aload(idx);
                }
                // Scalar types
                else if (actualType == Type::Int || actualType == Type::Bool) {
                    iload(idx);
                    if (e.type == Type::Float) i2f(); // Convert int to float if needed
                } else if (actualType == Type::Float) {
                    fload(idx);
                } else {
                    // String
                    aload(idx);
                }
            }
        } else if (e.kind == ExprKind::Cmp) {
            const CmpOp& co = get<CmpOp>(e.data);
            loadComparison(co, varIdx);
        } else if (e.kind == ExprKind::Unary) {
            const UnaryExpr& ue = get<UnaryExpr>(e.data);
            load(*ue.operand, varIdx);
            if (e.type == Type::Int) {
                ineg();
            } else if (e.type == Type::Float) {
                fneg();
            }
        } else if (e.kind == ExprKind::Call) {
            const CallExpr& ce = get<CallExpr>(e.data);
            
            // Check if it's a built-in function (uppercase names)
            auto builtinIt = builtinFunctions.find(ce.name);
            if (builtinIt != builtinFunctions.end()) {
                // Built-in function
                const FunctionSig& sig = builtinIt->second;
                
                // Load arguments
                for (size_t i = 0; i < ce.args.size(); ++i) {
                    load(*ce.args[i], varIdx);
                    
                    // Convert Int to Float if needed
                    Type expectedType = sig.paramTypes[i];
                    Type actualType = ce.args[i]->type;
                    if (expectedType == Type::Float && actualType == Type::Int) {
                        i2f();
                    }
                }
                
                // Call built-in function
                u2 methodRef = getFunctionMethodRef(ce.name);
                invokestatic(methodRef);
            } else {
                // User-defined function - use userFunctions for correct signature
                auto userIt = userFunctions.find(ce.name);
                if (userIt != userFunctions.end()) {
                    const auto& funcSig = userIt->second;
                    const vector<Type>& paramTypes = funcSig.first;
                    const Type& returnType = funcSig.second;
                    
                    // Load arguments with type matching
                    for (size_t i = 0; i < ce.args.size(); ++i) {
                        load(*ce.args[i], varIdx);
                        
                        // Convert Int to Float if parameter expects Float
                        if (i < paramTypes.size()) {
                            Type expectedType = paramTypes[i];
                            Type actualType = ce.args[i]->type;
                            if (expectedType == Type::Float && actualType == Type::Int) {
                                i2f();
                            }
                        }
                    }
                    
                    // Build descriptor from actual function signature
                    string descriptor = "(";
                    for (const auto& ptype : paramTypes) {
                        if (ptype == Type::IntArray) descriptor += "[I";
                        else if (ptype == Type::FloatArray) descriptor += "[F";
                        else if (ptype == Type::StringArray) descriptor += "[Ljava/lang/String;";
                        else if (ptype == Type::BoolArray) descriptor += "[Z";
                        else if (ptype == Type::Int || ptype == Type::Bool) descriptor += "I";
                        else if (ptype == Type::Float) descriptor += "F";
                        else if (ptype == Type::String) descriptor += "Ljava/lang/String;";
                    }
                    descriptor += ")";
                    if (returnType == Type::Int || returnType == Type::Bool) descriptor += "I";
                    else if (returnType == Type::Float) descriptor += "F";
                    else if (returnType == Type::String) descriptor += "Ljava/lang/String;";
                    
                    // Create method reference if not cached
                    string funcKey = ce.name;  // Use just name (signature is consistent now)
                    if (functionMethodRefs.find(funcKey) == functionMethodRefs.end()) {
                        u2 name_idx = cp.addUtf8(ce.name);
                        u2 desc_idx = cp.addUtf8(descriptor);
                        u2 nat_idx = cp.addNameAndType(name_idx, desc_idx);
                        functionMethodRefs[funcKey] = cp.addMethodRef(this_class_idx, nat_idx);
                    }
                    
                    // Call the user-defined function
                    invokestatic(functionMethodRefs[funcKey]);
                }
            }
        } else if (e.kind == ExprKind::Bin) {
            const BinOp& bo = get<BinOp>(e.data);
            load(*bo.left, varIdx);
            // If binary op is Float but left operand is Int, convert
            if (e.type == Type::Float && bo.left->type == Type::Int) {
                i2f();
            }
            load(*bo.right, varIdx);
            // If binary op is Float but right operand is Int, convert
            if (e.type == Type::Float && bo.right->type == Type::Int) {
                i2f();
            }
            if (e.type == Type::Int) {
                switch (bo.op) {
                    case Op::Add: iadd(); break;
                    case Op::Sub: isub(); break;
                    case Op::Mul: imul(); break;
                    case Op::Div: idiv(); break;
                    case Op::Mod: irem(); break;
                }
            } else if (e.type == Type::Float) {
                switch (bo.op) {
                    case Op::Add: fadd(); break;
                    case Op::Sub: fsub(); break;
                    case Op::Mul: fmul(); break;
                    case Op::Div: fdiv(); break;
                    case Op::Mod: frem(); break;
                }
            }
        }
    }

    void genStmt(const Stmt& s, map<string, u1>& varIdx, u1& nextLocal, const map<string, Type>& knownTypes) {
        if (s.kind == StmtKind::Print) {
            const PrintStmt& ps = get<PrintStmt>(s.data);
            
            for (size_t i = 0; i < ps.exprs.size(); ++i) {
                const auto& expr = ps.exprs[i];
                bool isLast = (i == ps.exprs.size() - 1);
                
                // Load System.out
                getstatic(out_field_idx);
                // Load expression value
                load(*expr, varIdx);
                
                // Determine actual type (check currentLocalTypes for function/sub parameters)
                Type actualType = expr->type;
                if (expr->kind == ExprKind::Var) {
                    const VarRef& vr = get<VarRef>(expr->data);
                    auto localIt = currentLocalTypes.find(vr.name);
                    if (localIt != currentLocalTypes.end()) {
                        actualType = localIt->second;
                    }
                }
                
                // Determine which print method to use based on actual type
                if (isLast && ps.addNewline) {
                    // Last expression with newline: use println
                    if (actualType == Type::Int) invokevirtual(println_int_idx);
                    else if (actualType == Type::Float) invokevirtual(println_float_idx);
                    else if (actualType == Type::Bool) invokevirtual(println_bool_idx);
                    else invokevirtual(println_str_idx);
                } else {
                    // Not last, or no newline: use print (no newline)
                    if (actualType == Type::Int) invokevirtual(print_int_idx);
                    else if (actualType == Type::Float) invokevirtual(print_float_idx);
                    else if (actualType == Type::Bool) invokevirtual(print_bool_idx);
                    else invokevirtual(print_str_idx);
                }
                
                // Print separator if not last
                if (!isLast) {
                    PrintSep sep = ps.seps[i];
                    if (sep == PrintSep::Comma) {
                        // Print a space for comma separator
                        getstatic(out_field_idx);
                        ldc(print_space_idx);
                        invokevirtual(print_str_idx);
                    }
                    // Semicolon separator: print nothing (no space)
                }
            }
            
            // If no newline at end but expressions were printed, we're done
            // Otherwise if empty PRINT, add newline
            if (ps.exprs.empty()) {
                getstatic(out_field_idx);
                invokevirtual(println_void_idx);
            }
        } else if (s.kind == StmtKind::Let) {
            const LetStmt& ls = get<LetStmt>(s.data);
            
            if (ls.index) {
                // Array element assignment: LET arr(index) = value
                u1 idx = varIdx.at(ls.var);
                aload(idx);  // Load array reference
                load(*ls.index, varIdx);  // Load index
                
                // Convert Float index to Int (for FOR loop variables)
                if (ls.index->type == Type::Float) {
                    emit(0x8B);  // f2i - float to int conversion
                }
                
                load(*ls.expr, varIdx);  // Load value
                
                // Store based on type
                if (ls.expr->type == Type::Int) iastore();
                else if (ls.expr->type == Type::Float) fastore();
                else if (ls.expr->type == Type::Bool) bastore();
                else if (ls.expr->type == Type::String) aastore();
            } else {
                // Scalar assignment
                if (varIdx.find(ls.var) == varIdx.end()) {
                    varIdx[ls.var] = nextLocal++;
                }
                u1 idx = varIdx[ls.var];
                load(*ls.expr, varIdx);
                if (ls.expr->type == Type::Int || ls.expr->type == Type::Bool) istore(idx);
                else if (ls.expr->type == Type::Float) fstore(idx);
                else astore(idx);
                max_locals = max(max_locals, static_cast<u2>(nextLocal));
            }
        } else if (s.kind == StmtKind::Input) {
            const InputStmt& is = get<InputStmt>(s.data);
            u1 idx = varIdx.at(is.var);
            Type varType = knownTypes.at(is.var);
            
            // Load scanner
            aload(scanner_local);
            // Call nextLine()
            invokevirtual(scanner_nextline_idx);
            
            // Convert string to appropriate type
            if (varType == Type::Int) {
                // Integer.parseInt(string)
                invokestatic(integer_parseint_idx);
                istore(idx);
            } else if (varType == Type::Float) {
                // Float.parseFloat(string)
                invokestatic(float_parsefloat_idx);
                fstore(idx);
            } else if (varType == Type::Bool) {
                // Check if string equals "true" (case-insensitive)
                // For simplicity: use String.toLowerCase().equals("true")
                u2 tolower_name = cp.addUtf8("toLowerCase");
                u2 tolower_desc = cp.addUtf8("()Ljava/lang/String;");
                u2 nat_tolower = cp.addNameAndType(tolower_name, tolower_desc);
                u2 tolower_idx = cp.addMethodRef(string_class_idx, nat_tolower);
                
                invokevirtual(tolower_idx);
                u2 true_utf = cp.addUtf8("true");
                u2 true_str = cp.addString(true_utf);
                ldc(true_str);
                invokevirtual(string_equals_idx);
                // Result is 0 or 1 (boolean as int)
                istore(idx);
            } else {
                // String: just store directly
                astore(idx);
            }
        } else if (s.kind == StmtKind::Dim) {
            const DimStmt& ds = get<DimStmt>(s.data);
            
            // Allocate local variable for array
            varIdx[ds.var] = nextLocal++;
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
            u1 idx = varIdx[ds.var];
            
            // Load size and create array
            load(*ds.size, varIdx);
            
            Type arrType = knownTypes.at(ds.var);
            if (arrType == Type::IntArray) newarray_int();
            else if (arrType == Type::FloatArray) newarray_float();
            else if (arrType == Type::BoolArray) newarray_bool();
            else if (arrType == Type::StringArray) anewarray(string_class_idx);
            
            // Store array reference
            astore(idx);
            
            // Initialize all elements with initVal
            // For simplicity, we'll initialize in a loop at runtime
            // Save size to a temp variable
            load(*ds.size, varIdx);
            u1 sizeVar = nextLocal++;
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
            istore(sizeVar);
            
            // Initialize counter to 0
            iconst(0);
            u1 counterVar = nextLocal++;
            max_locals = max(max_locals, static_cast<u2>(nextLocal));
            istore(counterVar);
            
            // Loop: while counter < size
            Label loopStart, loopEnd;
            mark(loopStart);
            
            // Check: counter < size
            iload(counterVar);
            iload(sizeVar);
            if_icmpge(loopEnd);
            
            // arr[counter] = initVal
            aload(idx);  // Load array
            iload(counterVar);  // Load index
            load(*ds.initVal, varIdx);  // Load init value
            
            // Store based on type
            if (ds.initVal->type == Type::Int) iastore();
            else if (ds.initVal->type == Type::Float) fastore();
            else if (ds.initVal->type == Type::Bool) bastore();
            else if (ds.initVal->type == Type::String) aastore();
            
            // counter++
            iload(counterVar);
            iconst(1);
            iadd();
            istore(counterVar);
            
            // goto loopStart
            goto_(loopStart);
            mark(loopEnd);
        } else if (s.kind == StmtKind::For) {
            const ForStmt& fs = get<ForStmt>(s.data);
            
            // Allocate loop variable if needed
            if (varIdx.find(fs.var) == varIdx.end()) {
                varIdx[fs.var] = nextLocal++;
                max_locals = max(max_locals, static_cast<u2>(nextLocal));
            }
            u1 varSlot = varIdx[fs.var];
            
            // Get loop variable type (safe lookup)
            Type varType;
            auto typeIt = knownTypes.find(fs.var);
            if (typeIt != knownTypes.end()) {
                varType = typeIt->second;
            } else {
                // Local FOR loop variable - use type from start expression
                varType = fs.start->type;
            }
            
            // Initialize: var = start
            load(*fs.start, varIdx);
            if (varType == Type::Int) istore(varSlot);
            else fstore(varSlot);
            
            // Determine step value (default 1)
            bool hasStep = (fs.step != nullptr);
            
            Label loopStart, loopEnd;
            mark(loopStart);
            
            // Check condition: var <= end (or >= for negative step)
            if (varType == Type::Int) {
                iload(varSlot);
                load(*fs.end, varIdx);
                // For simplicity, always use <= (assume positive step or user knows what they're doing)
                if_icmpgt(loopEnd);
            } else {
                // Float loop
                fload(varSlot);
                load(*fs.end, varIdx);
                if (fs.start->type == Type::Int) i2f();
                fcmpg();
                ifgt(loopEnd);
            }
            
            // Body
            for (const auto& stmt : fs.body) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            // Increment: var += step (or += 1)
            if (varType == Type::Int) {
                iload(varSlot);
                if (hasStep) {
                    load(*fs.step, varIdx);
                } else {
                    iconst(1);
                }
                iadd();
                istore(varSlot);
            } else {
                fload(varSlot);
                if (hasStep) {
                    load(*fs.step, varIdx);
                    if (fs.step->type == Type::Int) i2f();
                } else {
                    fconst(1.0f);
                }
                fadd();
                fstore(varSlot);
            }
            
            goto_(loopStart);
            mark(loopEnd);
        } else if (s.kind == StmtKind::While) {
            const WhileStmt& ws = get<WhileStmt>(s.data);
            
            Label loopStart, loopEnd;
            mark(loopStart);
            
            // Check condition
            load(*ws.cond, varIdx);
            ifeq(loopEnd);
            
            // Body
            for (const auto& stmt : ws.body) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            goto_(loopStart);
            mark(loopEnd);
        } else if (s.kind == StmtKind::DoWhile) {
            const DoWhileStmt& dws = get<DoWhileStmt>(s.data);
            
            Label loopStart;
            mark(loopStart);
            
            // Body (executes at least once)
            for (const auto& stmt : dws.body) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            // Check condition
            load(*dws.cond, varIdx);
            if (dws.isUntil) {
                // UNTIL: loop while condition is false
                ifeq(loopStart);
            } else {
                // WHILE: loop while condition is true
                ifne(loopStart);
            }
        } else if (s.kind == StmtKind::If) {
            const IfStmt& ifs = get<IfStmt>(s.data);
            
            // Evaluate main condition
            load(*ifs.cond, varIdx);
            Label nextLabel, endLabel;
            ifeq(nextLabel); // Jump to next clause if condition is false (0)
            
            // Generate THEN body
            for (const auto& stmt : ifs.thenBody) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            goto_(endLabel);
            
            // Generate ELSE IF clauses
            for (const auto& elseIf : ifs.elseIfs) {
                mark(nextLabel);
                nextLabel = Label(); // Create new label for next clause
                
                load(*elseIf.cond, varIdx);
                ifeq(nextLabel);
                
                for (const auto& stmt : elseIf.body) {
                    genStmt(*stmt, varIdx, nextLocal, knownTypes);
                }
                goto_(endLabel);
            }
            
            // Generate ELSE body
            mark(nextLabel);
            for (const auto& stmt : ifs.elseBody) {
                genStmt(*stmt, varIdx, nextLocal, knownTypes);
            }
            
            mark(endLabel);
        } else if (s.kind == StmtKind::Return) {
            const ReturnStmt& rs = get<ReturnStmt>(s.data);
            if (rs.expr) {
                // Load return value
                load(*rs.expr, varIdx);
                // Generate appropriate return instruction
                if (rs.expr->type == Type::Int || rs.expr->type == Type::Bool) {
                    ireturn();
                } else if (rs.expr->type == Type::Float) {
                    freturn();
                } else if (rs.expr->type == Type::String) {
                    areturn();
                }
            } else {
                // Void return
                _return();
            }
        } else if (s.kind == StmtKind::CallStmt) {
            const CallStmtNode& cs = get<CallStmtNode>(s.data);
            // Load arguments
            for (const auto& arg : cs.args) {
                load(*arg, varIdx);
            }
            // Build method descriptor
            string descriptor = "(";
            for (const auto& arg : cs.args) {
                if (arg->type == Type::Int || arg->type == Type::Bool) descriptor += "I";
                else if (arg->type == Type::Float) descriptor += "F";
                else if (arg->type == Type::String) descriptor += "Ljava/lang/String;";
            }
            descriptor += ")V";  // Subs return void
            
            // Create method reference if not cached
            string funcKey = cs.name + descriptor;
            if (functionMethodRefs.find(funcKey) == functionMethodRefs.end()) {
                u2 name_idx = cp.addUtf8(cs.name);
                u2 desc_idx = cp.addUtf8(descriptor);
                u2 nat_idx = cp.addNameAndType(name_idx, desc_idx);
                functionMethodRefs[funcKey] = cp.addMethodRef(this_class_idx, nat_idx);
            }
            
            // Call the user-defined sub
            invokestatic(functionMethodRefs[funcKey]);
        }
    }

    // Generate user-defined function method
    void generateFunction(const FunctionDecl& fd) {
        // Reset code for new method
        code.clear();
        max_stack = 10;
        u1 nextLocal = 0; // Note: for static methods, slot 0 is first param (no 'this')
        
        // Map parameters to local slots
        map<string, u1> varIdx;
        for (const auto& param : fd.params) {
            varIdx[param.name] = nextLocal++;
        }
        max_locals = nextLocal;
        
        // Build parameter types map for genStmt (use ACTUAL inferred types)
        map<string, Type> localTypes;
        for (const auto& param : fd.params) {
            localTypes[param.name] = param.type;  // Use actual inferred type!
        }
        
        // Set current local types for load() to access
        currentLocalTypes = localTypes;
        
        // Generate body
        for (const auto& stmt : fd.body) {
            genStmt(*stmt, varIdx, nextLocal, localTypes);
        }
        
        // If no explicit return, add default return of 0 or empty string
        if (fd.returnType == Type::Int) {
            iconst(0);
            ireturn();
        } else if (fd.returnType == Type::Float) {
            fconst(0.0f);
            freturn();
        } else if (fd.returnType == Type::String) {
            ldc_string("");
            areturn();
        } else if (fd.returnType == Type::Bool) {
            iconst(0);
            ireturn();
        }
        
        // Build method descriptor using ACTUAL parameter types
        string descriptor = "(";
        for (const auto& param : fd.params) {
            Type ptype = param.type;
            // Arrays use [ prefix
            if (ptype == Type::IntArray) descriptor += "[I";
            else if (ptype == Type::FloatArray) descriptor += "[F";
            else if (ptype == Type::StringArray) descriptor += "[Ljava/lang/String;";
            else if (ptype == Type::BoolArray) descriptor += "[Z";
            // Scalars
            else if (ptype == Type::Int || ptype == Type::Bool) descriptor += "I";
            else if (ptype == Type::Float) descriptor += "F";
            else if (ptype == Type::String) descriptor += "Ljava/lang/String;";
        }
        descriptor += ")";
        if (fd.returnType == Type::Int || fd.returnType == Type::Bool) descriptor += "I";
        else if (fd.returnType == Type::Float) descriptor += "F";
        else if (fd.returnType == Type::String) descriptor += "Ljava/lang/String;";
        
        // Add name and descriptor to constant pool
        u2 name_idx = cp.addUtf8(fd.name);
        u2 desc_idx = cp.addUtf8(descriptor);
        
        // Save method
        methods.push_back(MethodInfo{
            name_idx,
            desc_idx,
            0x0009, // public static
            code,
            max_stack,
            max_locals
        });
    }
    
    // Generate user-defined sub method
    void generateSub(const SubDecl& sd) {
        // Reset code for new method
        code.clear();
        max_stack = 10;
        u1 nextLocal = 0;
        
        // Map parameters to local slots
        map<string, u1> varIdx;
        for (const auto& param : sd.params) {
            varIdx[param.name] = nextLocal++;
        }
        max_locals = nextLocal;
        
        // Use inferred parameter types from call sites
        map<string, Type> paramTypes;
        for (const auto& param : sd.params) {
            paramTypes[param.name] = param.type;  // Use inferred type, not String default
        }
        
        // Build parameter types map for genStmt
        map<string, Type> localTypes;
        for (const auto& param : sd.params) {
            localTypes[param.name] = paramTypes[param.name];
        }
        
        // Set current local types for load() to access
        currentLocalTypes = localTypes;
        
        // Generate body
        for (const auto& stmt : sd.body) {
            genStmt(*stmt, varIdx, nextLocal, localTypes);
        }
        
        // Add return
        _return();
        
        // Build method descriptor
        string descriptor = "(";
        for (const auto& param : sd.params) {
            Type ptype = paramTypes[param.name];
            if (ptype == Type::Int || ptype == Type::Bool) descriptor += "I";
            else if (ptype == Type::Float) descriptor += "F";
            else if (ptype == Type::String) descriptor += "Ljava/lang/String;";
        }
        descriptor += ")V";
        
        // Add name and descriptor to constant pool
        u2 name_idx = cp.addUtf8(sd.name);
        u2 desc_idx = cp.addUtf8(descriptor);
        
        // Save method
        methods.push_back(MethodInfo{
            name_idx,
            desc_idx,
            0x0009, // public static
            code,
            max_stack,
            max_locals
        });
    }

    void generate(const vector<DeclPtr>& declarations, const vector<StmtPtr>& program, const map<string, Type>& knownTypes) {
        // Build userFunctions map from declarations for use in load()
        for (const auto& decl : declarations) {
            if (decl->kind == DeclKind::Function) {
                const FunctionDecl& fd = get<FunctionDecl>(decl->data);
                vector<Type> paramTypes;
                for (const auto& p : fd.params) paramTypes.push_back(p.type);
                userFunctions[fd.name] = {paramTypes, fd.returnType};
            }
        }
        
        // Generate methods for user-defined functions and subs
        for (const auto& decl : declarations) {
            if (decl->kind == DeclKind::Function) {
                const FunctionDecl& fd = get<FunctionDecl>(decl->data);
                generateFunction(fd);
            } else if (decl->kind == DeclKind::Sub) {
                const SubDecl& sd = get<SubDecl>(decl->data);
                generateSub(sd);
            }
        }
        
        // Generate main method
        code.clear();  // Reset code for main method
        currentLocalTypes.clear();  // Clear function local types
        map<string, u1> varIdx;
        u1 nextLocal = 1;
        max_locals = 1;
        max_stack = 10;
        
        // Initialize Scanner for INPUT (allocate before other variables)
        scanner_local = nextLocal++;
        max_locals = max(max_locals, static_cast<u2>(nextLocal));
        
        // new Scanner(System.in)
        new_(scanner_class_idx);
        dup();
        getstatic(system_in_idx);
        invokespecial(scanner_init_idx);
        astore(scanner_local);
        
        for (const auto& sp : program) {
            genStmt(*sp, varIdx, nextLocal, knownTypes);
        }
        _return();
        
        // Save main method
        methods.push_back(MethodInfo{
            main_name_idx,
            main_desc_idx,
            0x0009, // public static
            code,
            max_stack,
            max_locals
        });
    }

    void write(ostream& out) {
        writeU4(out, magic);
        writeU2(out, minor_version);
        writeU2(out, major_version);

        u2 cp_count = static_cast<u2>(cp.entries.size() + 1);
        writeU2(out, cp_count);
        for (const auto& entry : cp.entries) {
            for (u1 b : entry) {
                out.put(b);
            }
        }

        writeU2(out, 0x0021); // public, super
        writeU2(out, this_class_idx);
        writeU2(out, super_class_idx);
        writeU2(out, 0); // interfaces_count
        writeU2(out, 0); // fields_count

        // Write all methods
        writeU2(out, static_cast<u2>(methods.size())); // methods_count
        
        for (const auto& method : methods) {
            writeU2(out, method.access_flags);
            writeU2(out, method.name_idx);
            writeU2(out, method.descriptor_idx);
            writeU2(out, 1); // attributes_count (Code attribute)

            // Code attribute
            auto start_pos = out.tellp();
            writeU2(out, code_name_idx);
            auto len_pos = out.tellp();
            writeU4(out, 0); // placeholder for attribute_length

            writeU2(out, method.max_stack);
            writeU2(out, method.max_locals);
            u4 code_len = static_cast<u4>(method.code.size());
            writeU4(out, code_len);
            for (u1 b : method.code) out.put(b);
            writeU2(out, 0); // exception_table_length
            writeU2(out, 0); // code_attributes_count

            auto end_pos = out.tellp();
            u4 attr_len = static_cast<u4>(end_pos - (len_pos + static_cast<streamoff>(4)));
            out.seekp(len_pos);
            writeU4(out, attr_len);
            out.seekp(end_pos);
        }

        writeU2(out, 0); // attributes_count
    }

private:
    // User-defined function signatures (filled during generate())
    map<string, pair<vector<Type>, Type>> userFunctions;
    
    void writeU2(ostream& o, u2 v) {
        o.put(static_cast<char>(v >> 8));
        o.put(static_cast<char>(v & 0xFF));
    }
    void writeU4(ostream& o, u4 v) {
        writeU2(o, static_cast<u2>(v >> 16));
        writeU2(o, static_cast<u2>(v & 0xFFFF));
    }
};  // ClassFile
