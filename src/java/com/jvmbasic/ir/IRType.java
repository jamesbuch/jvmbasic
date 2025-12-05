package com.jvmbasic.ir;

/**
 * Represents types in the IR.
 *
 * Types are resolved during IR generation, so the IR has full type information.
 */
public sealed interface IRType {

    /**
     * Get the JVM type descriptor (e.g., "I" for int, "Ljava/lang/String;" for String)
     */
    String descriptor();

    /**
     * Get the JVM internal name (e.g., "java/lang/String")
     */
    default String internalName() {
        return null;
    }

    /**
     * Check if this is a primitive type
     */
    default boolean isPrimitive() {
        return this instanceof Primitive;
    }

    /**
     * Check if this type can hold null
     */
    default boolean isNullable() {
        return !isPrimitive() || this instanceof Nullable;
    }

    // Primitive types
    record Primitive(Kind kind) implements IRType {
        public enum Kind {
            VOID("V"),
            BOOLEAN("Z"),
            BYTE("B"),
            CHAR("C"),
            INT("I"),
            LONG("J"),
            FLOAT("F"),
            DOUBLE("D");

            private final String desc;
            Kind(String desc) { this.desc = desc; }
            public String descriptor() { return desc; }
        }

        @Override
        public String descriptor() {
            return kind.descriptor();
        }

        public static final Primitive VOID = new Primitive(Kind.VOID);
        public static final Primitive BOOLEAN = new Primitive(Kind.BOOLEAN);
        public static final Primitive BYTE = new Primitive(Kind.BYTE);
        public static final Primitive CHAR = new Primitive(Kind.CHAR);
        public static final Primitive INT = new Primitive(Kind.INT);
        public static final Primitive LONG = new Primitive(Kind.LONG);
        public static final Primitive FLOAT = new Primitive(Kind.FLOAT);
        public static final Primitive DOUBLE = new Primitive(Kind.DOUBLE);
    }

    // Reference types (classes, interfaces)
    record Reference(String name) implements IRType {
        @Override
        public String descriptor() {
            return "L" + name.replace('.', '/') + ";";
        }

        @Override
        public String internalName() {
            return name.replace('.', '/');
        }

        public static final Reference STRING = new Reference("java.lang.String");
        public static final Reference OBJECT = new Reference("java.lang.Object");
    }

    // Array types
    record Array(IRType elementType) implements IRType {
        @Override
        public String descriptor() {
            return "[" + elementType.descriptor();
        }
    }

    // Nullable wrapper (Type?)
    record Nullable(IRType wrapped) implements IRType {
        @Override
        public String descriptor() {
            return wrapped.descriptor();
        }

        @Override
        public String internalName() {
            return wrapped.internalName();
        }

        @Override
        public boolean isNullable() {
            return true;
        }
    }

    // Function types (for lambdas)
    record Function(java.util.List<IRType> paramTypes, IRType returnType) implements IRType {
        @Override
        public String descriptor() {
            StringBuilder sb = new StringBuilder("(");
            for (IRType pt : paramTypes) {
                sb.append(pt.descriptor());
            }
            sb.append(")").append(returnType.descriptor());
            return sb.toString();
        }
    }
}
