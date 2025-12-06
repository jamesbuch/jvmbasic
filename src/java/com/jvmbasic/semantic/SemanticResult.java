package com.jvmbasic.semantic;

import java.util.Collections;
import java.util.List;

/**
 * Results of semantic analysis.
 */
public class SemanticResult {

    private final List<SemanticError> errors;
    private final List<SemanticWarning> warnings;

    public SemanticResult(List<SemanticError> errors, List<SemanticWarning> warnings) {
        this.errors = Collections.unmodifiableList(errors);
        this.warnings = Collections.unmodifiableList(warnings);
    }

    public List<SemanticError> getErrors() {
        return errors;
    }

    public List<SemanticWarning> getWarnings() {
        return warnings;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public boolean isSuccess() {
        return !hasErrors();
    }

    public void printReport() {
        if (hasErrors()) {
            System.err.println("\n=== Semantic Errors ===");
            for (SemanticError err : errors) {
                System.err.println("  " + err);
            }
        }
        if (hasWarnings()) {
            System.err.println("\n=== Semantic Warnings ===");
            for (SemanticWarning warn : warnings) {
                System.err.println("  " + warn);
            }
        }
        if (isSuccess()) {
            System.out.println("Semantic analysis: OK (" + warnings.size() + " warnings)");
        } else {
            System.err.println("Semantic analysis failed: " + errors.size() + " errors, " + warnings.size() + " warnings");
        }
    }
}
