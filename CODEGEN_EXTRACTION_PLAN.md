# Code Generator Extraction Plan

## Goal
Extract ~1200 lines of bytecode generation from jvmbasic.cpp into modular codegen.cpp/h

## Current State
- ✅ Frontend modules complete (ast, lexer, parser, semantic)
- ❌ Codegen still in monolithic jvmbasic.cpp (lines ~1170-2450)
- ⚠️ stub codegen.h exists but not implemented

## Extraction Strategy

### Phase 1: Identify Components (30 min)
1. ConstantPool class (lines 1183-1238)
2. Label struct (lines 1240-1244)  
3. ClassFile class (lines 1246-2450)
   - Bytecode emission methods
   - Control flow methods
   - Expression generation
   - Statement generation
   - Method generation
   - Class file writing

### Phase 2: Create codegen.cpp (2 hours)
1. Copy ConstantPool, Label, MethodInfo structures
2. Implement ClassFile as CodeGenerator class
3. Keep same interface, clean up dependencies
4. Add proper error handling

### Phase 3: Update codegen.h Interface (30 min)
1. Match actual implementation
2. Clean API design
3. Document methods

### Phase 4: Integrate with main.cpp (1 hour)
1. Update main.cpp to use CodeGenerator
2. Pass AST + symbol table to generate()
3. Output .class file

### Phase 5: Test Integration (2 hours)
1. Compile modular version
2. Run all 10 standard tests
3. Fix any integration issues
4. Verify bytecode identical to old version

### Phase 6: Cleanup (1 hour)
1. Remove old jvmbasic.cpp
2. Update Makefile
3. Update documentation
4. Final testing

##Total Estimated Time: 6-7 hours

## Files to Create/Modify
- **CREATE**: codegen.cpp (~1300 lines)
- **MODIFY**: codegen.h (expand from 38 to ~150 lines)
- **MODIFY**: main.cpp (add codegen integration)
- **MODIFY**: Makefile (link codegen.o)
- **DELETE**: jvmbasic.cpp (or rename to jvmbasic-old.cpp)

## Dependencies to Handle
- AST types (already in ast.h)
- Symbol table from semantic analysis
- Built-in function registry
- Type system

## Success Criteria
- ✅ ./jvmbasic-new compiles with codegen
- ✅ All 10 standard tests pass
- ✅ Bytecode output identical to old version
- ✅ Clean modular architecture
- ✅ No monolithic files remain

## Start: Next

