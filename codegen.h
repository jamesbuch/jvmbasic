#pragma once

#include "ast.h"
#include <iostream>
#include <vector>
#include <map>
#include <memory>

using namespace std;
using u1 = uint8_t;
using u2 = uint16_t;
using u4 = uint32_t;

// Forward declarations from AST
struct Decl;
struct Stmt;
using DeclPtr = unique_ptr<Decl>;
using StmtPtr = unique_ptr<Stmt>;

// ClassFile generator - generates JVM bytecode
// Exposed for use by BasicCompiler in jvmbasic.cpp
class ClassFile;

