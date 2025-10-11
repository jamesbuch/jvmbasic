#include "ast.h"

string typeToString(Type t) {
    switch(t) {
        case Type::Int: return "Int";
        case Type::Float: return "Float";
        case Type::String: return "String";
        case Type::Bool: return "Bool";
        case Type::IntArray: return "IntArray";
        case Type::FloatArray: return "FloatArray";
        case Type::StringArray: return "StringArray";
        case Type::BoolArray: return "BoolArray";
        default: return "Unknown";
    }
}

string opToString(Op op) {
    switch(op) {
        case Op::Add: return "+";
        case Op::Sub: return "-";
        case Op::Mul: return "*";
        case Op::Div: return "/";
        case Op::Mod: return "%";
        case Op::Lt: return "<";
        case Op::Gt: return ">";
        case Op::Le: return "<=";
        case Op::Ge: return ">=";
        case Op::Eq: return "==";
        case Op::Ne: return "<>";
        default: return "?";
    }
}

