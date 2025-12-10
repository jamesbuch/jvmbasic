package com.jvmbasic.runtime;

import java.util.Arrays;

/**
 * BasicAssert - Assertion helpers for JVM BASIC 2.0 Test Framework
 *
 * Provides assertion methods accessible via Assert.FunctionName() in BASIC code.
 * All methods are static and throw AssertionError on failure.
 *
 * Example usage in BASIC:
 *   Assert.Equal(expected, actual)
 *   Assert.True(condition)
 *   Assert.NotNil(value)
 */
public final class BasicAssert {

    private BasicAssert() {} // Prevent instantiation

    // ========================================================================
    // Equality Assertions
    // ========================================================================

    /**
     * Assert that two values are equal.
     */
    public static void Equal(Object expected, Object actual) {
        if (!objectsEqual(expected, actual)) {
            throw new AssertionError("Expected <" + expected + "> but got <" + actual + ">");
        }
    }

    /**
     * Assert that two values are equal, with custom message.
     */
    public static void Equal(Object expected, Object actual, String message) {
        if (!objectsEqual(expected, actual)) {
            throw new AssertionError(message + " (expected <" + expected + "> but got <" + actual + ">)");
        }
    }

    /**
     * Assert that two values are not equal.
     */
    public static void NotEqual(Object expected, Object actual) {
        if (objectsEqual(expected, actual)) {
            throw new AssertionError("Expected values to be different, but both were <" + actual + ">");
        }
    }

    /**
     * Assert that two values are not equal, with custom message.
     */
    public static void NotEqual(Object expected, Object actual, String message) {
        if (objectsEqual(expected, actual)) {
            throw new AssertionError(message);
        }
    }

    // ========================================================================
    // Boolean Assertions
    // ========================================================================

    /**
     * Assert that a condition is true.
     */
    public static void True(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but got false");
        }
    }

    /**
     * Assert that a condition is true, with custom message.
     */
    public static void True(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a condition is false.
     */
    public static void False(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but got true");
        }
    }

    /**
     * Assert that a condition is false, with custom message.
     */
    public static void False(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    // ========================================================================
    // Null/Nil Assertions
    // ========================================================================

    /**
     * Assert that a value is null/nil.
     */
    public static void Nil(Object value) {
        if (value != null) {
            throw new AssertionError("Expected nil but got <" + value + ">");
        }
    }

    /**
     * Assert that a value is null/nil, with custom message.
     */
    public static void Nil(Object value, String message) {
        if (value != null) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a value is not null/nil.
     */
    public static void NotNil(Object value) {
        if (value == null) {
            throw new AssertionError("Expected a value but got nil");
        }
    }

    /**
     * Assert that a value is not null/nil, with custom message.
     */
    public static void NotNil(Object value, String message) {
        if (value == null) {
            throw new AssertionError(message);
        }
    }

    // ========================================================================
    // String Assertions
    // ========================================================================

    /**
     * Assert that a string contains a substring.
     */
    public static void Contains(String haystack, String needle) {
        if (haystack == null || needle == null || !haystack.contains(needle)) {
            throw new AssertionError("Expected <" + haystack + "> to contain <" + needle + ">");
        }
    }

    /**
     * Assert that a string contains a substring, with custom message.
     */
    public static void Contains(String haystack, String needle, String message) {
        if (haystack == null || needle == null || !haystack.contains(needle)) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a string starts with a prefix.
     */
    public static void StartsWith(String str, String prefix) {
        if (str == null || prefix == null || !str.startsWith(prefix)) {
            throw new AssertionError("Expected <" + str + "> to start with <" + prefix + ">");
        }
    }

    /**
     * Assert that a string starts with a prefix, with custom message.
     */
    public static void StartsWith(String str, String prefix, String message) {
        if (str == null || prefix == null || !str.startsWith(prefix)) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a string ends with a suffix.
     */
    public static void EndsWith(String str, String suffix) {
        if (str == null || suffix == null || !str.endsWith(suffix)) {
            throw new AssertionError("Expected <" + str + "> to end with <" + suffix + ">");
        }
    }

    /**
     * Assert that a string ends with a suffix, with custom message.
     */
    public static void EndsWith(String str, String suffix, String message) {
        if (str == null || suffix == null || !str.endsWith(suffix)) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a string matches a regex pattern.
     */
    public static void Matches(String str, String pattern) {
        if (str == null || pattern == null || !str.matches(pattern)) {
            throw new AssertionError("Expected <" + str + "> to match pattern <" + pattern + ">");
        }
    }

    /**
     * Assert that a string matches a regex pattern, with custom message.
     */
    public static void Matches(String str, String pattern, String message) {
        if (str == null || pattern == null || !str.matches(pattern)) {
            throw new AssertionError(message);
        }
    }

    // ========================================================================
    // Comparison Assertions
    // ========================================================================

    /**
     * Assert that a value is greater than another.
     */
    public static void Greater(double actual, double expected) {
        if (actual <= expected) {
            throw new AssertionError("Expected <" + actual + "> to be greater than <" + expected + ">");
        }
    }

    /**
     * Assert that a value is greater than another, with custom message.
     */
    public static void Greater(double actual, double expected, String message) {
        if (actual <= expected) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a value is greater than or equal to another.
     */
    public static void GreaterOrEqual(double actual, double expected) {
        if (actual < expected) {
            throw new AssertionError("Expected <" + actual + "> to be >= <" + expected + ">");
        }
    }

    /**
     * Assert that a value is greater than or equal to another, with custom message.
     */
    public static void GreaterOrEqual(double actual, double expected, String message) {
        if (actual < expected) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a value is less than another.
     */
    public static void Less(double actual, double expected) {
        if (actual >= expected) {
            throw new AssertionError("Expected <" + actual + "> to be less than <" + expected + ">");
        }
    }

    /**
     * Assert that a value is less than another, with custom message.
     */
    public static void Less(double actual, double expected, String message) {
        if (actual >= expected) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that a value is less than or equal to another.
     */
    public static void LessOrEqual(double actual, double expected) {
        if (actual > expected) {
            throw new AssertionError("Expected <" + actual + "> to be <= <" + expected + ">");
        }
    }

    /**
     * Assert that a value is less than or equal to another, with custom message.
     */
    public static void LessOrEqual(double actual, double expected, String message) {
        if (actual > expected) {
            throw new AssertionError(message);
        }
    }

    // ========================================================================
    // Array Assertions
    // ========================================================================

    /**
     * Assert that an array contains an element.
     */
    public static void ArrayContains(Object[] array, Object element) {
        if (array == null) {
            throw new AssertionError("Array is nil");
        }
        for (Object item : array) {
            if (objectsEqual(item, element)) {
                return;
            }
        }
        throw new AssertionError("Expected array to contain <" + element + ">");
    }

    /**
     * Assert that an array contains an element, with custom message.
     */
    public static void ArrayContains(Object[] array, Object element, String message) {
        if (array == null) {
            throw new AssertionError(message);
        }
        for (Object item : array) {
            if (objectsEqual(item, element)) {
                return;
            }
        }
        throw new AssertionError(message);
    }

    /**
     * Assert that an array has a specific length.
     */
    public static void ArrayLength(Object[] array, int expectedLength) {
        if (array == null) {
            throw new AssertionError("Array is nil");
        }
        if (array.length != expectedLength) {
            throw new AssertionError("Expected array length <" + expectedLength + "> but got <" + array.length + ">");
        }
    }

    /**
     * Assert that an array has a specific length, with custom message.
     */
    public static void ArrayLength(Object[] array, int expectedLength, String message) {
        if (array == null || array.length != expectedLength) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that an array is empty.
     */
    public static void ArrayEmpty(Object[] array) {
        if (array == null) {
            throw new AssertionError("Array is nil");
        }
        if (array.length != 0) {
            throw new AssertionError("Expected empty array but got length <" + array.length + ">");
        }
    }

    /**
     * Assert that an array is not empty.
     */
    public static void ArrayNotEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            throw new AssertionError("Expected non-empty array");
        }
    }

    // ========================================================================
    // Failure Helpers
    // ========================================================================

    /**
     * Unconditionally fail with a message.
     */
    public static void Fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * Unconditionally fail.
     */
    public static void Fail() {
        throw new AssertionError("Test failed");
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private static boolean objectsEqual(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        // Handle array comparisons
        if (a.getClass().isArray() && b.getClass().isArray()) {
            return Arrays.deepEquals(new Object[]{a}, new Object[]{b});
        }

        // Handle numeric comparisons (int vs long vs double)
        if (a instanceof Number && b instanceof Number) {
            return ((Number)a).doubleValue() == ((Number)b).doubleValue();
        }

        return a.equals(b);
    }
}
