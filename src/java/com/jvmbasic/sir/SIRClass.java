package com.jvmbasic.sir;

import com.jvmbasic.ir.IRType;

import java.util.ArrayList;
import java.util.List;

/**
 * A class in Stack IR form.
 */
public class SIRClass {
    private final String name;
    private final String superClass;
    private final List<String> interfaces;
    private final List<Field> fields;
    private final List<SIRFunction> methods;
    private final boolean isAbstract;

    public record Field(String name, IRType type, boolean isStatic, int access) {}

    public SIRClass(String name, String superClass, boolean isAbstract) {
        this.name = name;
        this.superClass = superClass != null ? superClass : "java/lang/Object";
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.isAbstract = isAbstract;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<SIRFunction> getMethods() {
        return methods;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void addInterface(String iface) {
        interfaces.add(iface);
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public void addMethod(SIRFunction method) {
        methods.add(method);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Class declaration
        if (isAbstract) {
            sb.append("abstract ");
        }
        sb.append("class ").append(name);
        if (!superClass.equals("java/lang/Object")) {
            sb.append(" extends ").append(superClass);
        }
        if (!interfaces.isEmpty()) {
            sb.append(" implements ");
            sb.append(String.join(", ", interfaces));
        }
        sb.append("\n");

        // Fields
        for (Field field : fields) {
            sb.append("  ");
            if (field.isStatic()) sb.append("static ");
            sb.append("field ").append(field.name()).append(": ").append(field.type()).append("\n");
        }

        // Methods
        for (SIRFunction method : methods) {
            sb.append("\n");
            // Indent method output
            String methodStr = method.toString();
            for (String line : methodStr.split("\n")) {
                sb.append("  ").append(line).append("\n");
            }
        }

        sb.append("end class\n");
        return sb.toString();
    }
}
