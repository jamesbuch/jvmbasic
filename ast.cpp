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
        case Type::UserDefined: return "UserDefined";
        case Type::Decimal: return "Decimal";  // Phase 9
        case Type::BigInt: return "BigInt";    // Phase 9
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
        case Op::Shl: return "<<";      // Phase 9
        case Op::Shr: return ">>";      // Phase 9
        case Op::BitAnd: return "&";    // Phase 9
        case Op::BitOr: return "|";     // Phase 9
        case Op::BitXor: return "^";    // Phase 9
        default: return "?";
    }
}

