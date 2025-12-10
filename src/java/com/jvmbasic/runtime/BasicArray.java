package com.jvmbasic.runtime;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * BasicArray - Runtime support for array operations in JVM BASIC 2.0
 *
 * Provides static methods for working with arrays, similar to .NET's Array class
 * and LINQ-style operations.
 */
public class BasicArray {

    // ============================================================
    // Length and Bounds
    // ============================================================

    /**
     * Get the length of a String array
     */
    public static int Length(String[] arr) {
        return arr == null ? 0 : arr.length;
    }

    /**
     * Get the length of an Integer array
     */
    public static int Length(int[] arr) {
        return arr == null ? 0 : arr.length;
    }

    /**
     * Get the length of a Double array
     */
    public static int Length(double[] arr) {
        return arr == null ? 0 : arr.length;
    }

    /**
     * Get the length of a Boolean array
     */
    public static int Length(boolean[] arr) {
        return arr == null ? 0 : arr.length;
    }

    /**
     * Get the length of an Object array
     */
    public static int Length(Object[] arr) {
        return arr == null ? 0 : arr.length;
    }

    // ============================================================
    // IndexOf - Find first occurrence
    // ============================================================

    /**
     * Find the index of a value in a String array
     */
    public static int IndexOf(String[] arr, String value) {
        if (arr == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (value == null ? arr[i] == null : value.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the index of a value in an Integer array
     */
    public static int IndexOf(int[] arr, int value) {
        if (arr == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    /**
     * Find the index of a value in a Double array
     */
    public static int IndexOf(double[] arr, double value) {
        if (arr == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    /**
     * Find the index of a value in an Object array
     */
    public static int IndexOf(Object[] arr, Object value) {
        if (arr == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (value == null ? arr[i] == null : value.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    // ============================================================
    // LastIndexOf - Find last occurrence
    // ============================================================

    /**
     * Find the last index of a value in a String array
     */
    public static int LastIndexOf(String[] arr, String value) {
        if (arr == null) return -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (value == null ? arr[i] == null : value.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the last index of a value in an Integer array
     */
    public static int LastIndexOf(int[] arr, int value) {
        if (arr == null) return -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    /**
     * Find the last index of a value in a Double array
     */
    public static int LastIndexOf(double[] arr, double value) {
        if (arr == null) return -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    /**
     * Find the last index of a value in an Object array
     */
    public static int LastIndexOf(Object[] arr, Object value) {
        if (arr == null) return -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (value == null ? arr[i] == null : value.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    // ============================================================
    // Contains - Check if array contains value
    // ============================================================

    /**
     * Check if a String array contains a value
     */
    public static boolean Contains(String[] arr, String value) {
        return IndexOf(arr, value) >= 0;
    }

    /**
     * Check if an Integer array contains a value
     */
    public static boolean Contains(int[] arr, int value) {
        return IndexOf(arr, value) >= 0;
    }

    /**
     * Check if a Double array contains a value
     */
    public static boolean Contains(double[] arr, double value) {
        return IndexOf(arr, value) >= 0;
    }

    /**
     * Check if an Object array contains a value
     */
    public static boolean Contains(Object[] arr, Object value) {
        return IndexOf(arr, value) >= 0;
    }

    // ============================================================
    // Reverse - Create a reversed copy
    // ============================================================

    /**
     * Return a reversed copy of a String array
     */
    public static String[] Reverse(String[] arr) {
        if (arr == null) return null;
        String[] result = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[arr.length - 1 - i];
        }
        return result;
    }

    /**
     * Return a reversed copy of an Integer array
     */
    public static int[] Reverse(int[] arr) {
        if (arr == null) return null;
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[arr.length - 1 - i];
        }
        return result;
    }

    /**
     * Return a reversed copy of a Double array
     */
    public static double[] Reverse(double[] arr) {
        if (arr == null) return null;
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[arr.length - 1 - i];
        }
        return result;
    }

    /**
     * Return a reversed copy of an Object array
     */
    public static Object[] Reverse(Object[] arr) {
        if (arr == null) return null;
        Object[] result = new Object[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[arr.length - 1 - i];
        }
        return result;
    }

    // ============================================================
    // Sort - Create a sorted copy
    // ============================================================

    /**
     * Return a sorted copy of a String array
     */
    public static String[] Sort(String[] arr) {
        if (arr == null) return null;
        String[] result = Arrays.copyOf(arr, arr.length);
        Arrays.sort(result);
        return result;
    }

    /**
     * Return a sorted copy of an Integer array
     */
    public static int[] Sort(int[] arr) {
        if (arr == null) return null;
        int[] result = Arrays.copyOf(arr, arr.length);
        Arrays.sort(result);
        return result;
    }

    /**
     * Return a sorted copy of a Double array
     */
    public static double[] Sort(double[] arr) {
        if (arr == null) return null;
        double[] result = Arrays.copyOf(arr, arr.length);
        Arrays.sort(result);
        return result;
    }

    // ============================================================
    // Join - Join array elements into a string
    // ============================================================

    /**
     * Join String array elements with a separator
     */
    public static String Join(String[] arr, String separator) {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(separator);
            sb.append(arr[i] != null ? arr[i] : "");
        }
        return sb.toString();
    }

    /**
     * Join Integer array elements with a separator
     */
    public static String Join(int[] arr, String separator) {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(separator);
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * Join Double array elements with a separator
     */
    public static String Join(double[] arr, String separator) {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(separator);
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * Join Object array elements with a separator
     */
    public static String Join(Object[] arr, String separator) {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(separator);
            sb.append(arr[i] != null ? arr[i].toString() : "");
        }
        return sb.toString();
    }

    // ============================================================
    // First/Last - Get first/last element
    // ============================================================

    /**
     * Get the first element of a String array (or null if empty)
     */
    public static String First(String[] arr) {
        return (arr == null || arr.length == 0) ? null : arr[0];
    }

    /**
     * Get the first element of an Integer array (or 0 if empty)
     */
    public static int First(int[] arr) {
        return (arr == null || arr.length == 0) ? 0 : arr[0];
    }

    /**
     * Get the first element of a Double array (or 0.0 if empty)
     */
    public static double First(double[] arr) {
        return (arr == null || arr.length == 0) ? 0.0 : arr[0];
    }

    /**
     * Get the first element of an Object array (or null if empty)
     */
    public static Object First(Object[] arr) {
        return (arr == null || arr.length == 0) ? null : arr[0];
    }

    /**
     * Get the last element of a String array (or null if empty)
     */
    public static String Last(String[] arr) {
        return (arr == null || arr.length == 0) ? null : arr[arr.length - 1];
    }

    /**
     * Get the last element of an Integer array (or 0 if empty)
     */
    public static int Last(int[] arr) {
        return (arr == null || arr.length == 0) ? 0 : arr[arr.length - 1];
    }

    /**
     * Get the last element of a Double array (or 0.0 if empty)
     */
    public static double Last(double[] arr) {
        return (arr == null || arr.length == 0) ? 0.0 : arr[arr.length - 1];
    }

    /**
     * Get the last element of an Object array (or null if empty)
     */
    public static Object Last(Object[] arr) {
        return (arr == null || arr.length == 0) ? null : arr[arr.length - 1];
    }

    // ============================================================
    // Take/Skip - Subarray operations
    // ============================================================

    /**
     * Take the first n elements from a String array
     */
    public static String[] Take(String[] arr, int count) {
        if (arr == null) return null;
        int len = Math.min(count, arr.length);
        if (len <= 0) return new String[0];
        return Arrays.copyOf(arr, len);
    }

    /**
     * Take the first n elements from an Integer array
     */
    public static int[] Take(int[] arr, int count) {
        if (arr == null) return null;
        int len = Math.min(count, arr.length);
        if (len <= 0) return new int[0];
        return Arrays.copyOf(arr, len);
    }

    /**
     * Take the first n elements from a Double array
     */
    public static double[] Take(double[] arr, int count) {
        if (arr == null) return null;
        int len = Math.min(count, arr.length);
        if (len <= 0) return new double[0];
        return Arrays.copyOf(arr, len);
    }

    /**
     * Skip the first n elements of a String array
     */
    public static String[] Skip(String[] arr, int count) {
        if (arr == null) return null;
        if (count >= arr.length) return new String[0];
        if (count <= 0) return Arrays.copyOf(arr, arr.length);
        return Arrays.copyOfRange(arr, count, arr.length);
    }

    /**
     * Skip the first n elements of an Integer array
     */
    public static int[] Skip(int[] arr, int count) {
        if (arr == null) return null;
        if (count >= arr.length) return new int[0];
        if (count <= 0) return Arrays.copyOf(arr, arr.length);
        return Arrays.copyOfRange(arr, count, arr.length);
    }

    /**
     * Skip the first n elements of a Double array
     */
    public static double[] Skip(double[] arr, int count) {
        if (arr == null) return null;
        if (count >= arr.length) return new double[0];
        if (count <= 0) return Arrays.copyOf(arr, arr.length);
        return Arrays.copyOfRange(arr, count, arr.length);
    }

    // ============================================================
    // Sum/Average/Min/Max - Numeric operations
    // ============================================================

    /**
     * Sum of Integer array elements
     */
    public static int Sum(int[] arr) {
        if (arr == null || arr.length == 0) return 0;
        int sum = 0;
        for (int v : arr) sum += v;
        return sum;
    }

    /**
     * Sum of Double array elements
     */
    public static double Sum(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double sum = 0.0;
        for (double v : arr) sum += v;
        return sum;
    }

    /**
     * Average of Integer array elements
     */
    public static double Average(int[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        return (double) Sum(arr) / arr.length;
    }

    /**
     * Average of Double array elements
     */
    public static double Average(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        return Sum(arr) / arr.length;
    }

    /**
     * Minimum value in an Integer array
     */
    public static int Min(int[] arr) {
        if (arr == null || arr.length == 0) return 0;
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
        }
        return min;
    }

    /**
     * Minimum value in a Double array
     */
    public static double Min(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
        }
        return min;
    }

    /**
     * Maximum value in an Integer array
     */
    public static int Max(int[] arr) {
        if (arr == null || arr.length == 0) return 0;
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }

    /**
     * Maximum value in a Double array
     */
    public static double Max(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }

    // ============================================================
    // IsEmpty/Count
    // ============================================================

    /**
     * Check if String array is null or empty
     */
    public static boolean IsEmpty(String[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * Check if Integer array is null or empty
     */
    public static boolean IsEmpty(int[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * Check if Double array is null or empty
     */
    public static boolean IsEmpty(double[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * Check if Object array is null or empty
     */
    public static boolean IsEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    // ============================================================
    // Fill - Create array filled with a value
    // ============================================================

    /**
     * Create a String array filled with a value
     */
    public static String[] Fill(String value, int count) {
        if (count <= 0) return new String[0];
        String[] arr = new String[count];
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Create an Integer array filled with a value
     */
    public static int[] FillInt(int value, int count) {
        if (count <= 0) return new int[0];
        int[] arr = new int[count];
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Create a Double array filled with a value
     */
    public static double[] FillDouble(double value, int count) {
        if (count <= 0) return new double[0];
        double[] arr = new double[count];
        Arrays.fill(arr, value);
        return arr;
    }

    // ============================================================
    // Range - Create array from a range of integers
    // ============================================================

    /**
     * Create an array with integers from start to end (inclusive)
     */
    public static int[] Range(int start, int end) {
        if (end < start) return new int[0];
        int[] arr = new int[end - start + 1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = start + i;
        }
        return arr;
    }

    /**
     * Create an array with integers from start to end with step
     */
    public static int[] Range(int start, int end, int step) {
        if (step == 0) return new int[0];
        if (step > 0 && end < start) return new int[0];
        if (step < 0 && end > start) return new int[0];

        int count = Math.abs((end - start) / step) + 1;
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) {
            arr[i] = start + (i * step);
        }
        return arr;
    }

    // ============================================================
    // Copy/Clone - Create copies
    // ============================================================

    /**
     * Create a copy of a String array
     */
    public static String[] Copy(String[] arr) {
        return arr == null ? null : Arrays.copyOf(arr, arr.length);
    }

    /**
     * Create a copy of an Integer array
     */
    public static int[] Copy(int[] arr) {
        return arr == null ? null : Arrays.copyOf(arr, arr.length);
    }

    /**
     * Create a copy of a Double array
     */
    public static double[] Copy(double[] arr) {
        return arr == null ? null : Arrays.copyOf(arr, arr.length);
    }

    // ============================================================
    // Slice - Get a subarray
    // ============================================================

    /**
     * Get a slice of a String array from start to end (exclusive)
     */
    public static String[] Slice(String[] arr, int start, int end) {
        if (arr == null) return null;
        start = Math.max(0, start);
        end = Math.min(arr.length, end);
        if (start >= end) return new String[0];
        return Arrays.copyOfRange(arr, start, end);
    }

    /**
     * Get a slice of an Integer array from start to end (exclusive)
     */
    public static int[] Slice(int[] arr, int start, int end) {
        if (arr == null) return null;
        start = Math.max(0, start);
        end = Math.min(arr.length, end);
        if (start >= end) return new int[0];
        return Arrays.copyOfRange(arr, start, end);
    }

    /**
     * Get a slice of a Double array from start to end (exclusive)
     */
    public static double[] Slice(double[] arr, int start, int end) {
        if (arr == null) return null;
        start = Math.max(0, start);
        end = Math.min(arr.length, end);
        if (start >= end) return new double[0];
        return Arrays.copyOfRange(arr, start, end);
    }

    // ============================================================
    // Distinct - Remove duplicates
    // ============================================================

    /**
     * Get distinct values from a String array
     */
    public static String[] Distinct(String[] arr) {
        if (arr == null) return null;
        return Arrays.stream(arr).distinct().toArray(String[]::new);
    }

    /**
     * Get distinct values from an Integer array
     */
    public static int[] Distinct(int[] arr) {
        if (arr == null) return null;
        return Arrays.stream(arr).distinct().toArray();
    }

    /**
     * Get distinct values from a Double array
     */
    public static double[] Distinct(double[] arr) {
        if (arr == null) return null;
        return Arrays.stream(arr).distinct().toArray();
    }

    // ============================================================
    // Concat - Concatenate arrays
    // ============================================================

    /**
     * Concatenate two String arrays
     */
    public static String[] Concat(String[] arr1, String[] arr2) {
        if (arr1 == null) return arr2;
        if (arr2 == null) return arr1;
        String[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

    /**
     * Concatenate two Integer arrays
     */
    public static int[] Concat(int[] arr1, int[] arr2) {
        if (arr1 == null) return arr2;
        if (arr2 == null) return arr1;
        int[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

    /**
     * Concatenate two Double arrays
     */
    public static double[] Concat(double[] arr1, double[] arr2) {
        if (arr1 == null) return arr2;
        if (arr2 == null) return arr1;
        double[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }
}
