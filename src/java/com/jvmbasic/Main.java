package com.jvmbasic;

import com.jvmbasic.grammar.*;
import com.jvmbasic.ir.*;
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
    private static boolean parseOnly = false;
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
              -o <name>     Output class name (default: derived from source file)
              -d            Enable debug output (uses listener for trace)
              -ast          Print AST structure (compact, single line)
              -tree         Print parse tree (pretty-printed, indented)
              -ir           Print intermediate representation
              -tokens       Print token stream
              -parse-only   Parse without code generation
              -help         Show this help

            Example:
              java -jar jvmbasic.jar -o HelloWorld hello.jvmb
              java -jar jvmbasic.jar -tree -parse-only examples/hello.jvmb
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
                .replaceFirst("\\.bas$", "");
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
        if (showAst) {
            System.out.println("\nParse Tree (compact):");
            System.out.println(tree.toStringTree(parser));
        }

        // Show pretty-printed tree if requested
        if (showTree) {
            System.out.println("\nParse Tree (pretty):");
            printTree(tree, parser, 0);
        }

        // Build IR if requested or needed
        if (showIr || !parseOnly) {
            System.out.println("\n=== Building IR ===");
            IRBuilder irBuilder = new IRBuilder(outputName);
            IRCompilationUnit irUnit = irBuilder.build(tree);

            if (showIr) {
                System.out.println("\nIntermediate Representation:");
                System.out.println(irUnit.toString());
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

        // Write class file
        byte[] bytecode = visitor.getBytecode();
        Files.write(Path.of(outputName + ".class"), bytecode);

        System.out.println("Successfully compiled: " + outputName + ".class (" + bytecode.length + " bytes)");
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
     * Pretty-print a parse tree with indentation
     */
    private static void printTree(ParseTree tree, Parser parser, int indent) {
        String prefix = "  ".repeat(indent);

        if (tree instanceof RuleContext ctx) {
            String ruleName = parser.getRuleNames()[ctx.getRuleIndex()];
            System.out.println(prefix + ruleName);
            for (int i = 0; i < tree.getChildCount(); i++) {
                printTree(tree.getChild(i), parser, indent + 1);
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
            System.out.println(prefix + tokenName + ": '" + token.getText() + "' (line " + token.getLine() + ")");
        }
    }
}
