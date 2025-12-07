package com.jvmbasic.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BasicRegex - Regular expression functions for JVM BASIC 2.0
 *
 * Provides regex operations accessible via Regex.FunctionName() in BASIC code.
 * All methods are static for easy bytecode generation.
 *
 * Example usage in BASIC:
 *   var matched as Boolean = Regex.IsMatch("hello123", "[a-z]+\\d+")
 *   var result as String = Regex.Replace("foo bar", "\\s+", "-")
 */
public final class BasicRegex {

    private BasicRegex() {} // Prevent instantiation

    // ========================================================================
    // Pattern Matching
    // ========================================================================

    /**
     * Check if the entire string matches the pattern
     */
    public static boolean IsMatch(String input, String pattern) {
        if (input == null || pattern == null) return false;
        try {
            return input.matches(pattern);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the pattern is found anywhere in the string
     */
    public static boolean Contains(String input, String pattern) {
        if (input == null || pattern == null) return false;
        try {
            return Pattern.compile(pattern).matcher(input).find();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the string starts with the pattern
     */
    public static boolean StartsWith(String input, String pattern) {
        if (input == null || pattern == null) return false;
        try {
            Matcher m = Pattern.compile(pattern).matcher(input);
            return m.find() && m.start() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the string ends with the pattern
     */
    public static boolean EndsWith(String input, String pattern) {
        if (input == null || pattern == null) return false;
        try {
            Matcher m = Pattern.compile(pattern).matcher(input);
            boolean found = false;
            int endPos = 0;
            while (m.find()) {
                found = true;
                endPos = m.end();
            }
            return found && endPos == input.length();
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // Search and Extract
    // ========================================================================

    /**
     * Find the first match in the string
     * Returns the matched substring, or empty string if not found
     */
    public static String Find(String input, String pattern) {
        if (input == null || pattern == null) return "";
        try {
            Matcher m = Pattern.compile(pattern).matcher(input);
            if (m.find()) {
                return m.group();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Find the position of the first match
     * Returns -1 if not found
     */
    public static int FindIndex(String input, String pattern) {
        if (input == null || pattern == null) return -1;
        try {
            Matcher m = Pattern.compile(pattern).matcher(input);
            if (m.find()) {
                return m.start();
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Find all matches in the string
     * Returns array of matched substrings
     */
    public static String[] FindAll(String input, String pattern) {
        if (input == null || pattern == null) return new String[0];
        try {
            List<String> matches = new ArrayList<>();
            Matcher m = Pattern.compile(pattern).matcher(input);
            while (m.find()) {
                matches.add(m.group());
            }
            return matches.toArray(new String[0]);
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * Count the number of matches in the string
     */
    public static int Count(String input, String pattern) {
        if (input == null || pattern == null) return 0;
        try {
            int count = 0;
            Matcher m = Pattern.compile(pattern).matcher(input);
            while (m.find()) {
                count++;
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    // ========================================================================
    // Capture Groups
    // ========================================================================

    /**
     * Extract the first capture group from the first match
     * Returns empty string if no match or no group
     */
    public static String Group(String input, String pattern, int groupNum) {
        if (input == null || pattern == null) return "";
        try {
            Matcher m = Pattern.compile(pattern).matcher(input);
            if (m.find() && groupNum <= m.groupCount()) {
                String result = m.group(groupNum);
                return result != null ? result : "";
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Extract all capture groups from the first match
     * Returns array where index 0 is full match, 1+ are groups
     */
    public static String[] Groups(String input, String pattern) {
        if (input == null || pattern == null) return new String[0];
        try {
            Matcher m = Pattern.compile(pattern).matcher(input);
            if (m.find()) {
                String[] groups = new String[m.groupCount() + 1];
                for (int i = 0; i <= m.groupCount(); i++) {
                    String group = m.group(i);
                    groups[i] = group != null ? group : "";
                }
                return groups;
            }
            return new String[0];
        } catch (Exception e) {
            return new String[0];
        }
    }

    // ========================================================================
    // Replace Operations
    // ========================================================================

    /**
     * Replace the first occurrence of the pattern
     */
    public static String ReplaceFirst(String input, String pattern, String replacement) {
        if (input == null || pattern == null) return input;
        if (replacement == null) replacement = "";
        try {
            return input.replaceFirst(pattern, replacement);
        } catch (Exception e) {
            return input;
        }
    }

    /**
     * Replace all occurrences of the pattern
     */
    public static String ReplaceAll(String input, String pattern, String replacement) {
        if (input == null || pattern == null) return input;
        if (replacement == null) replacement = "";
        try {
            return input.replaceAll(pattern, replacement);
        } catch (Exception e) {
            return input;
        }
    }

    /**
     * Replace all occurrences of the pattern (alias for ReplaceAll)
     */
    public static String Replace(String input, String pattern, String replacement) {
        return ReplaceAll(input, pattern, replacement);
    }

    /**
     * Remove all occurrences of the pattern (replace with empty string)
     */
    public static String Remove(String input, String pattern) {
        return ReplaceAll(input, pattern, "");
    }

    // ========================================================================
    // Split Operations
    // ========================================================================

    /**
     * Split the string by the pattern
     */
    public static String[] Split(String input, String pattern) {
        if (input == null || pattern == null) return new String[0];
        try {
            return input.split(pattern);
        } catch (Exception e) {
            return new String[]{input};
        }
    }

    /**
     * Split the string by the pattern, limiting the number of parts
     */
    public static String[] Split(String input, String pattern, int limit) {
        if (input == null || pattern == null) return new String[0];
        try {
            return input.split(pattern, limit);
        } catch (Exception e) {
            return new String[]{input};
        }
    }

    // ========================================================================
    // Pattern Utilities
    // ========================================================================

    /**
     * Escape special regex characters in a string
     * Makes the string safe to use as a literal pattern
     */
    public static String Escape(String literal) {
        if (literal == null) return "";
        return Pattern.quote(literal);
    }

    /**
     * Test if a pattern is valid
     */
    public static boolean IsValidPattern(String pattern) {
        if (pattern == null) return false;
        try {
            Pattern.compile(pattern);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // Common Pattern Helpers
    // ========================================================================

    /**
     * Check if string is a valid email address (simple check)
     */
    public static boolean IsEmail(String input) {
        if (input == null || input.isEmpty()) return false;
        return IsMatch(input, "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Check if string is a valid URL (simple check)
     */
    public static boolean IsUrl(String input) {
        if (input == null || input.isEmpty()) return false;
        return IsMatch(input, "^https?://[A-Za-z0-9.-]+(/[A-Za-z0-9._~:/?#\\[\\]@!$&'()*+,;=-]*)?$");
    }

    /**
     * Check if string is a valid IPv4 address
     */
    public static boolean IsIPv4(String input) {
        if (input == null || input.isEmpty()) return false;
        return IsMatch(input, "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");
    }

    /**
     * Check if string contains only digits
     */
    public static boolean IsDigitsOnly(String input) {
        if (input == null || input.isEmpty()) return false;
        return IsMatch(input, "^\\d+$");
    }

    /**
     * Check if string contains only letters
     */
    public static boolean IsLettersOnly(String input) {
        if (input == null || input.isEmpty()) return false;
        return IsMatch(input, "^[A-Za-z]+$");
    }

    /**
     * Check if string contains only alphanumeric characters
     */
    public static boolean IsAlphanumericOnly(String input) {
        if (input == null || input.isEmpty()) return false;
        return IsMatch(input, "^[A-Za-z0-9]+$");
    }

    /**
     * Extract all numbers from a string
     */
    public static String[] ExtractNumbers(String input) {
        return FindAll(input, "-?\\d+\\.?\\d*");
    }

    /**
     * Extract all words from a string
     */
    public static String[] ExtractWords(String input) {
        return FindAll(input, "[A-Za-z]+");
    }
}
