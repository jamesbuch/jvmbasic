package com.jvmbasic;

import com.jvmbasic.grammar.*;
import com.jvmbasic.ir.*;
import com.jvmbasic.semantic.*;
import com.jvmbasic.sir.*;
import com.jvmbasic.visitor.CompilerVisitor;
import com.jvmbasic.visitor.DebugListener;
import com.jvmbasic.visitor.SymbolCollector;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;
import java.nio.file.*;

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
    private static String outputName = null;
    private static String sourceFile = null;

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
                case "-help":
                case "--help":
                    printUsage();
                    System.exit(0);
                    break;
                default:
                    if (!args[i].startsWith("-")) {
                        sourceFile = args[i];
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
              -d              Enable debug output (uses listener for trace)
              -ast            Print AST structure (compact, single line)
              -tree           Print parse tree (pretty-printed, indented)
              -ir             Print intermediate representation (tree-based, readable)
              -sir            Print stack IR (SSA-style, for codegen)
              -tokens         Print token stream
              -parse-only     Parse without code generation
              -semantic       Run semantic analysis (type checking, reference checking)
              --output-ast    Write AST to <source>.ast file
              --output-tree   Write parse tree to <source>.tree file
              --output-ir     Write IR to <source>.ir file
              --output-sir    Write stack IR to <source>.sir file
              --output-all    Write all output files (.ast, .tree, .ir, .sir)
              -help           Show this help

            IR Representations:
              -ir   Tree-based IR - human readable, shows program structure
              -sir  Stack-based IR - SSA virtual registers, maps to JVM bytecode

            Example:
              java -jar jvmbasic.jar -o HelloWorld hello.jvmb
              java -jar jvmbasic.jar -tree -parse-only examples/hello.jvmb
              java -jar jvmbasic.jar -ir -sir -parse-only examples/hello.jvmb
              java -jar jvmbasic.jar --output-all -parse-only examples/hello.jvmb
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
                .replaceFirst("\\.(bas|jvmb)$", "");
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
            IRBuilder irBuilder = new IRBuilder(outputName);
            irUnit = irBuilder.build(tree);
            String irContent = irUnit.toString();

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

        // Collect symbols (always needed for visitor)
        SymbolCollector symbolCollector = new SymbolCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(symbolCollector, tree);

        // Pass 2: Use visitor to generate code
        if (debugMode) {
            System.out.println("\n=== Pass 2: Code Generation (Visitor) ===");
        }

        CompilerVisitor visitor = new CompilerVisitor(outputName, symbolCollector.getSymbols());
        visitor.visit(tree);

        // Write main class file
        byte[] bytecode = visitor.getBytecode();
        Files.write(Path.of(outputName + ".class"), bytecode);

        System.out.println("Successfully compiled: " + outputName + ".class (" + bytecode.length + " bytes)");

        // Write generated class files (user-defined classes)
        for (var entry : visitor.getGeneratedClasses().entrySet()) {
            String className = entry.getKey();
            byte[] classBytecode = entry.getValue();
            Files.write(Path.of(className + ".class"), classBytecode);
            System.out.println("Generated class: " + className + ".class (" + classBytecode.length + " bytes)");
        }
    }

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
