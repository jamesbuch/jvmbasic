package com.jvmbasic.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Test runner for JVM BASIC tests.
 *
 * Discovers methods annotated with @Test and executes them,
 * reporting results with colored output.
 *
 * Usage:
 *   TestRunner runner = new TestRunner();
 *   runner.runTests(TestClass.class);
 *   System.exit(runner.hasFailures() ? 1 : 0);
 */
public class TestRunner {
    // ANSI color codes
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD = "\u001B[1m";
    private static final String RESET = "\u001B[0m";

    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;
    private final List<TestResult> results = new ArrayList<>();

    /**
     * Run all tests in the given class.
     */
    public void runTests(Class<?> testClass) {
        System.out.println(BOLD + "Running tests in " + testClass.getSimpleName() + RESET);
        System.out.println();

        for (Method method : testClass.getDeclaredMethods()) {
            Test annotation = method.getAnnotation(Test.class);
            if (annotation != null) {
                runTest(testClass, method, annotation.value());
            }
        }
    }

    /**
     * Run all tests in the given class instance (pre-instantiated).
     */
    public void runTests(Object testInstance) {
        Class<?> testClass = testInstance.getClass();
        System.out.println(BOLD + "Running tests in " + testClass.getSimpleName() + RESET);
        System.out.println();

        for (Method method : testClass.getDeclaredMethods()) {
            Test annotation = method.getAnnotation(Test.class);
            if (annotation != null) {
                runTestOnInstance(testInstance, method, annotation.value());
            }
        }
    }

    private void runTest(Class<?> testClass, Method method, String description) {
        String displayName = description.isEmpty() ? method.getName() : description;

        try {
            // Create new instance for each test
            Object instance = testClass.getDeclaredConstructor().newInstance();
            method.setAccessible(true);
            method.invoke(instance);

            passed++;
            results.add(new TestResult(displayName, true, null));
            System.out.println(GREEN + "  ✓ " + RESET + displayName);

        } catch (InvocationTargetException e) {
            failed++;
            Throwable cause = e.getCause();
            String message = cause != null ? cause.getMessage() : e.getMessage();
            results.add(new TestResult(displayName, false, message));

            System.out.println(RED + "  ✗ " + RESET + displayName);
            if (message != null && !message.isEmpty()) {
                System.out.println("    " + RED + message + RESET);
            }

        } catch (Exception e) {
            failed++;
            results.add(new TestResult(displayName, false, e.getMessage()));
            System.out.println(RED + "  ✗ " + RESET + displayName);
            System.out.println("    " + RED + "Error: " + e.getMessage() + RESET);
        }
    }

    private void runTestOnInstance(Object instance, Method method, String description) {
        String displayName = description.isEmpty() ? method.getName() : description;

        try {
            method.setAccessible(true);
            method.invoke(instance);

            passed++;
            results.add(new TestResult(displayName, true, null));
            System.out.println(GREEN + "  ✓ " + RESET + displayName);

        } catch (InvocationTargetException e) {
            failed++;
            Throwable cause = e.getCause();
            String message = cause != null ? cause.getMessage() : e.getMessage();
            results.add(new TestResult(displayName, false, message));

            System.out.println(RED + "  ✗ " + RESET + displayName);
            if (message != null && !message.isEmpty()) {
                System.out.println("    " + RED + message + RESET);
            }

        } catch (Exception e) {
            failed++;
            results.add(new TestResult(displayName, false, e.getMessage()));
            System.out.println(RED + "  ✗ " + RESET + displayName);
            System.out.println("    " + RED + "Error: " + e.getMessage() + RESET);
        }
    }

    /**
     * Print the test summary.
     */
    public void printSummary() {
        System.out.println();
        System.out.println(BOLD + "Results:" + RESET + " " +
                GREEN + passed + " passed" + RESET + ", " +
                (failed > 0 ? RED : "") + failed + " failed" + RESET +
                (skipped > 0 ? ", " + YELLOW + skipped + " skipped" + RESET : ""));
    }

    /**
     * Check if any tests failed.
     */
    public boolean hasFailures() {
        return failed > 0;
    }

    /**
     * Get total number of tests run.
     */
    public int getTotalTests() {
        return passed + failed + skipped;
    }

    /**
     * Get number of passed tests.
     */
    public int getPassedCount() {
        return passed;
    }

    /**
     * Get number of failed tests.
     */
    public int getFailedCount() {
        return failed;
    }

    /**
     * Get all test results.
     */
    public List<TestResult> getResults() {
        return results;
    }

    /**
     * Represents a single test result.
     */
    public static class TestResult {
        public final String name;
        public final boolean passed;
        public final String errorMessage;

        public TestResult(String name, boolean passed, String errorMessage) {
            this.name = name;
            this.passed = passed;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Main entry point for running tests from command line.
     *
     * Usage: java com.jvmbasic.test.TestRunner <TestClassName>
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: TestRunner <TestClassName>");
            System.exit(1);
        }

        try {
            Class<?> testClass = Class.forName(args[0]);
            TestRunner runner = new TestRunner();
            runner.runTests(testClass);
            runner.printSummary();
            System.exit(runner.hasFailures() ? 1 : 0);
        } catch (ClassNotFoundException e) {
            System.err.println("Test class not found: " + args[0]);
            System.exit(1);
        }
    }
}
