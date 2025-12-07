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

    /**
     * Extract a substring starting at 'start' with the given 'length'.
     * Uses BASIC semantics: (start, length) not Java's (start, end).
     */
    public static String Substring(String s, int start, int length) {
        if (s == null) return null;
        if (start < 0) start = 0;
        if (start >= s.length()) return "";
        if (length <= 0) return "";
        int end = Math.min(start + length, s.length());
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

    // ========================================================================
    // Additional String Operations
    // ========================================================================

    // Count occurrences of substring in string
    public static int CountOccurrences(String s, String search) {
        if (s == null || search == null || search.isEmpty()) return 0;
        int count = 0;
        int idx = 0;
        while ((idx = s.indexOf(search, idx)) != -1) {
            count++;
            idx += search.length();
        }
        return count;
    }

    // Pad with zeros on left (for formatting numbers)
    public static String PadZero(String s, int width) {
        if (s == null) return null;
        if (s.length() >= width) return s;
        boolean negative = s.startsWith("-");
        if (negative) {
            return "-" + "0".repeat(width - s.length()) + s.substring(1);
        }
        return "0".repeat(width - s.length()) + s;
    }

    // Swap case of all characters
    public static String SwapCase(String s) {
        if (s == null) return null;
        StringBuilder result = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // ========================================================================
    // Character Type Checks
    // ========================================================================

    // Check if all characters are letters
    public static boolean IsAlpha(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    // Check if all characters are digits
    public static boolean IsDigit(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    // Check if all characters are alphanumeric
    public static boolean IsAlphanumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }

    // Check if all characters are whitespace
    public static boolean IsWhitespace(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isWhitespace(c)) return false;
        }
        return true;
    }

    // Check if all letters are uppercase
    public static boolean IsUpperCase(String s) {
        if (s == null || s.isEmpty()) return false;
        boolean hasLetter = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
                if (!Character.isUpperCase(c)) return false;
            }
        }
        return hasLetter;
    }

    // Check if all letters are lowercase
    public static boolean IsLowerCase(String s) {
        if (s == null || s.isEmpty()) return false;
        boolean hasLetter = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
                if (!Character.isLowerCase(c)) return false;
            }
        }
        return hasLetter;
    }

    // Check if string represents a valid number
    public static boolean IsNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Double.parseDouble(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ========================================================================
    // Split Variants
    // ========================================================================

    // Split at first occurrence, returns [before, separator, after]
    public static String[] SplitFirst(String s, String sep) {
        if (s == null) return new String[]{"", "", ""};
        int idx = s.indexOf(sep);
        if (idx == -1) {
            return new String[]{s, "", ""};
        }
        return new String[]{
            s.substring(0, idx),
            sep,
            s.substring(idx + sep.length())
        };
    }

    // Split at last occurrence, returns [before, separator, after]
    public static String[] SplitLast(String s, String sep) {
        if (s == null) return new String[]{"", "", ""};
        int idx = s.lastIndexOf(sep);
        if (idx == -1) {
            return new String[]{"", "", s};
        }
        return new String[]{
            s.substring(0, idx),
            sep,
            s.substring(idx + sep.length())
        };
    }

    // Split into lines
    public static String[] SplitLines(String s) {
        if (s == null) return new String[0];
        return s.split("\\r?\\n");
    }

    // ========================================================================
    // Prefix/Suffix Operations
    // ========================================================================

    // Remove prefix if present
    public static String RemovePrefix(String s, String prefix) {
        if (s == null || prefix == null) return s;
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

    // Remove suffix if present
    public static String RemoveSuffix(String s, String suffix) {
        if (s == null || suffix == null) return s;
        if (s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    // Ensure string starts with prefix
    public static String EnsurePrefix(String s, String prefix) {
        if (s == null) return prefix;
        if (s.startsWith(prefix)) return s;
        return prefix + s;
    }

    // Ensure string ends with suffix
    public static String EnsureSuffix(String s, String suffix) {
        if (s == null) return suffix;
        if (s.endsWith(suffix)) return s;
        return s + suffix;
    }

    // ========================================================================
    // Word Operations
    // ========================================================================

    // Capitalize first character only (rest unchanged)
    public static String CapitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // Lowercase first character only (rest unchanged)
    public static String LowercaseFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    // Count number of words
    public static int WordCount(String s) {
        if (s == null || s.isBlank()) return 0;
        return s.trim().split("\\s+").length;
    }

    // Extract words as array
    public static String[] Words(String s) {
        if (s == null || s.isBlank()) return new String[0];
        return s.trim().split("\\s+");
    }

    // Shuffle characters randomly
    public static String Shuffle(String s) {
        if (s == null) return null;
        java.util.List<Character> chars = new java.util.ArrayList<>();
        for (char c : s.toCharArray()) {
            chars.add(c);
        }
        java.util.Collections.shuffle(chars);
        StringBuilder result = new StringBuilder();
        for (char c : chars) {
            result.append(c);
        }
        return result.toString();
    }

    // ========================================================================
    // Text Wrapping
    // ========================================================================

    // Wrap text to specified width
    public static String Wrap(String s, int width) {
        return Wrap(s, width, "\n");
    }

    public static String Wrap(String s, int width, String lineBreak) {
        if (s == null || width <= 0) return s;
        StringBuilder result = new StringBuilder();
        int lineLength = 0;
        String[] words = s.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (lineLength + word.length() + (lineLength > 0 ? 1 : 0) > width) {
                if (lineLength > 0) {
                    result.append(lineBreak);
                    lineLength = 0;
                }
            } else if (lineLength > 0) {
                result.append(" ");
                lineLength++;
            }
            result.append(word);
            lineLength += word.length();
        }
        return result.toString();
    }

    // Insert separator every N characters
    public static String InsertEvery(String s, int interval, String separator) {
        if (s == null || interval <= 0) return s;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i += interval) {
            int endIdx = Math.min(i + interval, s.length());
            result.append(s.substring(i, endIdx));
            if (endIdx < s.length()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    // ========================================================================
    // HTML/Web Utilities
    // ========================================================================

    // Convert newlines to HTML line breaks
    public static String NewlinesToBreaks(String s) {
        if (s == null) return null;
        return s.replace("\r\n", "<br>").replace("\n", "<br>").replace("\r", "<br>");
    }

    // Escape HTML special characters
    public static String EscapeHtml(String s) {
        if (s == null) return null;
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#039;");
    }

    // Unescape HTML entities
    public static String UnescapeHtml(String s) {
        if (s == null) return null;
        return s.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#039;", "'");
    }

    // Escape string for use in JSON
    public static String EscapeJson(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Create URL-friendly slug
    public static String Slugify(String s) {
        if (s == null) return null;
        return s.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    // ========================================================================
    // Whitespace Operations
    // ========================================================================

    // Remove all whitespace
    public static String RemoveWhitespace(String s) {
        if (s == null) return null;
        return s.replaceAll("\\s+", "");
    }

    // Collapse multiple spaces to single space
    public static String CollapseSpaces(String s) {
        if (s == null) return null;
        return s.replaceAll(" +", " ");
    }

    // Normalize whitespace (trim + collapse)
    public static String NormalizeWhitespace(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("\\s+", " ");
    }

    // Expand tabs to spaces
    public static String ExpandTabs(String s, int tabSize) {
        if (s == null) return null;
        return s.replace("\t", " ".repeat(tabSize));
    }

    public static String ExpandTabs(String s) {
        return ExpandTabs(s, 4);
    }

    // ========================================================================
    // Truncation
    // ========================================================================

    // Truncate with ellipsis
    public static String Truncate(String s, int maxLength) {
        return Truncate(s, maxLength, "...");
    }

    public static String Truncate(String s, int maxLength, String suffix) {
        if (s == null) return null;
        if (s.length() <= maxLength) return s;
        if (maxLength <= suffix.length()) return suffix.substring(0, maxLength);
        return s.substring(0, maxLength - suffix.length()) + suffix;
    }

    // ========================================================================
    // Extraction/Filtering
    // ========================================================================

    // Extract only digits
    public static String OnlyDigits(String s) {
        if (s == null) return null;
        return s.replaceAll("[^0-9]", "");
    }

    // Extract only letters
    public static String OnlyLetters(String s) {
        if (s == null) return null;
        return s.replaceAll("[^a-zA-Z]", "");
    }

    // Extract only alphanumeric
    public static String OnlyAlphanumeric(String s) {
        if (s == null) return null;
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    // ========================================================================
    // Case Conversion Utilities
    // ========================================================================

    // CamelCase to snake_case
    public static String ToSnakeCase(String s) {
        if (s == null) return null;
        return s.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    // CamelCase to kebab-case
    public static String ToKebabCase(String s) {
        if (s == null) return null;
        return s.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    // snake_case or kebab-case to camelCase
    public static String ToCamelCase(String s) {
        if (s == null || s.isEmpty()) return s;
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : s.toCharArray()) {
            if (c == '_' || c == '-') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // snake_case or kebab-case to PascalCase
    public static String ToPascalCase(String s) {
        String camel = ToCamelCase(s);
        if (camel == null || camel.isEmpty()) return camel;
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }
}
