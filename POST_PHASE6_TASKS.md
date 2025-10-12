# Post-Phase 6 Tasks

**Complete AFTER Phase 6 (user-defined types/structs) is finished**

---

## 1. Add Command-Line Options for Output File

### Current State
- Output filename is hardcoded to `BasicProgram.class`
- Class name is hardcoded to `BasicProgram`
- User reverted earlier implementation to keep things simple during Phase 6 development

### Planned Implementation

Add support for:
```bash
jvmbasic program.bas -o MyProgram.class  # Custom output file
java MyProgram                            # Run with custom class name
```

### Code Changes Required

1. **Add command-line parsing in `main()`**:
   ```cpp
   int main(int argc, char* argv[]) {
       string inputFile;
       string outputFile = "BasicProgram.class";
       string className = "BasicProgram";
       bool showHelp = false;
       
       // Parse arguments
       for (int i = 1; i < argc; i++) {
           string arg = argv[i];
           if (arg == "-h" || arg == "--help") {
               showHelp = true;
           } else if (arg == "-o" && i + 1 < argc) {
               outputFile = argv[++i];
               // Extract class name from filename
               className = extractClassName(outputFile);
           } else if (arg[0] != '-') {
               inputFile = arg;
           }
       }
       
       // ... compilation logic ...
   }
   ```

2. **Add `setClassName()` method to BasicCompiler**:
   ```cpp
   class BasicCompiler {
   public:
       void setClassName(const string& name) {
           cf.className = name;
       }
       // ...
   };
   ```

3. **Add `className` field to ClassFile in codegen.h**:
   ```cpp
   class ClassFile {
   public:
       string className = "BasicProgram";  // Default
       // ...
       
       void buildConstantPool() {
           u2 simple_class = cp.addUtf8(className);  // Use variable
           // ...
       }
   };
   ```

4. **Update help message**:
   ```cpp
   if (showHelp) {
       cout << "JVM BASIC Compiler - Phase 6 Complete\n\n";
       cout << "Usage: jvmbasic [options] [input.bas]\n\n";
       cout << "Options:\n";
       cout << "  -o <file>      Output class file (default: BasicProgram.class)\n";
       cout << "                 Class name is derived from filename\n";
       cout << "  -h, --help     Show this help message\n\n";
       cout << "Examples:\n";
       cout << "  jvmbasic program.bas\n";
       cout << "  jvmbasic program.bas -o MyApp.class\n";
       cout << "  jvmbasic < program.bas\n";
       return 0;
   }
   ```

5. **Helper function to extract class name**:
   ```cpp
   string extractClassName(const string& filepath) {
       // Remove path
       size_t slashPos = filepath.rfind('/');
       string filename = (slashPos != string::npos) 
           ? filepath.substr(slashPos + 1) 
           : filepath;
       
       // Remove .class extension
       size_t dotPos = filename.rfind(".class");
       if (dotPos != string::npos) {
           return filename.substr(0, dotPos);
       }
       
       // Remove any other extension
       dotPos = filename.rfind('.');
       if (dotPos != string::npos) {
           return filename.substr(0, dotPos);
       }
       
       return filename;
   }
   ```

### Why Wait Until After Phase 6?

- Keep `main()` simple during active development
- Avoid merge conflicts in rapidly changing code
- Struct implementation is more critical than CLI polish
- Easier to test with consistent output filename
- Can test properly once Phase 6 is stable

---

## 2. Other Post-Phase 6 Enhancements

### Command-Line Options to Consider

1. **`--dump-ast`** - Print AST instead of compiling
   - Already exists in `jvmbasic-new`
   - Useful for debugging

2. **`--verbose`** - Show compilation steps
   - Parse phase
   - Type inference passes
   - Code generation
   - Class file written

3. **`--version`** - Show compiler version

4. **`--target-version`** - JVM target version
   - Currently hardcoded to Java 5 (version 49)
   - Could support Java 8+ for better features

### Documentation Updates

After adding command-line options:
- Update `docs/USER_GUIDE.md`
- Update `README.md`
- Add examples to help message
- Update `START_PHASE6_HERE.md` → `START_PHASE7_HERE.md`

---

## Timeline

**Phase 6 Complete** → Add command-line options → **Phase 7 (OOP)**

Estimated time: 2-3 hours for command-line options

---

**Note**: This file tracks features intentionally deferred during Phase 6 development.

