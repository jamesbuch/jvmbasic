# Phase 10 Development Priorities

**Branch**: `ready-phase10-development`  
**Start Date**: October 22, 2025  
**Timeline**: 2-3 months  

---

## 🎯 High Priority Features

### 1. String Interpolation ⭐ (Weeks 1-4)
**Why**: Modern, readable syntax for all examples and user code

**Syntax**:
```basic
Dim name As String = "Alice"
Dim age As Integer = 30
Console.WriteLine($"Hello {name}, you are {age} years old!")
```

**Implementation**:
- Add `$"..."` token to lexer
- Parse `{variable}` placeholders
- Transform to string concatenation
- Support all types with automatic conversion

**Documentation**: `docs/planning/STRING_INTERPOLATION.md`

---

### 2. Enhanced File/IO for Compiler Development ⭐ (Weeks 5-8)
**Why**: Enable writing compilers in JVM BASIC

**Critical Functions**:
```basic
' Binary I/O (essential for bytecode)
Dim bytes() As Integer = File.ReadAllBytes(path)
File.WriteAllBytes(path, bytes)

' Line-by-line (for source files)
Dim lines() As String = File.ReadAllLines(path)
For Each line In lines
    ' Process line
Next

' Streaming (for large files)
Dim reader As Integer = File.OpenReader(path)
While Not File.EndOfFile(reader)
    Dim line As String = File.ReadLine(reader)
    ' Process
End While
File.Close(reader)

' Directory traversal (for multi-file projects)
Dim files() As String = File.ListFiles(directory, "*.bas")
For Each file In files
    ' Compile file
Next

' Path operations
Dim combined As String = File.Combine(dir, "src", filename)
Dim ext As String = File.GetExtension(path)
```

**Use Case**: Implement a simple compiler/interpreter in JVM BASIC

---

### 3. Command-Line Arguments ⭐ (Weeks 9-10)
**Why**: Essential for CLI tools and compilers

**Implementation**:
```basic
Function Main(args As String()) As Integer
    If ArrayLength(args) < 2 Then
        Console.WriteLine("Usage: compiler <input.src> <output.class>")
        Return 1
    End If
    
    Dim inputFile As String = args(0)
    Dim outputFile As String = args(1)
    
    ' Compile file
    If compile(inputFile, outputFile) Then
        Return 0  ' Success
    Else
        Return 2  ' Error
    End If
End Function
```

**Advanced Parsing** (Apache Commons CLI):
```basic
Dim parser As Integer = Args.NewParser()
Args.AddOption(parser, "i", "input", "Input file", true)
Args.AddOption(parser, "o", "output", "Output file", true)
Args.AddFlag(parser, "v", "verbose", "Verbose output")

Dim parsed As Integer = Args.Parse(parser, args)
Dim inputFile As String = Args.GetValue(parsed, "input")
Dim verbose As Boolean = Args.HasFlag(parsed, "verbose")
```

---

### 4. Crypto Namespace ⭐ (Weeks 11-14)
**Why**: Security is essential for production applications

**Priority Functions**:
```basic
' File hashing (for build systems)
Dim hash As String = Crypto.Sha256File(path)
Dim checksum As String = Crypto.Md5File(path)

' Password storage
Dim hashed As String = Crypto.Bcrypt(password, 12)
If Crypto.BcryptVerify(inputPassword, hashed) Then
    ' Login successful
End If

' Data encryption (for sensitive files)
Dim key As String = Crypto.GenerateAesKey(256)
Dim encrypted As String = Crypto.AesEncrypt(data, key)
File.WriteAllBytes("encrypted.dat", encrypted)

' Digital signatures (for code signing)
Dim keypair As Integer = Crypto.GenerateRsaKeyPair(2048)
Dim signature As String = Crypto.Sign(fileContents, privateKey)
File.WriteAllText("file.sig", signature)
```

---

### 5. Module System ⭐ (Weeks 15-18)
**Why**: Code reusability and library development

**Define Library**:
```basic
' Lexer.bas
Module Compiler.Lexer
    Public Function Tokenize(source As String) As Integer()
        ' Return array of tokens
    End Function
    
    Private Function IsKeyword(word As String) As Boolean
        ' Internal helper
    End Function
End Module
```

**Use Library**:
```basic
' main.bas
Import Compiler.Lexer
Import Compiler.Parser

Function Main(args As String()) As Integer
    Dim source As String = File.ReadAllText(args(0))
    Dim tokens As Integer() = Compiler.Lexer.Tokenize(source)
    Dim ast As Integer = Compiler.Parser.Parse(tokens)
    Return 0
End Function
```

**Compilation**:
```bash
# Compile library
./jvmbasic --library -o Compiler.Lexer < Lexer.bas

# Compile main program (automatically finds Compiler.Lexer.class)
./jvmbasic < main.bas

# Run
java -cp ".:lib/*:basicrt" BasicProgram
```

---

## 🔧 Medium Priority Features

### 6. Thread Namespace (Weeks 19-22)
**Why**: Multithreading for performance

**Basic Example**:
```basic
Function CompileFile(filename As String)
    Console.WriteLine($"Compiling {filename}...")
    ' Compile logic
End Function

' Parallel compilation
Dim files() As String = File.ListFiles("src", "*.bas")
Dim threads(ArrayLength(files)) As Integer

For i = 0 To ArrayLength(files) - 1
    threads(i) = Thread.Create(CompileFile, files(i))
    Thread.Start(threads(i))
Next i

For i = 0 To ArrayLength(files) - 1
    Thread.Join(threads(i))
Next i

Console.WriteLine("All files compiled!")
```

---

### 7. Stream I/O (Weeks 23-24)
**Why**: Binary data handling for bytecode generation

```basic
' Write bytecode manually
Dim output As Integer = Stream.OpenOutput("MyClass.class")

' Class file magic number
Stream.WriteByte(output, 0xCA)
Stream.WriteByte(output, 0xFE)
Stream.WriteByte(output, 0xBA)
Stream.WriteByte(output, 0xBE)

' Version
Stream.WriteU16(output, 0)     ' Minor
Stream.WriteU16(output, 49)    ' Major (Java 5)

Stream.Close(output)
```

---

## 🎓 Example: Simple Compiler in JVM BASIC

With Phase 10 features, we can write a working compiler!

```basic
' simple_compiler.bas
Import Compiler.Lexer
Import Compiler.Parser
Import Compiler.CodeGen

Function Main(args As String()) As Integer
    ' Parse arguments
    If ArrayLength(args) < 2 Then
        Console.WriteLine("Usage: compiler <input.src> <output.class>")
        Return 1
    End If
    
    Dim inputFile As String = args(0)
    Dim outputFile As String = args(1)
    
    ' Check input exists
    If Not File.Exists(inputFile) Then
        Console.WriteLine($"Error: File not found: {inputFile}")
        Return 2
    End If
    
    Console.WriteLine($"Compiling {inputFile}...")
    
    ' Read source
    Dim source As String = File.ReadAllText(inputFile)
    
    ' Lexical analysis
    Dim tokens As Integer() = Compiler.Lexer.Tokenize(source)
    Console.WriteLine($"  Lexer: {ArrayLength(tokens)} tokens")
    
    ' Parsing
    Dim ast As Integer = Compiler.Parser.Parse(tokens)
    Console.WriteLine("  Parser: AST generated")
    
    ' Code generation
    Dim bytecode() As Integer = Compiler.CodeGen.Generate(ast, outputFile)
    File.WriteAllBytes(outputFile, bytecode)
    Console.WriteLine($"  Generated: {outputFile}")
    
    Return 0
End Function
```

**This is the goal!** Write compilers in JVM BASIC itself.

---

## 📚 Documentation Requirements

### New Documents to Create:
1. `docs/user/STRING_INTERPOLATION_GUIDE.md` - Usage examples
2. `docs/user/FILE_IO_ADVANCED.md` - Binary I/O, streaming, directories
3. `docs/user/COMMAND_LINE_ARGS.md` - Main() function guide
4. `docs/user/CRYPTO_GUIDE.md` - Security best practices
5. `docs/user/MODULE_SYSTEM_GUIDE.md` - Creating libraries
6. `docs/dev/COMPILER_IN_BASIC.md` - Writing compilers guide

### Update Existing:
- `README.md` - Add Phase 10 feature list
- `docs/USER_GUIDE.md` - Update with all new namespaces
- All examples - Convert to modern Console.WriteLine + string interpolation

---

## 🧪 Testing Requirements

### New Test Files:
```
tests/
├── test_interpolation_basic.bas
├── test_interpolation_types.bas
├── test_interpolation_escape.bas
├── test_main_args.bas
├── test_main_exitcode.bas
├── test_file_binary_io.bas
├── test_file_streaming.bas
├── test_file_lines.bas
├── test_file_directory.bas
├── test_file_path.bas
├── test_crypto_hash.bas
├── test_crypto_aes.bas
├── test_crypto_rsa.bas
├── test_crypto_bcrypt.bas
├── test_module_basic.bas
├── test_module_import.bas
├── test_thread_basic.bas
└── test_stream_binary.bas
```

**Target**: 105 tests (87 current + 18 new)

---

## 🎯 Success Metrics

Phase 10 is successful when:
1. ✅ String interpolation works for all types
2. ✅ Can write a simple compiler in JVM BASIC
3. ✅ Binary I/O fully functional
4. ✅ Command-line arg parsing works
5. ✅ Crypto namespace has 30+ secure functions
6. ✅ Module system allows library creation
7. ✅ 105+ tests passing (100% pass rate)
8. ✅ 5+ example programs using new features
9. ✅ Complete documentation for all features

---

## 🚀 Getting Started Checklist

### Week 1: Setup
- [x] Create `ready-phase10-development` branch
- [x] Create planning documents
- [x] Clean up repository
- [ ] Download Apache Commons CLI JAR
- [ ] Set up test infrastructure

### Week 1-2: String Interpolation
- [ ] Add `$"..."` token to lexer
- [ ] Parse `{variable}` placeholders
- [ ] Transform to string concatenation
- [ ] Test with all types
- [ ] Update examples to use interpolation

### Week 2-4: Enhanced File I/O
- [ ] Implement `File.ReadAllBytes()`
- [ ] Implement `File.WriteAllBytes()`
- [ ] Implement `File.ReadAllLines()`
- [ ] Implement streaming functions
- [ ] Implement directory operations
- [ ] Test with large files

---

## 💡 Vision: Compiler Development Platform

**Ultimate Goal**: JVM BASIC becomes a platform for writing compilers

**Why**:
1. Modern syntax (like VB but better)
2. JVM performance
3. Rich I/O for file processing
4. Module system for reusable components
5. Can eventually compile itself!

**Impact**:
- Educational: Teach compiler construction in BASIC
- Practical: Write real compilers and tools
- Self-hosting: jvmbasic compiling itself (Phase 16)

---

## 📊 Timeline Overview

```
Month 1:
  - Weeks 1-4:   String Interpolation
  - Weeks 5-8:   Enhanced File/IO

Month 2:
  - Weeks 9-10:  Command-Line Arguments
  - Weeks 11-14: Crypto Namespace
  - Weeks 15-18: Module System

Month 3:
  - Weeks 19-22: Thread Namespace
  - Weeks 23-24: Stream I/O & Polish
```

**Total**: 24 weeks (6 months for comprehensive implementation)

---

## 🎓 Milestone: Write a Compiler

**Target**: End of Phase 10

Create a complete, working compiler in JVM BASIC:
- Lexer
- Parser
- Code generator
- Multiple file support
- Command-line interface
- Error reporting

**This proves JVM BASIC is production-ready!**

---

**Branch**: `ready-phase10-development`  
**Status**: Ready to begin implementation  
**Next Step**: Implement string interpolation  

🚀 **Let's build the future of JVM BASIC!**

