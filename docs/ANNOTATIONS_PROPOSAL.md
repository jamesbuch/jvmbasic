# Annotations Proposal for JVM BASIC 2.0

This document proposes a PHP 8-style annotation system for JVM BASIC 2.0, enabling metadata-driven programming for web frameworks, dependency injection, validation, and more.

## Table of Contents

1. [Motivation](#motivation)
2. [Proposed Syntax](#proposed-syntax)
3. [Grammar Changes](#grammar-changes)
4. [Symbol Table Changes](#symbol-table-changes)
5. [IR Representation](#ir-representation)
6. [Code Generation](#code-generation)
7. [Runtime Access](#runtime-access)
8. [Use Cases](#use-cases)
9. [Implementation Phases](#implementation-phases)

---

## Motivation

Annotations enable:

1. **Web Frameworks**: Route definitions, HTTP method mapping
2. **Dependency Injection**: Service registration, injection points
3. **Validation**: Input validation rules
4. **Serialization**: Field mapping, format control
5. **Testing**: Test discovery, setup/teardown
6. **Documentation**: API documentation generation

---

## Proposed Syntax

### PHP 8-Style Attribute Syntax

Using `#[...]` syntax (preferred over `@...` to avoid confusion with decorators):

```basic
#[Controller]
#[Route("/api/users")]
public class UserController

    #[Inject]
    private var _userService as UserService

    #[Get("/")]
    #[Authorize("read:users")]
    public function list() as User[]
        return _userService.getAllUsers()
    end function

    #[Get("/{id}")]
    public function get(#[PathParam] id as Integer) as User
        return _userService.getUser(id)
    end function

    #[Post("/")]
    #[ValidateBody]
    public function create(#[Body] user as User) as User
        return _userService.createUser(user)
    end function

    #[Delete("/{id}")]
    #[Authorize("admin")]
    public function delete(#[PathParam] id as Integer) as Boolean
        return _userService.deleteUser(id)
    end function
end class
```

### Annotation with Parameters

```basic
#[Route("/users", methods: {"GET", "POST"})]
#[Cache(ttl: 3600, key: "users")]
#[Retry(maxAttempts: 3, delay: 1000)]
public function getUsers() as User[]
```

### Multiple Annotations

```basic
#[Serializable]
#[Table("users")]
#[Index("email", unique: true)]
public class User
    #[Column("user_id")]
    #[PrimaryKey]
    #[AutoIncrement]
    public var id as Integer

    #[Column("email_address")]
    #[NotNull]
    #[Email]
    public var email as String

    #[Column("created_at")]
    #[Timestamp]
    public var createdAt as Date
end class
```

---

## Grammar Changes

### Lexer Additions (JvmBasicLexer.g4)

```antlr
// Annotation tokens
HASH_LBRACKET : '#[' ;

// Already exists
RBRACKET      : ']' ;
```

### Parser Additions (JvmBasicParser.g4)

```antlr
// Annotation rule
annotation
    : HASH_LBRACKET annotationName annotationArgs? RBRACKET
    ;

annotationName
    : IDENTIFIER (DOT IDENTIFIER)*    // Support namespaced annotations
    ;

annotationArgs
    : LPAREN annotationArgList? RPAREN
    ;

annotationArgList
    : annotationArg (COMMA annotationArg)*
    ;

annotationArg
    : expression                              // Positional argument
    | IDENTIFIER COLON expression             // Named argument
    ;

// Annotation list (zero or more)
annotations
    : annotation*
    ;

// Update class declaration
classDeclaration
    : annotations                             // Add annotations
      accessModifier? ABSTRACT? CLASS IDENTIFIER
      typeParameters?
      (EXTENDS typeName)?
      (IMPLEMENTS typeNameList)?
      classMember*
      END CLASS
    ;

// Update method declaration
methodDeclaration
    : annotations                             // Add annotations
      accessModifier? SHARED? OVERRIDE? (FUNCTION | SUB) IDENTIFIER
      typeParameters?
      parameterList?
      (AS typeName)?
      statement*
      END (FUNCTION | SUB)
    ;

// Update field declaration
fieldDeclaration
    : annotations                             // Add annotations
      accessModifier? SHARED? VAR IDENTIFIER AS typeName (EQ expression)?
    ;

// Update parameter for annotations
parameter
    : annotations                             // Add annotations
      BYREF? IDENTIFIER AS typeName (EQ expression)?
    ;
```

---

## Symbol Table Changes

### New Symbol Types

```java
// Annotation symbol
public class AnnotationSymbol {
    private final String name;
    private final Map<String, Object> arguments;
    private final int line;
    private final int column;

    public AnnotationSymbol(String name, Map<String, Object> arguments, int line, int column) {
        this.name = name;
        this.arguments = arguments;
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public Map<String, Object> getArguments() { return arguments; }
    public Object getArgument(String key) { return arguments.get(key); }
    public boolean hasArgument(String key) { return arguments.containsKey(key); }
}

// Add to existing symbols
public class ClassSymbol {
    // ... existing fields ...
    private final List<AnnotationSymbol> annotations = new ArrayList<>();

    public void addAnnotation(AnnotationSymbol annotation) {
        annotations.add(annotation);
    }

    public List<AnnotationSymbol> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public boolean hasAnnotation(String name) {
        return annotations.stream().anyMatch(a -> a.getName().equals(name));
    }

    public AnnotationSymbol getAnnotation(String name) {
        return annotations.stream()
            .filter(a -> a.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
}

// Similar additions for:
// - FunctionSymbol (method annotations)
// - VariableSymbol (field annotations)
// - ParameterSymbol (parameter annotations)
```

### SymbolCollector Updates

```java
@Override
public void enterClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
    String className = ctx.IDENTIFIER().getText();
    ClassSymbol classSym = new ClassSymbol(className, ctx.getStart().getLine());

    // Process annotations
    if (ctx.annotations() != null) {
        for (JvmBasicParser.AnnotationContext annoCtx : ctx.annotations().annotation()) {
            AnnotationSymbol anno = visitAnnotation(annoCtx);
            classSym.addAnnotation(anno);
        }
    }

    // ... rest of processing ...
}

private AnnotationSymbol visitAnnotation(JvmBasicParser.AnnotationContext ctx) {
    String name = ctx.annotationName().getText();
    Map<String, Object> args = new LinkedHashMap<>();

    if (ctx.annotationArgs() != null && ctx.annotationArgs().annotationArgList() != null) {
        int positionalIndex = 0;
        for (JvmBasicParser.AnnotationArgContext argCtx : ctx.annotationArgs().annotationArgList().annotationArg()) {
            if (argCtx.IDENTIFIER() != null) {
                // Named argument
                String key = argCtx.IDENTIFIER().getText();
                Object value = evaluateConstantExpression(argCtx.expression());
                args.put(key, value);
            } else {
                // Positional argument
                Object value = evaluateConstantExpression(argCtx.expression());
                args.put("$" + positionalIndex++, value);
            }
        }
    }

    return new AnnotationSymbol(name, args, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
}
```

---

## IR Representation

### IR Annotation Node

```java
// In ir/IRAnnotation.java
public record IRAnnotation(
    String name,
    Map<String, IRExpression> arguments,
    int line,
    int column
) implements IRNode {

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visitAnnotation(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("#[");
        sb.append(name);
        if (!arguments.isEmpty()) {
            sb.append("(");
            sb.append(arguments.entrySet().stream()
                .map(e -> e.getKey().startsWith("$")
                    ? e.getValue().toString()
                    : e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(", ")));
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }
}
```

### Updated IR Nodes

```java
// Updated IRClass
public class IRClass implements IRNode {
    // ... existing fields ...
    private final List<IRAnnotation> annotations;

    public IRClass(String name, String superClass, List<IRAnnotation> annotations) {
        // ...
        this.annotations = annotations != null ? annotations : List.of();
    }

    public List<IRAnnotation> getAnnotations() { return annotations; }
}

// Updated IRFunction
public class IRFunction implements IRNode {
    // ... existing fields ...
    private final List<IRAnnotation> annotations;

    public List<IRAnnotation> getAnnotations() { return annotations; }
}

// Updated IRVariable (for fields)
public class IRVariable implements IRNode {
    // ... existing fields ...
    private final List<IRAnnotation> annotations;

    public List<IRAnnotation> getAnnotations() { return annotations; }
}

// Updated IRParameter
public class IRParameter implements IRNode {
    // ... existing fields ...
    private final List<IRAnnotation> annotations;

    public List<IRAnnotation> getAnnotations() { return annotations; }
}
```

---

## Code Generation

### Java Annotations Output

Annotations should generate actual Java annotations visible at runtime:

```java
// In CompilerVisitor.java

private void generateAnnotations(List<AnnotationSymbol> annotations, AnnotationTarget target) {
    for (AnnotationSymbol anno : annotations) {
        AnnotationVisitor av;
        String descriptor = "L" + mapAnnotationName(anno.getName()) + ";";

        switch (target) {
            case CLASS:
                av = cw.visitAnnotation(descriptor, true);
                break;
            case METHOD:
                av = mv.visitAnnotation(descriptor, true);
                break;
            case FIELD:
                av = fv.visitAnnotation(descriptor, true);
                break;
            case PARAMETER:
                av = mv.visitParameterAnnotation(paramIndex, descriptor, true);
                break;
        }

        // Generate annotation values
        for (Map.Entry<String, Object> arg : anno.getArguments().entrySet()) {
            String key = arg.getKey();
            if (key.startsWith("$")) {
                key = "value"; // Positional becomes "value"
            }
            generateAnnotationValue(av, key, arg.getValue());
        }

        av.visitEnd();
    }
}

private String mapAnnotationName(String basicName) {
    // Map BASIC annotation names to Java annotation classes
    return switch (basicName) {
        case "Route" -> "com/jvmbasic/web/Route";
        case "Get" -> "com/jvmbasic/web/Get";
        case "Post" -> "com/jvmbasic/web/Post";
        case "Controller" -> "com/jvmbasic/web/Controller";
        case "Inject" -> "javax/inject/Inject";
        case "Table" -> "javax/persistence/Table";
        case "Column" -> "javax/persistence/Column";
        default -> "com/jvmbasic/annotation/" + basicName;
    };
}
```

### Runtime Annotation Definitions

Create annotation interfaces in the runtime library:

```java
// com/jvmbasic/web/Route.java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Route {
    String value() default "";
    String[] methods() default {};
}

// com/jvmbasic/web/Get.java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Get {
    String value() default "";
}

// com/jvmbasic/web/Controller.java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
    String value() default "";
}
```

---

## Runtime Access

### Reflection API

```basic
' Get class annotations
var clazz as Class = typeof(UserController)
var annotations as Annotation[] = Reflect.GetAnnotations(clazz)

for each anno in annotations
    Console.WriteLine($"Annotation: {anno.Name}")
    for each arg in anno.Arguments
        Console.WriteLine($"  {arg.Key} = {arg.Value}")
    next
next

' Check for specific annotation
if Reflect.HasAnnotation(clazz, "Controller") then
    var route = Reflect.GetAnnotation(clazz, "Route")
    Console.WriteLine($"Route: {route.Arguments["value"]}")
end if
```

### Framework Integration

```basic
' Web framework scanner
public function scanControllers(packageName as String) as Controller[]
    var controllers as List<Controller> = new List<Controller>()

    for each clazz in Reflect.GetClasses(packageName)
        if Reflect.HasAnnotation(clazz, "Controller") then
            var controller = new Controller()
            controller.Class = clazz
            controller.Route = Reflect.GetAnnotation(clazz, "Route")

            for each method in clazz.Methods
                if Reflect.HasAnnotation(method, "Get") or _
                   Reflect.HasAnnotation(method, "Post") then
                    controller.AddEndpoint(method)
                end if
            next

            controllers.Add(controller)
        end if
    next

    return controllers.ToArray()
end function
```

---

## Use Cases

### 1. Web Framework Routes

```basic
#[Controller]
#[Route("/api/products")]
public class ProductController

    #[Get("/")]
    public function list() as Product[]
        ' List all products
    end function

    #[Get("/{id}")]
    public function get(#[PathParam] id as Integer) as Product
        ' Get single product
    end function

    #[Post("/")]
    public function create(#[Body] product as Product) as Product
        ' Create product
    end function
end class
```

### 2. Dependency Injection

```basic
#[Service]
public class UserService

    #[Inject]
    private var _repository as UserRepository

    #[Inject]
    private var _logger as Logger

    public function getUser(id as Integer) as User
        _logger.Info($"Getting user {id}")
        return _repository.FindById(id)
    end function
end class
```

### 3. Validation

```basic
public class CreateUserRequest
    #[Required]
    #[MinLength(3)]
    #[MaxLength(50)]
    public var username as String

    #[Required]
    #[Email]
    public var email as String

    #[Required]
    #[MinLength(8)]
    #[Pattern("^(?=.*[A-Z])(?=.*[0-9])")]
    public var password as String

    #[Range(18, 150)]
    public var age as Integer
end class
```

### 4. Database Mapping

```basic
#[Entity]
#[Table("users")]
public class User
    #[Id]
    #[GeneratedValue(strategy: "IDENTITY")]
    #[Column("user_id")]
    public var id as Integer

    #[Column("username", nullable: false, length: 50)]
    public var username as String

    #[Column("email", unique: true)]
    public var email as String

    #[OneToMany(mappedBy: "user")]
    public var posts as Post[]
end class
```

---

## Implementation Phases

### Phase 1: Grammar and Parsing (1-2 weeks)
- [ ] Add annotation tokens to lexer
- [ ] Add annotation rules to parser
- [ ] Update class, method, field, parameter rules
- [ ] Test parsing with example files

### Phase 2: Symbol Collection (1 week)
- [ ] Create AnnotationSymbol class
- [ ] Add annotations to ClassSymbol, FunctionSymbol, etc.
- [ ] Update SymbolCollector to process annotations
- [ ] Store annotation arguments as constant expressions

### Phase 3: IR Integration (1 week)
- [ ] Create IRAnnotation class
- [ ] Add annotations to IRClass, IRFunction, etc.
- [ ] Update IRBuilder to include annotations
- [ ] Display annotations in -ir output

### Phase 4: Code Generation (2 weeks)
- [ ] Create runtime annotation interfaces
- [ ] Generate Java annotations with ASM
- [ ] Handle annotation arguments and arrays
- [ ] Support @Retention(RUNTIME)

### Phase 5: Runtime API (1-2 weeks)
- [ ] Create Reflect namespace in runtime library
- [ ] Implement GetAnnotations, HasAnnotation, etc.
- [ ] Test annotation access at runtime
- [ ] Document reflection API

### Phase 6: Framework Integration (ongoing)
- [ ] Web framework annotation scanner
- [ ] Validation framework
- [ ] DI container integration
- [ ] Database mapping support

---

## References

- [PHP 8 Attributes](https://www.php.net/manual/en/language.attributes.php)
- [Java Annotations](https://docs.oracle.com/javase/tutorial/java/annotations/)
- [ASM Annotation Visitor](https://asm.ow2.io/javadoc/org/objectweb/asm/AnnotationVisitor.html)
- [JSR 330: Dependency Injection](https://jcp.org/en/jsr/detail?id=330)
