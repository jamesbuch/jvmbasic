# Type System Specification

## Overview

JVM BASIC uses a strong, static type system with full type inference. All types map directly to JVM types for seamless Java interoperability.

## Type Hierarchy

```
Type
├── PrimitiveType
│   ├── Integer   -> int (32-bit)
│   ├── Long      -> long (64-bit)
│   ├── Float     -> float (32-bit IEEE 754)
│   ├── Double    -> double (64-bit IEEE 754)
│   ├── Boolean   -> boolean
│   ├── Byte      -> byte (8-bit signed)
│   ├── Char      -> char (16-bit Unicode)
│   └── Void      -> void (for Sub return type)
│
├── ReferenceType
│   ├── String          -> java.lang.String
│   ├── ClassType       -> user-defined classes
│   ├── InterfaceType   -> user-defined interfaces
│   ├── ArrayType       -> T[]
│   ├── NullableType    -> T? (Optional<T>)
│   └── FunctionType    -> functional interfaces
│
├── SpecialType
│   ├── Nothing   -> null reference
│   ├── Any       -> java.lang.Object (for interop)
│   └── Unknown   -> type inference placeholder
│
└── NumericType (trait)
    ├── Integer, Long, Float, Double, Byte
    └── BigInteger, BigDecimal (arbitrary precision)
```

## JVM Type Mappings

| Object BASIC Type | JVM Type | Descriptor | Notes |
|-------------------|----------|------------|-------|
| Integer | int | I | 32-bit signed |
| Long | long | J | 64-bit signed |
| Float | float | F | 32-bit IEEE 754 |
| Double | double | D | 64-bit IEEE 754 |
| Boolean | boolean | Z | true/false |
| Byte | byte | B | 8-bit signed |
| Char | char | C | 16-bit Unicode |
| String | String | Ljava/lang/String; | Immutable |
| Integer[] | int[] | [I | Array |
| T? | Optional<T> | Ljava/util/Optional; | Nullable |
| Nothing | null | - | Null reference |
| Any | Object | Ljava/lang/Object; | Java interop |

## Type Inference

### Variable Declaration

```basic
' Explicit type
Dim x As Integer = 42

' Inferred type (from initializer)
Dim y = 3.14  ' Inferred as Double

' Required: type or initializer
Dim z As String  ' OK - type specified
Dim w = "hello"  ' OK - type inferred
Dim v            ' ERROR - type required
```

### Function Return Type Inference

```basic
' Explicit return type
Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function

' Inferred from return statements
Function Multiply(a As Integer, b As Integer)
    Return a * b  ' Inferred as Integer
End Function

' Multiple returns must be compatible
Function GetValue(flag As Boolean)
    If flag Then
        Return 42       ' Integer
    Else
        Return 3.14     ' Double - promotes to Double
    End If
End Function  ' Return type: Double
```

### Generic Type Inference

```basic
' Type parameter inferred from arguments
Dim list = List.Of(1, 2, 3)  ' List<Integer>
Dim map = Map.Of("a", 1, "b", 2)  ' Map<String, Integer>

' Explicit type arguments when needed
Dim empty = New List<String>()
```

## Type Compatibility

### Numeric Promotion

```
Byte -> Integer -> Long -> Float -> Double
                        -> BigInteger -> BigDecimal
```

Operations between numeric types promote to the wider type:
```basic
Dim a As Integer = 10
Dim b As Long = 20L
Dim c = a + b  ' c is Long (Integer promoted to Long)

Dim d As Float = 3.14f
Dim e = c + d  ' e is Float (Long promoted to Float)
```

### Reference Type Compatibility

```basic
' Subtype is compatible with supertype
Dim animal As Animal = New Dog()  ' OK if Dog extends Animal

' Interface implementation
Dim comparable As IComparable = New MyClass()  ' OK if MyClass implements IComparable

' Nullable types
Dim maybe As String? = Nothing  ' OK
Dim sure As String = maybe      ' ERROR - must unwrap nullable

' Arrays are covariant for read, invariant for write
Dim animals As Animal[] = New Dog[10]  ' OK for reading
animals[0] = New Cat()  ' Runtime error - ArrayStoreException
```

## Null Safety

### Nullable Types

```basic
' Non-nullable (default)
Dim name As String = "hello"
name = Nothing  ' ERROR - cannot assign Nothing to non-nullable

' Nullable with ?
Dim maybeName As String? = Nothing  ' OK
maybeName = "hello"  ' OK

' Accessing nullable requires unwrapping
Dim length = maybeName.Length  ' ERROR - must check for Nothing first

' Safe access with ?.
Dim length = maybeName?.Length  ' Returns Integer? (Nothing if maybeName is Nothing)

' Null coalescing with ??
Dim length = maybeName?.Length ?? 0  ' Returns Integer (0 if Nothing)

' Assert non-null with !
Dim length = maybeName!.Length  ' Throws if Nothing
```

### Nothing Checks

```basic
If maybeName IsNot Nothing Then
    ' maybeName is smart-cast to String here
    Console.WriteLine(maybeName.Length)
End If

' Pattern matching
Select Case maybeName
    Case Nothing
        Console.WriteLine("No name")
    Case Else name
        Console.WriteLine("Name: " + name)
End Select
```

## Generics

### Generic Classes

```basic
Public Class Stack<T>
    Private items As List<T> = New List<T>()

    Public Sub Push(item As T)
        items.Add(item)
    End Sub

    Public Function Pop() As T
        Return items.RemoveLast()
    End Function
End Class

' Usage
Dim intStack = New Stack<Integer>()
intStack.Push(42)
Dim value = intStack.Pop()  ' Integer
```

### Generic Constraints

```basic
' Type must implement interface
Public Function Max<T Extends IComparable<T>>(a As T, b As T) As T
    If a.CompareTo(b) > 0 Then Return a Else Return b
End Function

' Multiple constraints
Public Class Repository<T Extends Entity, ISerializable>
    ' T must extend Entity AND implement ISerializable
End Class
```

### Variance

```basic
' Covariance (out) - producer
Public Interface IReadOnlyList<Out T>
    Function Get(index As Integer) As T
End Interface

' Contravariance (in) - consumer
Public Interface IComparer<In T>
    Function Compare(a As T, b As T) As Integer
End Interface

' Invariance (default)
Public Interface IMutableList<T>
    Sub Add(item As T)
    Function Get(index As Integer) As T
End Interface
```

## Function Types

```basic
' Function type syntax
Dim adder As Function(Integer, Integer) As Integer

' Lambda assignment
adder = Lambda (a, b) => a + b

' Higher-order function
Function Apply<T, R>(
    value As T,
    transform As Function(T) As R
) As R
    Return transform(value)
End Function

' Usage
Dim doubled = Apply(21, Lambda (x) => x * 2)  ' 42
```

## Type Checking Implementation

```java
public class TypeChecker {

    public Type checkExpression(Expression expr, TypeEnvironment env) {
        return switch (expr) {
            case LiteralExpr lit -> lit.type();

            case IdentifierExpr id -> env.lookup(id.name());

            case BinaryExpr bin -> {
                Type left = checkExpression(bin.left(), env);
                Type right = checkExpression(bin.right(), env);
                yield checkBinaryOp(bin.operator(), left, right);
            }

            case CallExpr call -> {
                Type calleeType = checkExpression(call.callee(), env);
                if (calleeType instanceof FunctionType fn) {
                    checkArguments(call.arguments(), fn.parameterTypes(), env);
                    yield fn.returnType();
                }
                throw new TypeError("Cannot call non-function type", call.location());
            }

            case MethodCallExpr mc -> {
                Type objType = checkExpression(mc.object(), env);
                MethodSignature method = lookupMethod(objType, mc.methodName());
                checkArguments(mc.arguments(), method.parameterTypes(), env);
                yield method.returnType();
            }

            // ... other cases
        };
    }

    private Type checkBinaryOp(BinaryOp op, Type left, Type right) {
        return switch (op) {
            case ADD, SUBTRACT, MULTIPLY, DIVIDE -> promoteNumeric(left, right);
            case EQUAL, NOT_EQUAL -> {
                checkComparable(left, right);
                yield PrimitiveType.BOOLEAN;
            }
            case LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL -> {
                checkOrdered(left, right);
                yield PrimitiveType.BOOLEAN;
            }
            case AND, OR -> {
                checkBoolean(left);
                checkBoolean(right);
                yield PrimitiveType.BOOLEAN;
            }
            case CONCAT -> PrimitiveType.STRING;
        };
    }
}
```
