package com.jvmbasic;

import com.jvmbasic.grammar.*;
import com.jvmbasic.ir.*;
import com.jvmbasic.semantic.*;
import com.jvmbasic.sir.*;
import com.jvmbasic.test.TestRunner;
import com.jvmbasic.visitor.CompilerVisitor;
import com.jvmbasic.visitor.DebugListener;
import com.jvmbasic.visitor.SymbolCollector;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * JVM BASIC Compiler - Main Entry Point
 *
 * A modern BASIC compiler targeting the JVM.
 *
 * Usage:
 *   java -jar jvmbasic.jar [options] <source.bas>
 *
 * Options:
 *   -o <name>     Output class name (default: derived from source file)
 *   -d            Enable debug output (uses listener for trace)
 *   -ast          Print AST structure
 *   -tokens       Print token stream
 *   -help         Show this help
 */
public class Main {

    private static boolean debugMode = false;
    private static boolean showAst = false;
    private static boolean showTokens = false;
    private static boolean showTree = false;
    private static boolean showIr = false;
    private static boolean showSir = false;
    private static boolean parseOnly = false;
    private static boolean semanticCheck = false;
    private static boolean outputAstFile = false;
    private static boolean outputTreeFile = false;
    private static boolean outputIrFile = false;
    private static boolean outputSirFile = false;
    private static boolean testMode = false;
    private static String outputName = null;
    private static String outputDir = null;
    private static String sourceFile = null;
    private static List<String> libraryFiles = new ArrayList<>();

    public static void main(String[] args) {
        try {
            parseArgs(args);

            if (sourceFile == null) {
                printUsage();
                System.exit(1);
            }

            compile(sourceFile);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if (i + 1 < args.length) {
                        outputName = args[++i];
                    }
                    break;
                case "-d":
                    debugMode = true;
                    break;
                case "-ast":
                    showAst = true;
                    break;
                case "-tokens":
                    showTokens = true;
                    break;
                case "-tree":
                    showTree = true;
                    break;
                case "-parse-only":
                    parseOnly = true;
                    break;
                case "-ir":
                    showIr = true;
                    break;
                case "-sir":
                    showSir = true;
                    break;
                case "--output-ast":
                    outputAstFile = true;
                    break;
                case "--output-tree":
                    outputTreeFile = true;
                    break;
                case "--output-ir":
                    outputIrFile = true;
                    break;
                case "--output-sir":
                    outputSirFile = true;
                    break;
                case "--output-all":
                    outputAstFile = true;
                    outputTreeFile = true;
                    outputIrFile = true;
                    outputSirFile = true;
                    break;
                case "-semantic":
                    semanticCheck = true;
                    break;
                case "--test":
                case "-test":
                    testMode = true;
                    break;
                case "-outdir":
                case "--outdir":
                    if (i + 1 < args.length) {
                        outputDir = args[++i];
                    }
                    break;
                case "-help":
                case "--help":
                    printUsage();
                    System.exit(0);
                    break;
                default:
                    if (!args[i].startsWith("-")) {
                        if (sourceFile == null) {
                            sourceFile = args[i];
                        } else {
                            // Additional source files are libraries
                            libraryFiles.add(args[i]);
                        }
                    }
                    break;
            }
        }
    }

    private static void printUsage() {
        System.out.println("""
            JVM BASIC Compiler v2.0

            Usage: java -jar jvmbasic.jar [options] <source.jvmb>

            Options:
              -o <name>       Output class name (default: derived from source file)
              -outdir <dir>   Output directory for .class files (default: current directory)
              -d              Enable debug output (uses listener for trace)
              -ast            Print AST structure (compact, single line)
              -tree           Print parse tree (pretty-printed, indented)
              -ir             Print intermediate representation (tree-based, readable)
              -sir            Print stack IR (SSA-style, for codegen)
              -tokens         Print token stream
              -parse-only     Parse without code generation
              -semantic       Run semantic analysis (type checking, reference checking)
              --test          Run in test mode (discover and execute #[Test] methods)
              --output-ast    Write AST to <source>.ast file
              --output-tree   Write parse tree to <source>.tree file
              --output-ir     Write IR to <source>.ir file
              --output-sir    Write stack IR to <source>.sir file
              --output-all    Write all output files (.ast, .tree, .ir, .sir)
              -help           Show this help

            File Extensions:
              .jvmb           Standard JVM BASIC source file
              .jvmt           Test file (automatically enables --test mode)

            IR Representations:
              -ir   Tree-based IR - human readable, shows program structure
              -sir  Stack-based IR - SSA virtual registers, maps to JVM bytecode

            Example:
              java -jar jvmbasic.jar -o HelloWorld hello.jvmb
              java -jar jvmbasic.jar -tree -parse-only examples/hello.jvmb
              java -jar jvmbasic.jar -ir -sir -parse-only examples/hello.jvmb
              java -jar jvmbasic.jar --output-all -parse-only examples/hello.jvmb
              java -jar jvmbasic.jar --test tests/calculator_test.jvmt
              java -jar jvmbasic.jar main.jvmb mymodule.jvmb   (compile with library)
            """);
    }

    private static void compile(String sourcePath) throws IOException {
        // Read source file
        String source = Files.readString(Path.of(sourcePath));

        // Determine output name
        if (outputName == null) {
            outputName = Path.of(sourcePath)
                .getFileName()
                .toString()
                .replaceFirst("\\.(bas|jvmb|jvmt)$", "");
        }

        // Auto-detect test mode from file extension
        if (sourcePath.endsWith(".jvmt")) {
            testMode = true;
        }

        System.out.println("Compiling: " + sourcePath + " -> " + outputName + ".class");

        // Create lexer and parser
        CharStream input = CharStreams.fromString(source);
        JvmBasicLexer lexer = new JvmBasicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JvmBasicParser parser = new JvmBasicParser(tokens);

        // Add error handling
        parser.removeErrorListeners();
        parser.addErrorListener(new CompilerErrorListener(sourcePath));

        // Show tokens if requested
        if (showTokens) {
            tokens.fill();
            for (Token token : tokens.getTokens()) {
                System.out.println(token);
            }
        }

        // Parse
        JvmBasicParser.CompilationUnitContext tree = parser.compilationUnit();

        // Check for syntax errors
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.err.println("Compilation failed with " + parser.getNumberOfSyntaxErrors() + " syntax error(s)");
            System.exit(1);
        }

        // Show AST if requested (compact, single line)
        String astContent = tree.toStringTree(parser);
        if (showAst) {
            System.out.println("\nParse Tree (compact):");
            System.out.println(astContent);
        }

        // Output AST to file if requested
        if (outputAstFile) {
            String astPath = sourcePath + ".ast";
            Files.writeString(Path.of(astPath), astContent);
            System.out.println("Wrote AST to: " + astPath);
        }

        // Show pretty-printed tree if requested
        if (showTree || outputTreeFile) {
            StringBuilder treeBuilder = new StringBuilder();
            buildTreeString(tree, parser, 0, treeBuilder);
            String treeContent = treeBuilder.toString();

            if (showTree) {
                System.out.println("\nParse Tree (pretty):");
                System.out.println(treeContent);
            }

            // Output tree to file if requested
            if (outputTreeFile) {
                String treePath = sourcePath + ".tree";
                Files.writeString(Path.of(treePath), treeContent);
                System.out.println("Wrote parse tree to: " + treePath);
            }
        }

        // Build IR if requested or needed
        IRCompilationUnit irUnit = null;
        if (showIr || showSir || outputIrFile || outputSirFile || semanticCheck || !parseOnly) {
            System.out.println("\n=== Building IR ===");
            try {
                IRBuilder irBuilder = new IRBuilder(outputName);
                irUnit = irBuilder.build(tree);
            } catch (Exception e) {
                // IR building failed, but we can continue with direct code generation
                if (debugMode) {
                    System.err.println("Warning: IR building failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            String irContent = irUnit != null ? irUnit.toString() : "";

            if (showIr) {
                System.out.println("\nIntermediate Representation (Tree IR):");
                System.out.println(irContent);
            }

            // Output IR to file if requested
            if (outputIrFile) {
                String irPath = sourcePath + ".ir";
                Files.writeString(Path.of(irPath), irContent);
                System.out.println("Wrote IR to: " + irPath);
            }

            // Semantic analysis if requested
            if (semanticCheck) {
                System.out.println("\n=== Semantic Analysis ===");
                SemanticAnalyzer analyzer = new SemanticAnalyzer();
                SemanticResult result = analyzer.analyze(irUnit);
                result.printReport();

                if (!result.isSuccess()) {
                    System.err.println("Compilation failed due to semantic errors");
                    System.exit(1);
                }
            }
        }

        // Build Stack IR if requested
        if ((showSir || outputSirFile) && irUnit != null) {
            System.out.println("\n=== Lowering to Stack IR ===");
            IRLowering lowering = new IRLowering();
            SIRModule sirModule = lowering.lower(irUnit);
            String sirContent = sirModule.toString();

            if (showSir) {
                System.out.println("\nStack IR (SSA):");
                System.out.println(sirContent);
            }

            // Output SIR to file if requested
            if (outputSirFile) {
                String sirPath = sourcePath + ".sir";
                Files.writeString(Path.of(sirPath), sirContent);
                System.out.println("Wrote Stack IR to: " + sirPath);
            }
        }

        // If parse-only mode, stop here
        if (parseOnly) {
            System.out.println("\nParse successful - no code generation (parse-only mode)");
            return;
        }

        // Pass 1: Use listener to collect symbols and debug info
        if (debugMode) {
            System.out.println("\n=== Pass 1: Symbol Collection (Listener) ===");
            DebugListener debugListener = new DebugListener(parser);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(debugListener, tree);
        }

        // Collect symbols from library files first (for module imports)
        SymbolCollector symbolCollector = new SymbolCollector();
        ParseTreeWalker walker = new ParseTreeWalker();

        // Process library files to collect their symbols and generate their classes
        List<LibraryCompilation> libraryCompilations = new ArrayList<>();
        for (String libPath : libraryFiles) {
            String libSource = Files.readString(Path.of(libPath));
            CharStream libInput = CharStreams.fromString(libSource);
            JvmBasicLexer libLexer = new JvmBasicLexer(libInput);
            CommonTokenStream libTokens = new CommonTokenStream(libLexer);
            JvmBasicParser libParser = new JvmBasicParser(libTokens);
            libParser.removeErrorListeners();
            libParser.addErrorListener(new CompilerErrorListener(libPath));

            JvmBasicParser.CompilationUnitContext libTree = libParser.compilationUnit();
            if (libParser.getNumberOfSyntaxErrors() > 0) {
                System.err.println("Library " + libPath + " failed with " + libParser.getNumberOfSyntaxErrors() + " syntax error(s)");
                System.exit(1);
            }

            // Collect symbols from library
            walker.walk(symbolCollector, libTree);

            // Determine library output name
            String libOutputName = Path.of(libPath)
                .getFileName()
                .toString()
                .replaceFirst("\\.(bas|jvmb|jvmt)$", "");

            libraryCompilations.add(new LibraryCompilation(libPath, libOutputName, libTree));
            System.out.println("Loaded library: " + libPath);
        }

        // Collect symbols from main source file
        walker.walk(symbolCollector, tree);

        // Generate code for library files first
        for (LibraryCompilation lib : libraryCompilations) {
            CompilerVisitor libVisitor = new CompilerVisitor(lib.outputName, symbolCollector.getSymbols());
            libVisitor.visit(lib.tree);

            // Write library class files
            Path libOutputPath = outputDir != null
                ? Path.of(outputDir).resolve(lib.outputName + ".class")
                : Path.of(lib.outputName + ".class");
            Files.write(libOutputPath, libVisitor.getBytecode());
            System.out.println("Compiled library: " + libOutputPath);

            // Write generated classes from library (module classes)
            for (var entry : libVisitor.getGeneratedClasses().entrySet()) {
                String clsName = entry.getKey();
                byte[] clsBytecode = entry.getValue();
                Path clsPath = outputDir != null
                    ? Path.of(outputDir).resolve(clsName + ".class")
                    : Path.of(clsName + ".class");
                Path parent = clsPath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.write(clsPath, clsBytecode);
                System.out.println("Generated class: " + clsPath + " (" + clsBytecode.length + " bytes)");
            }
        }

        // Pass 2: Use visitor to generate code for main file
        if (debugMode) {
            System.out.println("\n=== Pass 2: Code Generation (Visitor) ===");
        }

        CompilerVisitor visitor = new CompilerVisitor(outputName, symbolCollector.getSymbols());
        visitor.visit(tree);

        // Determine output path
        Path outputPath;
        if (outputDir != null) {
            Path dirPath = Path.of(outputDir);
            Files.createDirectories(dirPath);
            outputPath = dirPath.resolve(outputName + ".class");
        } else {
            outputPath = Path.of(outputName + ".class");
        }

        // Write main class file
        byte[] bytecode = visitor.getBytecode();
        Files.write(outputPath, bytecode);

        System.out.println("Successfully compiled: " + outputPath + " (" + bytecode.length + " bytes)");

        // Write generated class files (user-defined classes)
        for (var entry : visitor.getGeneratedClasses().entrySet()) {
            String className = entry.getKey();
            byte[] classBytecode = entry.getValue();
            Path classPath;
            if (outputDir != null) {
                classPath = Path.of(outputDir).resolve(className + ".class");
            } else {
                classPath = Path.of(className + ".class");
            }
            // Create parent directories if needed (for module classes like Module/Class)
            Path parent = classPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(classPath, classBytecode);
            System.out.println("Generated class: " + classPath + " (" + classBytecode.length + " bytes)");
        }

        // Run tests if in test mode
        if (testMode) {
            runTests(outputPath, outputName);
        }
    }

    /**
     * Run tests discovered in the compiled class.
     */
    private static void runTests(Path classPath, String className) {
        System.out.println();
        try {
            // Create a class loader that can load the compiled class
            // Convert to absolute path and handle case where parent is null (file in current directory)
            Path absolutePath = classPath.toAbsolutePath();
            Path parentDir = absolutePath.getParent();
            if (parentDir == null) {
                parentDir = Path.of(".").toAbsolutePath();
            }
            URL[] urls = { parentDir.toUri().toURL() };
            try (URLClassLoader loader = new URLClassLoader(urls, Main.class.getClassLoader())) {
                Class<?> testClass = loader.loadClass(className);

                // Run tests using the TestRunner
                TestRunner runner = new TestRunner();
                runner.runTests(testClass);
                runner.printSummary();

                // Exit with appropriate code
                if (runner.hasFailures()) {
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    /**
     * Holds parsed library information for deferred compilation.
     */
    private record LibraryCompilation(String path, String outputName, JvmBasicParser.CompilationUnitContext tree) {}

    /**
     * Custom error listener for better error messages
     */
    static class CompilerErrorListener extends BaseErrorListener {
        private final String sourcePath;

        public CompilerErrorListener(String sourcePath) {
            this.sourcePath = sourcePath;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                               Object offendingSymbol,
                               int line,
                               int charPositionInLine,
                               String msg,
                               RecognitionException e) {
            System.err.println(sourcePath + ":" + line + ":" + charPositionInLine + ": error: " + msg);
        }
    }

    /**
     * Pretty-print a parse tree with indentation (to stdout)
     */
    private static void printTree(ParseTree tree, Parser parser, int indent) {
        StringBuilder sb = new StringBuilder();
        buildTreeString(tree, parser, indent, sb);
        System.out.print(sb);
    }

    /**
     * Build a pretty-printed parse tree string with indentation
     */
    private static void buildTreeString(ParseTree tree, Parser parser, int indent, StringBuilder sb) {
        String prefix = "  ".repeat(indent);

        if (tree instanceof RuleContext ctx) {
            String ruleName = parser.getRuleNames()[ctx.getRuleIndex()];
            sb.append(prefix).append(ruleName).append("\n");
            for (int i = 0; i < tree.getChildCount(); i++) {
                buildTreeString(tree.getChild(i), parser, indent + 1, sb);
            }
        } else if (tree instanceof TerminalNode term) {
            Token token = term.getSymbol();
            String tokenName = parser.getVocabulary().getSymbolicName(token.getType());
            if (tokenName == null) {
                tokenName = parser.getVocabulary().getLiteralName(token.getType());
            }
            if (tokenName == null) {
                tokenName = "TOKEN[" + token.getType() + "]";
            }
            sb.append(prefix).append(tokenName).append(": '").append(token.getText())
              .append("' (line ").append(token.getLine()).append(")\n");
        }
    }
}
