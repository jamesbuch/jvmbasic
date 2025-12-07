#pragma once

#include "ast.h"
#include <map>
#include <string>

using namespace std;

// Built-in function signature
struct FunctionSig {
    vector<Type> paramTypes;
    Type returnType;
    string javaMethod;
    string descriptor;
};

// Registry of all built-in functions
extern const map<string, FunctionSig> builtinFunctions;

