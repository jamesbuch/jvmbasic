# JVM BASIC Documentation

**Complete guide to understanding and extending JVM BASIC**

---

## 🎯 Quick Start

**New to the project?** Start here:
1. Read `/README.md` (project overview)
2. Read `reference/LANGUAGE_FEATURES.md` (what the language can do)
3. Read `dev/CODE_GUIDE.md` (architecture overview)

**Want to add a feature?** Read:
1. `dev/LEXER_GUIDE.md` (add tokens)
2. `dev/AST_GUIDE.md` (understand structure)
3. `dev/DEBUGGING_GUIDE.md` (test and debug)

---

## 📚 Documentation Structure

### `/docs/dev/` - Developer Guides
**For extending the compiler**

| File | Purpose | Read When |
|------|---------|-----------|
| **CODE_GUIDE.md** | Architecture overview | Understanding the codebase |
| **LEXER_GUIDE.md** | How tokenization works | Adding new syntax |
| **AST_GUIDE.md** | Abstract Syntax Tree structure | Understanding program representation |
| **DEBUGGING_GUIDE.md** | Debug techniques & tools | Fixing bugs |
| **walkthrough.md** | Detailed code walkthrough | Deep dive |
| **extending.md** | Extension guidelines | Adding features |

### `/docs/reference/` - Reference Documentation
**Language features and specifications**

| File | Purpose |
|------|---------|
| **LANGUAGE_FEATURES.md** | Complete language reference (93 built-in functions!) |
| **ARRAY_PARAM_RESEARCH.md** | How array parameters were implemented |
| **SESSION_DELIVERABLES.md** | What Phase 5 delivered |
| **SESSION_ACHIEVEMENTS.md** | Detailed accomplishments |
| **FINAL_SESSION_REPORT.md** | Comprehensive session report |

### `/docs/planning/` - Roadmap & Design
**Future development**

| File | Purpose |
|------|---------|
| **PHASE6_ROADMAP.md** | Complete Phase 6-10 plan (structs → OOP → networking) |
| **PHASE6_DESIGN.md** | Detailed Phase 6 implementation design |
| **SERIOUS_LANGUAGE_ANALYSIS.md** | What makes a serious language? |

### `/docs/ideas/` - Brainstorming
**Feature ideas and experiments**

- `WISHLIST.md` - Future features
- `LOOPS_PLAN.md` - Loop implementation notes
- `STDLIB_PLAN.md` - Standard library ideas

---

## 🎓 Learning Path

### **Level 1: User** (Use the language)
1. README.md (project root)
2. `reference/LANGUAGE_FEATURES.md`
3. Example programs in `/examples/`

### **Level 2: Contributor** (Fix bugs, add built-ins)
1. `dev/CODE_GUIDE.md`
2. `dev/DEBUGGING_GUIDE.md`
3. Pick an issue and fix it

### **Level 3: Core Developer** (Add major features)
1. All dev guides
2. `dev/walkthrough.md`
3. `planning/PHASE6_DESIGN.md`
4. Implement new language features

---

## 🔍 Quick Reference

### Adding a Built-in Function
1. Add to `BasicRuntime.java`
2. Register in `builtin_functions.cpp`
3. Test it
4. Done!

### Adding New Syntax
1. Add tokens in `lexer.h` / `lexer.cpp`
2. Add AST node in `ast.h`
3. Add parsing in parser
4. Add code generation in codegen
5. Test thoroughly

### Debugging a Problem
1. Use AST dump: `./jvmbasic-new --dump-ast < program.bas`
2. Check bytecode: `javap -v -c BasicProgram`
3. Compare with javac
4. See `dev/DEBUGGING_GUIDE.md`

---

## 📊 Project Status

**Current**: Phase 5 Complete + Enhanced  
**Features**: 93 built-in functions, recursion, array parameters, file I/O, regex  
**Tests**: 13/13 passing (100%)  
**Lines**: ~4,000 (modular, professional)  
**Maturity**: 40% to full production language  

**Next**: Phase 6 (User-Defined Types / Structs) → 60% maturity

---

## 🎯 Documentation Goals

### ✅ Completed
- Comprehensive developer guides
- Complete language reference
- Detailed planning documents
- Session reports and achievements
- Debugging and testing guides

### 📝 To Add (Future)
- Parser guide (detailed)
- Code generator guide (detailed)
- JVM bytecode reference
- Tutorial series
- Video walkthroughs

---

## 🤝 Contributing

**Want to help?**

1. Read the dev guides
2. Pick a task from `planning/PHASE6_ROADMAP.md`
3. Follow the code style
4. Write tests
5. Submit changes

**Questions?** Check `dev/DEBUGGING_GUIDE.md` first!

---

## 📞 Quick Links

| Need | Location |
|------|----------|
| Language features | `reference/LANGUAGE_FEATURES.md` |
| How to extend | `dev/extending.md` |
| Debug a problem | `dev/DEBUGGING_GUIDE.md` |
| Understand AST | `dev/AST_GUIDE.md` |
| Future plans | `planning/PHASE6_ROADMAP.md` |
| Project status | `reference/SESSION_DELIVERABLES.md` |

---

**Remember**: Documentation is never complete - feel free to improve it!

