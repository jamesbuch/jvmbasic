#include "builtin_functions.h"

const map<string, FunctionSig> builtinFunctions = {
    // Math functions - single parameter
    {"ABS", {{Type::Float}, Type::Float, "abs_f", "(F)F"}},
    {"SQR", {{Type::Float}, Type::Float, "sqr", "(F)F"}},
    {"SQRT", {{Type::Float}, Type::Float, "sqr", "(F)F"}},  // Alias
    {"INT", {{Type::Float}, Type::Int, "int_f", "(F)I"}},
    {"SGN", {{Type::Float}, Type::Int, "sgn_f", "(F)I"}},
    {"SIN", {{Type::Float}, Type::Float, "sin", "(F)F"}},
    {"COS", {{Type::Float}, Type::Float, "cos", "(F)F"}},
    {"TAN", {{Type::Float}, Type::Float, "tan", "(F)F"}},
    {"ASIN", {{Type::Float}, Type::Float, "asin", "(F)F"}},
    {"ACOS", {{Type::Float}, Type::Float, "acos", "(F)F"}},
    {"ATAN", {{Type::Float}, Type::Float, "atan", "(F)F"}},
    {"EXP", {{Type::Float}, Type::Float, "exp", "(F)F"}},
    {"LOG", {{Type::Float}, Type::Float, "log", "(F)F"}},
    {"LOG10", {{Type::Float}, Type::Float, "log10", "(F)F"}},
    {"ROUND", {{Type::Float}, Type::Int, "round", "(F)I"}},
    {"CEIL", {{Type::Float}, Type::Float, "ceil", "(F)F"}},
    {"FLOOR", {{Type::Float}, Type::Float, "floor", "(F)F"}},
    
    // Math functions - no parameters
    {"RND", {{}, Type::Float, "rnd", "()F"}},
    {"PI", {{}, Type::Float, "pi", "()F"}},
    {"E", {{}, Type::Float, "e", "()F"}},

    // Math functions - one and two parameters, random numbers
    {"RNDI", {{Type::Int}, Type::Int, "rnd_i", "(I)I"}},
    {"RNDINT", {{Type::Int, Type::Int}, Type::Int, "rnd_i_ranged", "(II)I"}},
    
    // Math functions - two parameters
    {"POW", {{Type::Float, Type::Float}, Type::Float, "pow", "(FF)F"}},
    {"ATAN2", {{Type::Float, Type::Float}, Type::Float, "atan2", "(FF)F"}},
    {"MIN", {{Type::Float, Type::Float}, Type::Float, "min_ff", "(FF)F"}},
    {"MAX", {{Type::Float, Type::Float}, Type::Float, "max_ff", "(FF)F"}},
    
    // String functions - single parameter
    {"LEN", {{Type::String}, Type::Int, "len", "(Ljava/lang/String;)I"}},
    {"UPPER", {{Type::String}, Type::String, "upper", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"UCASE", {{Type::String}, Type::String, "upper", "(Ljava/lang/String;)Ljava/lang/String;"}},  // Alias
    {"LOWER", {{Type::String}, Type::String, "lower", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"LCASE", {{Type::String}, Type::String, "lower", "(Ljava/lang/String;)Ljava/lang/String;"}},  // Alias
    {"TRIM", {{Type::String}, Type::String, "trim", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"LTRIM", {{Type::String}, Type::String, "ltrim", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"RTRIM", {{Type::String}, Type::String, "rtrim", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"REVERSE", {{Type::String}, Type::String, "reverse", "(Ljava/lang/String;)Ljava/lang/String;"}},
    {"ASC", {{Type::String}, Type::Int, "asc", "(Ljava/lang/String;)I"}},
    
    // String functions - two parameters
    {"LEFT", {{Type::String, Type::Int}, Type::String, "left", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"RIGHT", {{Type::String, Type::Int}, Type::String, "right", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"INSTR", {{Type::String, Type::String}, Type::Int, "instr", "(Ljava/lang/String;Ljava/lang/String;)I"}},
    {"CONTAINS", {{Type::String, Type::String}, Type::Bool, "contains", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"SPACE", {{Type::Int}, Type::String, "space", "(I)Ljava/lang/String;"}},
    
    // String functions - three parameters
    {"MID", {{Type::String, Type::Int, Type::Int}, Type::String, "mid", "(Ljava/lang/String;II)Ljava/lang/String;"}},
    {"SUBSTR", {{Type::String, Type::Int, Type::Int}, Type::String, "mid", "(Ljava/lang/String;II)Ljava/lang/String;"}},  // Alias
    {"STRING", {{Type::Int, Type::String}, Type::String, "string", "(ILjava/lang/String;)Ljava/lang/String;"}},
    
    // Type conversion
    {"CHR", {{Type::Int}, Type::String, "chr", "(I)Ljava/lang/String;"}},
    {"VAL", {{Type::String}, Type::Float, "val_f", "(Ljava/lang/String;)F"}},
    
    // Type checking
    {"ISNUM", {{Type::String}, Type::Bool, "isnum", "(Ljava/lang/String;)Z"}},
    {"ISINT", {{Type::String}, Type::Bool, "isint", "(Ljava/lang/String;)Z"}},
    
    // Array utility functions (that return values)
    {"MINARRAY", {{Type::IntArray}, Type::Int, "min_ia", "([I)I"}},
    {"MAXARRAY", {{Type::IntArray}, Type::Int, "max_ia", "([I)I"}},
    {"SUMARRAY", {{Type::IntArray}, Type::Int, "sum_ia", "([I)I"}},
    {"UBOUND", {{Type::IntArray}, Type::Int, "ubound_ia", "([I)I"}},
    
    // File I/O
    {"OPENINPUT", {{Type::String}, Type::Int, "openInput", "(Ljava/lang/String;)I"}},
    {"OPENOUTPUT", {{Type::String}, Type::Int, "openOutput", "(Ljava/lang/String;)I"}},
    {"READLINE", {{Type::Int}, Type::String, "readLine", "(I)Ljava/lang/String;"}},
    {"WRITELINE", {{Type::Int, Type::String}, Type::Int, "writeLine", "(ILjava/lang/String;)V"}},
    {"WRITETEXT", {{Type::Int, Type::String}, Type::Int, "writeText", "(ILjava/lang/String;)V"}},
    {"CLOSEFILE", {{Type::Int}, Type::Int, "closeFile", "(I)V"}},
    {"FILEEXISTS", {{Type::String}, Type::Bool, "fileExists", "(Ljava/lang/String;)Z"}},
    {"DELETEFILE", {{Type::String}, Type::Bool, "deleteFile", "(Ljava/lang/String;)Z"}},
    
    // Regular Expressions
    {"REGEXMATCH", {{Type::String, Type::String}, Type::Bool, "regexMatch", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"REGEXFIND", {{Type::String, Type::String}, Type::String, "regexFind", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"REGEXREPLACE", {{Type::String, Type::String, Type::String}, Type::String, "regexReplace", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"REGEXGROUP", {{Type::String, Type::String, Type::Int}, Type::String, "regexGroup", "(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;"}},
    
    // Enhanced String Functions
    {"FORMAT", {{Type::String, Type::String}, Type::String, "format1", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"FORMATF", {{Type::String, Type::Float}, Type::String, "format1_f", "(Ljava/lang/String;F)Ljava/lang/String;"}},
    {"FORMATI", {{Type::String, Type::Int}, Type::String, "format1_i", "(Ljava/lang/String;I)Ljava/lang/String;"}},
};

