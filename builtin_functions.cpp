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
    {"WRITELINE", {{Type::Int, Type::String}, Type::Int, "writeLine", "(ILjava/lang/String;)I"}},
    {"WRITETEXT", {{Type::Int, Type::String}, Type::Int, "writeText", "(ILjava/lang/String;)I"}},
    {"CLOSEFILE", {{Type::Int}, Type::Int, "closeFile", "(I)I"}},
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
    
    // Phase 8: Advanced String Functions
    {"REPLACE", {{Type::String, Type::String, Type::String}, Type::String, "replace", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"REPLACEALL", {{Type::String, Type::String, Type::String}, Type::String, "replaceAll", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"STARTSWITH", {{Type::String, Type::String}, Type::Bool, "startsWith", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"ENDSWITH", {{Type::String, Type::String}, Type::Bool, "endsWith", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"INDEXOF", {{Type::String, Type::String}, Type::Int, "indexOf", "(Ljava/lang/String;Ljava/lang/String;)I"}},
    {"LASTINDEXOF", {{Type::String, Type::String}, Type::Int, "lastIndexOf", "(Ljava/lang/String;Ljava/lang/String;)I"}},
    {"CONCAT", {{Type::String, Type::String}, Type::String, "concat", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"CONCAT3", {{Type::String, Type::String, Type::String}, Type::String, "concat3", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"REPEAT", {{Type::String, Type::Int}, Type::String, "repeat", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"PADLEFT", {{Type::String, Type::Int}, Type::String, "padLeft", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"PADRIGHT", {{Type::String, Type::Int}, Type::String, "padRight", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"SUBSTRING", {{Type::String, Type::Int}, Type::String, "substring", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"SUBSTRINGLEN", {{Type::String, Type::Int, Type::Int}, Type::String, "substringLen", "(Ljava/lang/String;II)Ljava/lang/String;"}},
    {"STRCMP", {{Type::String, Type::String}, Type::Int, "strcmp", "(Ljava/lang/String;Ljava/lang/String;)I"}},
    {"STRICMP", {{Type::String, Type::String}, Type::Int, "stricmp", "(Ljava/lang/String;Ljava/lang/String;)I"}},
    {"EQUALS", {{Type::String, Type::String}, Type::Bool, "equals", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"EQUALSIGNORECASE", {{Type::String, Type::String}, Type::Bool, "equalsIgnoreCase", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"CHAR", {{Type::String, Type::Int}, Type::String, "charAt", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"CHARAT", {{Type::String, Type::Int}, Type::String, "charAt", "(Ljava/lang/String;I)Ljava/lang/String;"}},
    {"CHARCODE", {{Type::String, Type::Int}, Type::Int, "charCodeAt", "(Ljava/lang/String;I)I"}},
    {"CHARCODEAT", {{Type::String, Type::Int}, Type::Int, "charCodeAt", "(Ljava/lang/String;I)I"}},
    
    // Phase 8: Date/Time Functions
    {"NOW", {{}, Type::Float, "now", "()F"}},
    {"DATE", {{}, Type::String, "date", "()Ljava/lang/String;"}},
    {"TIME", {{}, Type::String, "time", "()Ljava/lang/String;"}},
    {"DATETIME", {{}, Type::String, "datetime", "()Ljava/lang/String;"}},
    {"YEAR", {{Type::Float}, Type::Int, "year", "(F)I"}},
    {"MONTH", {{Type::Float}, Type::Int, "month", "(F)I"}},
    {"DAY", {{Type::Float}, Type::Int, "day", "(F)I"}},
    {"DAYOFWEEK", {{Type::Float}, Type::Int, "dayOfWeek", "(F)I"}},
    {"DAYOFYEAR", {{Type::Float}, Type::Int, "dayOfYear", "(F)I"}},
    {"HOUR", {{Type::Float}, Type::Int, "hour", "(F)I"}},
    {"MINUTE", {{Type::Float}, Type::Int, "minute", "(F)I"}},
    {"SECOND", {{Type::Float}, Type::Int, "second", "(F)I"}},
    {"MILLISECOND", {{Type::Float}, Type::Int, "millisecond", "(F)I"}},
    {"ADDDAYS", {{Type::Float, Type::Int}, Type::Float, "addDays", "(FI)F"}},
    {"ADDHOURS", {{Type::Float, Type::Int}, Type::Float, "addHours", "(FI)F"}},
    {"ADDMINUTES", {{Type::Float, Type::Int}, Type::Float, "addMinutes", "(FI)F"}},
    {"ADDSECONDS", {{Type::Float, Type::Int}, Type::Float, "addSeconds", "(FI)F"}},
    {"ADDMONTHS", {{Type::Float, Type::Int}, Type::Float, "addMonths", "(FI)F"}},
    {"ADDYEARS", {{Type::Float, Type::Int}, Type::Float, "addYears", "(FI)F"}},
    {"DATEDIFF", {{Type::Float, Type::Float}, Type::Int, "dateDiff", "(FF)I"}},
    {"FORMATDATE", {{Type::Float, Type::String}, Type::String, "formatDate", "(FLjava/lang/String;)Ljava/lang/String;"}},
    
    // Phase 8: Timing Functions
    {"TIMER", {{}, Type::Float, "timer", "()F"}},
    {"NANOSECONDS", {{}, Type::Float, "nanoseconds", "()F"}},
    {"SLEEP", {{Type::Int}, Type::Int, "sleep_i", "(I)I"}},
    
    // Phase 8: Character I/O
    {"READCHAR", {{Type::Int}, Type::Int, "readChar", "(I)I"}},
    {"WRITECHAR", {{Type::Int, Type::Int}, Type::Int, "writeChar_i", "(II)I"}},
    {"HASMORE", {{Type::Int}, Type::Bool, "hasMore", "(I)Z"}},
    {"ISEOF", {{Type::Int}, Type::Bool, "isEof", "(I)Z"}},
    {"FLUSH", {{Type::Int}, Type::Int, "flush_i", "(I)I"}},
    
    // Phase 8: Advanced File I/O
    {"FILESIZE", {{Type::String}, Type::Float, "fileSize", "(Ljava/lang/String;)F"}},
    {"RENAME", {{Type::String, Type::String}, Type::Bool, "rename", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"COPY", {{Type::String, Type::String}, Type::Bool, "copy", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"MOVE", {{Type::String, Type::String}, Type::Bool, "move", "(Ljava/lang/String;Ljava/lang/String;)Z"}},
    {"ISFILE", {{Type::String}, Type::Bool, "isFile", "(Ljava/lang/String;)Z"}},
    {"ISDIR", {{Type::String}, Type::Bool, "isDir", "(Ljava/lang/String;)Z"}},
    {"MKDIR", {{Type::String}, Type::Bool, "mkdir", "(Ljava/lang/String;)Z"}},
    {"MKDIRS", {{Type::String}, Type::Bool, "mkdirs", "(Ljava/lang/String;)Z"}},
    {"RMDIR", {{Type::String}, Type::Bool, "rmdir", "(Ljava/lang/String;)Z"}},
    {"CURRENTDIR", {{}, Type::String, "currentDir", "()Ljava/lang/String;"}},
    {"ABSOLUTEPATH", {{Type::String}, Type::String, "absolutePath", "(Ljava/lang/String;)Ljava/lang/String;"}},
    
    // Phase 8.2: Collections - IntList (no underscores - lexer limitation)
    {"INTLISTNEW", {{}, Type::Int, "intListNew", "()I"}},
    {"INTLISTADD", {{Type::Int, Type::Int}, Type::Int, "intListAdd", "(II)I"}},
    {"INTLISTGET", {{Type::Int, Type::Int}, Type::Int, "intListGet", "(II)I"}},
    {"INTLISTSET", {{Type::Int, Type::Int, Type::Int}, Type::Int, "intListSet", "(III)I"}},
    {"INTLISTSIZE", {{Type::Int}, Type::Int, "intListSize", "(I)I"}},
    {"INTLISTREMOVE", {{Type::Int, Type::Int}, Type::Int, "intListRemove", "(II)I"}},
    {"INTLISTCONTAINS", {{Type::Int, Type::Int}, Type::Bool, "intListContains", "(II)Z"}},
    {"INTLISTINDEXOF", {{Type::Int, Type::Int}, Type::Int, "intListIndexOf", "(II)I"}},
    {"INTLISTCLEAR", {{Type::Int}, Type::Int, "intListClear", "(I)I"}},
    {"INTLISTTOARRAY", {{Type::Int}, Type::IntArray, "intListToArray", "(I)[I"}},
    
    // Phase 8.2: Collections - StringList
    {"STRINGLISTNEW", {{}, Type::Int, "stringListNew", "()I"}},
    {"STRINGLISTADD", {{Type::Int, Type::String}, Type::Int, "stringListAdd", "(ILjava/lang/String;)I"}},
    {"STRINGLISTGET", {{Type::Int, Type::Int}, Type::String, "stringListGet", "(II)Ljava/lang/String;"}},
    {"STRINGLISTSET", {{Type::Int, Type::Int, Type::String}, Type::String, "stringListSet", "(IILjava/lang/String;)Ljava/lang/String;"}},
    {"STRINGLISTSIZE", {{Type::Int}, Type::Int, "stringListSize", "(I)I"}},
    {"STRINGLISTREMOVE", {{Type::Int, Type::Int}, Type::String, "stringListRemove", "(II)Ljava/lang/String;"}},
    {"STRINGLISTCONTAINS", {{Type::Int, Type::String}, Type::Bool, "stringListContains", "(ILjava/lang/String;)Z"}},
    {"STRINGLISTINDEXOF", {{Type::Int, Type::String}, Type::Int, "stringListIndexOf", "(ILjava/lang/String;)I"}},
    {"STRINGLISTCLEAR", {{Type::Int}, Type::Int, "stringListClear", "(I)I"}},
    {"STRINGLISTTOARRAY", {{Type::Int}, Type::StringArray, "stringListToArray", "(I)[Ljava/lang/String;"}},
    
    // Phase 8.2: Collections - Map
    {"MAPNEW", {{}, Type::Int, "mapNew", "()I"}},
    {"MAPPUT", {{Type::Int, Type::String, Type::String}, Type::String, "mapPut", "(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;"}},
    {"MAPGET", {{Type::Int, Type::String}, Type::String, "mapGet", "(ILjava/lang/String;)Ljava/lang/String;"}},
    {"MAPCONTAINSKEY", {{Type::Int, Type::String}, Type::Bool, "mapContainsKey", "(ILjava/lang/String;)Z"}},
    {"MAPREMOVE", {{Type::Int, Type::String}, Type::String, "mapRemove", "(ILjava/lang/String;)Ljava/lang/String;"}},
    {"MAPSIZE", {{Type::Int}, Type::Int, "mapSize", "(I)I"}},
    {"MAPCLEAR", {{Type::Int}, Type::Int, "mapClear", "(I)I"}},
    {"MAPKEYS", {{Type::Int}, Type::StringArray, "mapKeys", "(I)[Ljava/lang/String;"}},
    {"MAPVALUES", {{Type::Int}, Type::StringArray, "mapValues", "(I)[Ljava/lang/String;"}},
    
    // Phase 8.2: Collections - Stack
    {"STACKNEW", {{}, Type::Int, "stackNew", "()I"}},
    {"STACKPUSH", {{Type::Int, Type::String}, Type::Int, "stackPush", "(ILjava/lang/String;)I"}},
    {"STACKPOP", {{Type::Int}, Type::String, "stackPop", "(I)Ljava/lang/String;"}},
    {"STACKPEEK", {{Type::Int}, Type::String, "stackPeek", "(I)Ljava/lang/String;"}},
    {"STACKISEMPTY", {{Type::Int}, Type::Bool, "stackIsEmpty", "(I)Z"}},
    {"STACKSIZE", {{Type::Int}, Type::Int, "stackSize", "(I)I"}},
    {"STACKCLEAR", {{Type::Int}, Type::Int, "stackClear", "(I)I"}},
    
    // Phase 8.2: Collections - Queue
    {"QUEUENEW", {{}, Type::Int, "queueNew", "()I"}},
    {"QUEUEENQUEUE", {{Type::Int, Type::String}, Type::Int, "queueEnqueue", "(ILjava/lang/String;)I"}},
    {"QUEUEDEQUEUE", {{Type::Int}, Type::String, "queueDequeue", "(I)Ljava/lang/String;"}},
    {"QUEUEPEEK", {{Type::Int}, Type::String, "queuePeek", "(I)Ljava/lang/String;"}},
    {"QUEUEISEMPTY", {{Type::Int}, Type::Bool, "queueIsEmpty", "(I)Z"}},
    {"QUEUESIZE", {{Type::Int}, Type::Int, "queueSize", "(I)I"}},
    {"QUEUECLEAR", {{Type::Int}, Type::Int, "queueClear", "(I)I"}},
    
    // Phase 9: Console I/O (modern VB-style)
    {"CONSOLEWRITELINE", {{Type::String}, Type::Int, "consoleWriteLine", "(Ljava/lang/String;)I"}},
    {"CONSOLEWRITE", {{Type::String}, Type::Int, "consoleWrite", "(Ljava/lang/String;)I"}},
    {"CONSOLEREADLINE", {{}, Type::String, "consoleReadLine", "()Ljava/lang/String;"}},
    {"CONSOLEREADKEY", {{}, Type::String, "consoleReadKey", "()Ljava/lang/String;"}},
};

