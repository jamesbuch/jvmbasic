# Phase 10 Development Plan

**Status**: Planning  
**Target**: Production-Ready Enterprise Features  
**Timeline**: 2-3 months  

---

## 🎯 Phase 10 Goals

Make JVM BASIC a **production-ready language** for enterprise development with:
1. Command-line argument parsing for programs
2. Multithreading and concurrency
3. Comprehensive Crypto namespace
4. Expanded File/IO capabilities
5. Module system for reusable libraries

---

## 📋 Feature Breakdown

### 1. Command-Line Arguments ✅ (In Progress)

**Objective**: Programs can accept and parse command-line arguments

**Implementation**:
```basic
' Main function with arguments
Function Main(args As String()) As Integer
    If ArrayLength(args) < 2 Then
        Console.WriteLine("Usage: program <file> <output>")
        Return 1
    End If
    
    Dim inputFile As String = args(0)
    Dim outputFile As String = args(1)
    Console.WriteLine("Processing: " + inputFile)
    Return 0
End Function
```

**Technical Details**:
- Modify code generator to look for `Main()` function
- If `Main(args As String()) As Integer` exists, use it instead of implicit main
- Pass `String[] args` from `public static void main(String[] args)`
- Use Apache Commons CLI for advanced parsing

**JAR Required**: Apache Commons CLI 1.6.0
```bash
wget https://repo1.maven.org/maven2/commons-cli/commons-cli/1.6.0/commons-cli-1.6.0.jar
```

**Namespace**:
```basic
' Args namespace for argument parsing
Dim parser As Integer = Args.NewParser()
Args.AddOption(parser, "f", "file", "Input file", true)
Args.AddOption(parser, "o", "output", "Output file", false)
Dim parsed As Integer = Args.Parse(parser, args)
Dim filename As String = Args.GetValue(parsed, "file")
```

---

### 2. Multithreading Support

**Objective**: Create and manage threads with modern syntax

**Implementation**:
```basic
' Thread namespace
Function WorkerThread(name As String)
    For i = 1 To 5
        Console.WriteLine(name + ": " + Str(i))
        Thread.Sleep(1000)  ' Sleep 1 second
    Next i
End Function

' Create and start threads
Dim thread1 As Integer = Thread.Create(WorkerThread, "Thread1")
Dim thread2 As Integer = Thread.Create(WorkerThread, "Thread2")

Thread.Start(thread1)
Thread.Start(thread2)

' Wait for completion
Thread.Join(thread1)
Thread.Join(thread2)

Console.WriteLine("All threads completed")
```

**Advanced Features**:
```basic
' Thread synchronization
Dim lock As Integer = Thread.NewLock()
Thread.Lock(lock)
' Critical section
Thread.Unlock(lock)

' Atomic operations
Dim counter As Integer = Thread.NewAtomic(0)
Thread.AtomicIncrement(counter)
Dim value As Integer = Thread.AtomicGet(counter)
```

**Technical Details**:
- Use Java's `Thread` class
- Wrap `Runnable` interface
- Provide lock objects (`ReentrantLock`)
- Atomic variables (`AtomicInteger`, `AtomicLong`)

---

### 3. Crypto Namespace

**Objective**: Professional cryptography using Bouncy Castle

**Implementation**:
```basic
' Hashing
Dim hash As String = Crypto.Sha256("data to hash")
Dim verified As Boolean = Crypto.VerifySha256("data to hash", hash)

' Symmetric encryption (AES)
Dim key As String = Crypto.GenerateAesKey(256)
Dim encrypted As String = Crypto.AesEncrypt("secret message", key)
Dim decrypted As String = Crypto.AesDecrypt(encrypted, key)

' Asymmetric encryption (RSA)
Dim keypair As Integer = Crypto.GenerateRsaKeyPair(2048)
Dim publicKey As String = Crypto.GetPublicKey(keypair)
Dim privateKey As String = Crypto.GetPrivateKey(keypair)

Dim encrypted2 As String = Crypto.RsaEncrypt("message", publicKey)
Dim decrypted2 As String = Crypto.RsaDecrypt(encrypted2, privateKey)

' Digital signatures
Dim signature As String = Crypto.Sign("document", privateKey)
Dim valid As Boolean = Crypto.Verify("document", signature, publicKey)

' Password hashing
Dim hashedPassword As String = Crypto.Bcrypt("mypassword", 12)
Dim matches As Boolean = Crypto.BcryptVerify("mypassword", hashedPassword)
```

**Algorithms Supported**:
- **Hashing**: MD5, SHA-1, SHA-256, SHA-512, SHA3-256
- **Symmetric**: AES-128/192/256, DES, 3DES, ChaCha20
- **Asymmetric**: RSA, ECC (Elliptic Curve)
- **Key Exchange**: Diffie-Hellman, ECDH
- **Password**: Bcrypt, Scrypt, PBKDF2

**Technical Details**:
- Already have Bouncy Castle 1.77 in `lib/`
- Wrap `org.bouncycastle.crypto.*` classes
- Handle key generation, storage, and conversion
- Base64 encoding/decoding for binary data

---

### 4. Expanded File/IO Namespace

**Objective**: Comprehensive file operations using Apache Commons IO

**Current File Namespace** (Phase 9):
```basic
File.ReadAllText(path)
File.WriteAllText(path, content)
File.Exists(path)
File.Delete(path)
File.Copy(source, dest)
File.Move(source, dest)
File.Size(path)
File.IsDirectory(path)
```

**New Phase 10 Extensions**:
```basic
' Binary I/O
Dim bytes() As Integer = File.ReadAllBytes(path)
File.WriteAllBytes(path, bytes)

' Line-by-line reading
Dim lines() As String = File.ReadAllLines(path)
File.WriteAllLines(path, lines)
Dim line As String = File.ReadLine(fileHandle)

' Streaming I/O
Dim reader As Integer = File.OpenReader(path)
While Not File.EndOfFile(reader)
    line = File.ReadLine(reader)
    Console.WriteLine(line)
End While
File.Close(reader)

' Directory operations
Dim files() As String = File.ListFiles(directory)
Dim dirs() As String = File.ListDirectories(directory)
File.CreateDirectory(path)
File.DeleteDirectory(path, recursive)

' Path operations
Dim fullPath As String = File.GetAbsolutePath(path)
Dim filename As String = File.GetFileName(path)
Dim extension As String = File.GetExtension(path)
Dim directory As String = File.GetDirectory(path)
Dim combined As String = File.Combine(dir, filename)

' File metadata
Dim modified As Long = File.GetLastModified(path)
Dim permissions As String = File.GetPermissions(path)
File.SetPermissions(path, "rw-r--r--")

' Advanced operations
File.CreateTempFile(prefix, suffix)
File.CreateTempDirectory(prefix)
Dim checksum As String = File.Checksum(path, "SHA-256")
```

**Technical Details**:
- Use Apache Commons IO `FileUtils`, `FilenameUtils`
- Java NIO for binary and streaming I/O
- `Files.walk()` for directory traversal
- Support symbolic links

---

### 5. Module System (Dynamic Libraries)

**Objective**: Create reusable libraries that can be loaded dynamically

**Module Definition**:
```basic
' MyLibrary.bas
Module MyLibrary
    Public Function Add(a As Integer, b As Integer) As Integer
        Return a + b
    End Function
    
    Public Function Greet(name As String) As String
        Return "Hello, " + name + "!"
    End Function
    
    Private Function Helper()
        ' Internal helper function
    End Function
End Module
```

**Compilation**:
```bash
# Compile as library
./jvmbasic -o MyLibrary --library < MyLibrary.bas
# Generates: MyLibrary.class
```

**Usage in Programs**:
```basic
' main.bas
Import MyLibrary

Dim result As Integer = MyLibrary.Add(5, 10)
Console.WriteLine("Result: " + Str(result))

Dim greeting As String = MyLibrary.Greet("Alice")
Console.WriteLine(greeting)
```

**Technical Implementation**:
- Module compiled as separate class: `MyLibrary.class`
- All public functions become `public static` methods
- Private functions become `private static`
- Use `Class.forName("MyLibrary")` to load dynamically
- Import statement adds to classpath at runtime

**Namespace within Modules**:
```basic
Module Math.Advanced
    Public Function GCD(a As Integer, b As Integer) As Integer
        ' ...
    End Function
End Module

' Usage:
Import Math.Advanced
Dim gcd = Math.Advanced.GCD(48, 18)
```

---

### 6. IO Namespace (Expanded)

**Objective**: Advanced I/O beyond files

**Console Extensions**:
```basic
' Already have:
Console.WriteLine(text)
Console.Write(text)
Console.ReadLine()
Console.ReadKey()
Console.Clear()

' New Phase 10:
Console.SetColor(color)           ' "red", "green", "blue", "yellow", etc.
Console.ResetColor()
Console.SetCursorPosition(x, y)
Console.GetCursorPosition()
Dim width As Integer = Console.GetWidth()
Dim height As Integer = Console.GetHeight()
```

**Stream I/O**:
```basic
' Stream namespace
Dim input As Integer = Stream.OpenInput("data.bin")
Dim byte As Integer = Stream.ReadByte(input)
Dim bytes(1024) As Integer
Dim count As Integer = Stream.Read(input, bytes, 0, 1024)
Stream.Close(input)

Dim output As Integer = Stream.OpenOutput("output.bin")
Stream.WriteByte(output, 255)
Stream.Write(output, bytes, 0, count)
Stream.Flush(output)
Stream.Close(output)
```

**Network I/O**:
```basic
' Socket namespace
Dim server As Integer = Socket.CreateServer(8080)
Socket.Listen(server)
While true
    Dim client As Integer = Socket.Accept(server)
    Dim data As String = Socket.Read(client)
    Socket.Write(client, "HTTP/1.1 200 OK\r\n\r\nHello!")
    Socket.Close(client)
End While

' Client
Dim sock As Integer = Socket.Connect("localhost", 8080)
Socket.Write(sock, "GET / HTTP/1.1\r\n\r\n")
Dim response As String = Socket.Read(sock)
Socket.Close(sock)
```

---

## 📦 Required JARs (Phase 10)

### To Download:
```bash
cd lib/

# Command-line parsing
wget https://repo1.maven.org/maven2/commons-cli/commons-cli/1.6.0/commons-cli-1.6.0.jar

# Additional crypto support (if needed beyond Bouncy Castle)
# Already have: bcprov-jdk18on-1.77.jar, bcpkix-jdk18on-1.77.jar

# Logging framework
wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar
wget https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar
```

### Already Have (Use in Phase 10):
- ✅ Bouncy Castle 1.77 (crypto)
- ✅ Apache Commons IO 2.15.1 (file operations)
- ✅ Apache Commons Lang3 3.14.0 (utilities)
- ✅ Apache Commons Codec 1.16.0 (encoding)

---

## 🔨 Implementation Priority

### High Priority (Must Have)
1. **--bytecode option** ✅ (In Progress)
2. **Main() with arguments** - Essential for CLI programs
3. **Crypto namespace** - Security is critical
4. **Module system** - Code reusability

### Medium Priority (Should Have)
5. **Thread namespace** - Multithreading support
6. **Expanded File/IO** - Better file handling
7. **Stream I/O** - Binary data support

### Low Priority (Nice to Have)
8. **Socket namespace** - Network programming
9. **Console colors** - Better UI
10. **Logging framework** - Debugging support

---

## 🎓 Example Programs (Phase 10)

### 1. Command-Line Tool with Arguments
```basic
Function Main(args As String()) As Integer
    If ArrayLength(args) < 1 Then
        Console.WriteLine("Usage: program <filename>")
        Return 1
    End If
    
    Dim filename As String = args(0)
    If Not File.Exists(filename) Then
        Console.WriteLine("Error: File not found")
        Return 2
    End If
    
    Dim content As String = File.ReadAllText(filename)
    Console.WriteLine("File size: " + Str(Len(content)) + " bytes")
    Return 0
End Function
```

### 2. Multithreaded Web Scraper
```basic
Function FetchUrl(url As String)
    Dim response As String = Http.Get(url)
    Console.WriteLine("Fetched: " + url + " (" + Str(Len(response)) + " bytes)")
End Function

Function Main(args As String()) As Integer
    Dim urls() As String = {"http://example.com", "http://example.org", "http://example.net"}
    Dim threads(3) As Integer
    
    For i = 0 To 2
        threads(i) = Thread.Create(FetchUrl, urls(i))
        Thread.Start(threads(i))
    Next i
    
    For i = 0 To 2
        Thread.Join(threads(i))
    Next i
    
    Console.WriteLine("All URLs fetched")
    Return 0
End Function
```

### 3. Encrypted File Storage
```basic
Import Crypto
Import File

Function Main(args As String()) As Integer
    Dim key As String = Crypto.GenerateAesKey(256)
    
    ' Encrypt file
    Dim plaintext As String = File.ReadAllText("secret.txt")
    Dim encrypted As String = Crypto.AesEncrypt(plaintext, key)
    File.WriteAllText("secret.enc", encrypted)
    
    ' Decrypt file
    Dim encryptedData As String = File.ReadAllText("secret.enc")
    Dim decrypted As String = Crypto.AesDecrypt(encryptedData, key)
    Console.WriteLine("Decrypted: " + decrypted)
    
    Return 0
End Function
```

### 4. Reusable Math Library
```basic
' MathLib.bas
Module MathLib
    Public Function Factorial(n As Integer) As Integer
        If n <= 1 Then
            Return 1
        Else
            Return n * Factorial(n - 1)
        End If
    End Function
    
    Public Function IsPrime(n As Integer) As Boolean
        If n < 2 Then Return False
        For i = 2 To Int(Sqr(n))
            If n Mod i = 0 Then Return False
        Next i
        Return True
    End Function
End Module

' main.bas
Import MathLib

Function Main(args As String()) As Integer
    Console.WriteLine("5! = " + Str(MathLib.Factorial(5)))
    Console.WriteLine("17 is prime: " + Str(MathLib.IsPrime(17)))
    Return 0
End Function
```

---

## 🧪 Test Plan

### Tests to Add (Phase 10)
- `test_main_args.bas` - Command-line argument handling
- `test_threading_basic.bas` - Thread creation and joining
- `test_threading_sync.bas` - Thread synchronization
- `test_crypto_hash.bas` - Hashing algorithms
- `test_crypto_aes.bas` - Symmetric encryption
- `test_crypto_rsa.bas` - Asymmetric encryption
- `test_crypto_sign.bas` - Digital signatures
- `test_file_binary.bas` - Binary I/O
- `test_file_stream.bas` - Streaming I/O
- `test_file_directory.bas` - Directory operations
- `test_module_import.bas` - Module system
- `test_module_namespace.bas` - Namespaced modules

**Target**: 95+ tests (81 current + 14 new)

---

## 📚 Documentation Updates

### New Documents to Create:
- `docs/reference/THREADING_GUIDE.md` - Multithreading patterns
- `docs/reference/CRYPTO_GUIDE.md` - Cryptography best practices
- `docs/reference/MODULE_SYSTEM.md` - Creating and using modules
- `docs/reference/CLI_ARGUMENTS.md` - Command-line argument parsing

### Update Existing:
- `README.md` - Add Phase 10 features
- `docs/USER_GUIDE.md` - Add new namespaces
- `lib/README.md` - Add new JARs

---

## 🎯 Success Criteria

Phase 10 is complete when:
- ✅ `--bytecode` option works
- ✅ Main() function with arguments supported
- ✅ Thread namespace with 10+ functions
- ✅ Crypto namespace with 20+ functions
- ✅ File/IO namespace expanded to 30+ functions
- ✅ Module system working (compile and import)
- ✅ 95+ tests passing
- ✅ 5+ example programs demonstrating new features
- ✅ Complete documentation

---

## 🚀 Beyond Phase 10

### Phase 11: Advanced Features
- Lambda expressions
- LINQ-style queries
- Async/await
- Exception handling (Try/Catch)

### Phase 12-15: Compiler in BASIC
- Lexer in JVM BASIC
- Parser in JVM BASIC using ANTLR4
- Code generation in JVM BASIC

### Phase 16+: Self-Hosting
- jvmbasic.bas compiles itself
- Bootstrap process
- Full independence from C++ compiler

---

**Phase 10 Timeline**: 2-3 months  
**Start Date**: TBD  
**Expected Completion**: Q1 2026  

**Status**: Ready to begin implementation! 🎉

