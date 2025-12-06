package com.jvmbasic.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BasicStr - String functions for JVM BASIC 2.0
 *
 * Provides string manipulation functions accessible via Str.FunctionName() in BASIC code.
 * All methods are static for easy bytecode generation.
 *
 * Example usage in BASIC:
 *   var upper as String = Str.ToUpper("hello")
 *   var length as Integer = Str.Length("hello world")
 */
public final class BasicStr {

    private BasicStr() {} // Prevent instantiation

    // ========================================================================
    // Basic Properties
    // ========================================================================

    public static int Length(String s) {
        return s == null ? 0 : s.length();
    }

    public static boolean IsEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean IsBlank(String s) {
        return s == null || s.isBlank();
    }

    // ========================================================================
    // Case Conversion
    // ========================================================================

    public static String ToUpper(String s) {
        return s == null ? null : s.toUpperCase();
    }

    public static String ToLower(String s) {
        return s == null ? null : s.toLowerCase();
    }

    public static String Capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static String Title(String s) {
        if (s == null || s.isEmpty()) return s;
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    // ========================================================================
    // Trimming
    // ========================================================================

    public static String Trim(String s) {
        return s == null ? null : s.trim();
    }

    public static String TrimLeft(String s) {
        return s == null ? null : s.stripLeading();
    }

    public static String TrimRight(String s) {
        return s == null ? null : s.stripTrailing();
    }

    // ========================================================================
    // Substring Operations
    // ========================================================================

    public static String Left(String s, int length) {
        if (s == null) return null;
        if (length <= 0) return "";
        if (length >= s.length()) return s;
        return s.substring(0, length);
    }

    public static String Right(String s, int length) {
        if (s == null) return null;
        if (length <= 0) return "";
        if (length >= s.length()) return s;
        return s.substring(s.length() - length);
    }

    public static String Mid(String s, int start, int length) {
        if (s == null) return null;
        if (start < 0) start = 0;
        if (start >= s.length()) return "";
        int end = Math.min(start + length, s.length());
        return s.substring(start, end);
    }

    public static String Substring(String s, int start) {
        if (s == null) return null;
        if (start < 0) start = 0;
        if (start >= s.length()) return "";
        return s.substring(start);
    }

    public static String Substring(String s, int start, int end) {
        if (s == null) return null;
        if (start < 0) start = 0;
        if (end > s.length()) end = s.length();
        if (start >= end) return "";
        return s.substring(start, end);
    }

    // ========================================================================
    // Search Operations
    // ========================================================================

    public static int IndexOf(String s, String search) {
        if (s == null || search == null) return -1;
        return s.indexOf(search);
    }

    public static int IndexOf(String s, String search, int startIndex) {
        if (s == null || search == null) return -1;
        return s.indexOf(search, startIndex);
    }

    public static int LastIndexOf(String s, String search) {
        if (s == null || search == null) return -1;
        return s.lastIndexOf(search);
    }

    public static boolean Contains(String s, String search) {
        if (s == null || search == null) return false;
        return s.contains(search);
    }

    public static boolean StartsWith(String s, String prefix) {
        if (s == null || prefix == null) return false;
        return s.startsWith(prefix);
    }

    public static boolean EndsWith(String s, String suffix) {
        if (s == null || suffix == null) return false;
        return s.endsWith(suffix);
    }

    // ========================================================================
    // Transformation
    // ========================================================================

    public static String Replace(String s, String oldValue, String newValue) {
        if (s == null) return null;
        return s.replace(oldValue, newValue);
    }

    public static String ReplaceFirst(String s, String regex, String replacement) {
        if (s == null) return null;
        return s.replaceFirst(regex, replacement);
    }

    public static String ReplaceAll(String s, String regex, String replacement) {
        if (s == null) return null;
        return s.replaceAll(regex, replacement);
    }

    public static String Reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }

    public static String Repeat(String s, int count) {
        if (s == null || count <= 0) return "";
        return s.repeat(count);
    }

    // ========================================================================
    // Padding
    // ========================================================================

    public static String PadLeft(String s, int totalWidth) {
        return PadLeft(s, totalWidth, ' ');
    }

    public static String PadLeft(String s, int totalWidth, char paddingChar) {
        if (s == null) return null;
        if (s.length() >= totalWidth) return s;
        return String.valueOf(paddingChar).repeat(totalWidth - s.length()) + s;
    }

    public static String PadRight(String s, int totalWidth) {
        return PadRight(s, totalWidth, ' ');
    }

    public static String PadRight(String s, int totalWidth, char paddingChar) {
        if (s == null) return null;
        if (s.length() >= totalWidth) return s;
        return s + String.valueOf(paddingChar).repeat(totalWidth - s.length());
    }

    public static String Center(String s, int totalWidth) {
        return Center(s, totalWidth, ' ');
    }

    public static String Center(String s, int totalWidth, char paddingChar) {
        if (s == null) return null;
        if (s.length() >= totalWidth) return s;
        int padding = totalWidth - s.length();
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;
        return String.valueOf(paddingChar).repeat(leftPad) + s + String.valueOf(paddingChar).repeat(rightPad);
    }

    // ========================================================================
    // Split and Join
    // ========================================================================

    public static String[] Split(String s, String delimiter) {
        if (s == null) return new String[0];
        return s.split(Pattern.quote(delimiter));
    }

    public static String[] SplitRegex(String s, String regex) {
        if (s == null) return new String[0];
        return s.split(regex);
    }

    public static String Join(String[] parts, String delimiter) {
        if (parts == null || parts.length == 0) return "";
        return String.join(delimiter, parts);
    }

    // ========================================================================
    // Character Operations
    // ========================================================================

    public static char CharAt(String s, int index) {
        if (s == null || index < 0 || index >= s.length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return s.charAt(index);
    }

    public static int Asc(String s) {
        if (s == null || s.isEmpty()) return 0;
        return s.charAt(0);
    }

    public static String Chr(int code) {
        return String.valueOf((char) code);
    }

    // ========================================================================
    // Comparison
    // ========================================================================

    public static int Compare(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }

    public static int CompareIgnoreCase(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareToIgnoreCase(b);
    }

    public static boolean Equals(String a, String b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    public static boolean EqualsIgnoreCase(String a, String b) {
        if (a == null) return b == null;
        return a.equalsIgnoreCase(b);
    }

    // ========================================================================
    // Regex Operations
    // ========================================================================

    public static boolean Matches(String s, String regex) {
        if (s == null || regex == null) return false;
        return s.matches(regex);
    }

    public static String[] Match(String s, String regex) {
        if (s == null || regex == null) return new String[0];
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String[] groups = new String[matcher.groupCount() + 1];
            for (int i = 0; i <= matcher.groupCount(); i++) {
                groups[i] = matcher.group(i);
            }
            return groups;
        }
        return new String[0];
    }

    // ========================================================================
    // Formatting and Conversion
    // ========================================================================

    public static String Format(String format, Object... args) {
        return String.format(format, args);
    }

    public static int ToInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long ToLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static double ToDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static boolean ToBoolean(String s) {
        if (s == null) return false;
        return Boolean.parseBoolean(s.trim());
    }

    public static String FromInt(int value) {
        return String.valueOf(value);
    }

    public static String FromLong(long value) {
        return String.valueOf(value);
    }

    public static String FromDouble(double value) {
        return String.valueOf(value);
    }

    public static String FromBoolean(boolean value) {
        return String.valueOf(value);
    }
}
