# Running JVM BASIC Programs with Libraries

## Command Line Reference

### Compiling a Program
```bash
./jvmbasic < yourprogram.bas
```

This generates `BasicProgram.class`

### Running with Libraries - FULL COMMAND
```bash
java -cp ".:lib/*:basicrt" BasicProgram
```

**Breakdown**:
- `java` - Java runtime
- `-cp ".:lib/*:basicrt"` - Classpath with 3 components:
  - `.` - Current directory (for BasicProgram.class)
  - `lib/*` - All JAR files in lib/ directory
  - `basicrt` - Runtime class directory
- `BasicProgram` - The compiled program

### Using Custom Class Name
```bash
# Compile with custom name
./jvmbasic -o MyApp < program.bas

# Run with custom name
java -cp ".:lib/*:basicrt" MyApp
```

### Quick Build and Run
```bash
# Use the buildrun.sh script
./buildrun.sh yourprogram.bas
```

This automatically:
1. Compiles BasicRuntime.java if needed
2. Compiles your BASIC program
3. Runs with correct classpath

---

## Library Requirements

The programs need these libraries in the `lib/` directory:

| Library | Size | Purpose |
|---------|------|---------|
| gson-2.10.1.jar | 277KB | JSON parsing/generation |
| postgresql-42.7.1.jar | 1.1MB | PostgreSQL database |
| mariadb-java-client-3.3.2.jar | 647KB | MariaDB/MySQL database |
| commons-io-2.15.1.jar | 490KB | File I/O utilities |
| guava-33.0.0-jre.jar | 3.0MB | Collections/utilities |

**Total**: 5.4MB

---

## Examples

### Basic Program
```bash
$ ./jvmbasic < examples/fibonacci_sequence.bas
$ java -cp ".:lib/*:basicrt" BasicProgram
```

### Web App
```bash
$ cd examples
$ ../jvmbasic -o WebApp < modern_web_app.bas
$ java -cp "..:../lib/*:../basicrt" WebApp
```

### Database Program
```bash
$ ./jvmbasic < test_postgres.bas
$ java -cp ".:lib/*:basicrt" BasicProgram
# Uses PostgreSQL JDBC driver from lib/
```

---

## Environment Variables (Optional)

### Set CLASSPATH permanently
```bash
export CLASSPATH=".:lib/*:basicrt"
java BasicProgram
```

### Create alias
```bash
alias jvmrun='java -cp ".:lib/*:basicrt"'
jvmrun BasicProgram
```

---

## Distribution

When distributing your compiled programs, include:
1. `BasicProgram.class` (your compiled program)
2. `lib/` directory (all JAR files)
3. `basicrt/BasicRuntime.class` (runtime library)

Users run with:
```bash
java -cp ".:lib/*:basicrt" BasicProgram
```

---

## Troubleshooting

### ClassNotFoundException
**Error**: `Could not find or load main class BasicProgram`  
**Fix**: Ensure classpath includes current directory (`:` or `.`)

### NoClassDefFoundError for BasicRuntime
**Error**: `NoClassDefFoundError: basicrt/BasicRuntime`  
**Fix**: Include `basicrt` in classpath

### NoSuchMethodError for namespace methods
**Error**: `NoSuchMethodError: json_Parse`  
**Fix**: Recompile BasicRuntime.java with libraries:
```bash
javac -cp "lib/*" BasicRuntime.java
cp BasicRuntime.class basicrt/
```

### ClassNotFoundException for Gson/JDBC
**Error**: `ClassNotFoundException: com.google.gson.Gson`  
**Fix**: Include `lib/*` in classpath

---

## Summary

**Standard command** for running JVM BASIC programs:
```bash
java -cp ".:lib/*:basicrt" BasicProgram
```

**Or use the script**:
```bash
./buildrun.sh yourprogram.bas
```

Simple! 🚀

