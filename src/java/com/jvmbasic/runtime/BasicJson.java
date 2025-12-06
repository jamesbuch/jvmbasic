package com.jvmbasic.runtime;

import java.util.*;
import java.util.regex.*;

/**
 * BasicJson - JSON functions for JVM BASIC 2.0
 *
 * Provides JSON parsing and generation accessible via Json.FunctionName() in BASIC code.
 * Uses a simple recursive descent parser - no external dependencies.
 *
 * Example usage in BASIC:
 *   var json as String = Json.Create()
 *   json = Json.Set(json, "name", "Alice")
 *   json = Json.Set(json, "age", "30")
 *   var name as String = Json.Get(json, "name")
 *   Console.WriteLine(Json.Pretty(json))
 */
public final class BasicJson {

    private BasicJson() {} // Prevent instantiation

    // ========================================================================
    // JSON Creation
    // ========================================================================

    /**
     * Create an empty JSON object
     */
    public static String Create() {
        return "{}";
    }

    /**
     * Create an empty JSON array
     */
    public static String CreateArray() {
        return "[]";
    }

    /**
     * Create a JSON object from key-value pairs
     * Keys and values alternate: key1, value1, key2, value2, ...
     */
    public static String FromPairs(String... pairs) {
        if (pairs.length % 2 != 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) sb.append(",");
            sb.append(quote(pairs[i])).append(":").append(valueToJson(pairs[i + 1]));
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Create a JSON array from values
     */
    public static String FromArray(String... values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(valueToJson(values[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    // ========================================================================
    // JSON Access (Get)
    // ========================================================================

    /**
     * Get a value from JSON by key (for objects) or index (for arrays)
     * Returns empty string if not found
     */
    public static String Get(String json, String key) {
        if (json == null || json.isEmpty()) return "";

        json = json.trim();
        if (json.startsWith("{")) {
            return getFromObject(json, key);
        } else if (json.startsWith("[")) {
            try {
                int index = Integer.parseInt(key);
                return getFromArray(json, index);
            } catch (NumberFormatException e) {
                return "";
            }
        }
        return "";
    }

    /**
     * Get a nested value using dot notation: "user.address.city"
     */
    public static String GetPath(String json, String path) {
        String[] parts = path.split("\\.");
        String current = json;
        for (String part : parts) {
            current = Get(current, part);
            if (current.isEmpty()) return "";
        }
        return current;
    }

    /**
     * Get value as integer
     */
    public static int GetInt(String json, String key) {
        String value = Get(json, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Get value as double
     */
    public static double GetDouble(String json, String key) {
        String value = Get(json, key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Get value as boolean
     */
    public static boolean GetBool(String json, String key) {
        String value = Get(json, key);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * Check if key exists in JSON object
     */
    public static boolean Has(String json, String key) {
        if (json == null || !json.trim().startsWith("{")) return false;
        // Simple check - look for the key
        return json.contains("\"" + key + "\"");
    }

    /**
     * Get array length
     */
    public static int Length(String json) {
        if (json == null) return 0;
        json = json.trim();
        if (!json.startsWith("[")) return 0;

        int count = 0;
        int depth = 0;
        boolean inString = false;
        boolean escape = false;

        for (int i = 1; i < json.length() - 1; i++) {
            char c = json.charAt(i);

            if (escape) {
                escape = false;
                continue;
            }

            if (c == '\\') {
                escape = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                continue;
            }

            if (inString) continue;

            if (c == '[' || c == '{') {
                if (depth == 0 && count == 0) count = 1;
                depth++;
            } else if (c == ']' || c == '}') {
                depth--;
            } else if (c == ',' && depth == 0) {
                count++;
            } else if (!Character.isWhitespace(c) && depth == 0 && count == 0) {
                count = 1;
            }
        }

        return count;
    }

    /**
     * Get all keys from a JSON object
     */
    public static String[] Keys(String json) {
        if (json == null) return new String[0];
        json = json.trim();
        if (!json.startsWith("{")) return new String[0];

        List<String> keys = new ArrayList<>();
        Pattern keyPattern = Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"\\s*:");
        Matcher matcher = keyPattern.matcher(json);

        while (matcher.find()) {
            keys.add(unescape(matcher.group(1)));
        }

        return keys.toArray(new String[0]);
    }

    // ========================================================================
    // JSON Modification (Set)
    // ========================================================================

    /**
     * Set a value in JSON object (creates new JSON string)
     */
    public static String Set(String json, String key, String value) {
        if (json == null || json.trim().isEmpty()) {
            json = "{}";
        }
        json = json.trim();
        if (!json.startsWith("{")) {
            return json;
        }

        // Remove existing key if present
        String result = Remove(json, key);

        // Add new key-value pair
        if (result.equals("{}")) {
            return "{" + quote(key) + ":" + valueToJson(value) + "}";
        } else {
            // Insert before closing brace
            return result.substring(0, result.length() - 1) + "," +
                   quote(key) + ":" + valueToJson(value) + "}";
        }
    }

    /**
     * Set an integer value
     */
    public static String SetInt(String json, String key, int value) {
        return Set(json, key, String.valueOf(value));
    }

    /**
     * Set a double value
     */
    public static String SetDouble(String json, String key, double value) {
        return Set(json, key, String.valueOf(value));
    }

    /**
     * Set a boolean value
     */
    public static String SetBool(String json, String key, boolean value) {
        if (json == null || json.trim().isEmpty()) {
            json = "{}";
        }
        json = json.trim();
        if (!json.startsWith("{")) {
            return json;
        }

        String result = Remove(json, key);
        String boolStr = value ? "true" : "false";

        if (result.equals("{}")) {
            return "{" + quote(key) + ":" + boolStr + "}";
        } else {
            return result.substring(0, result.length() - 1) + "," +
                   quote(key) + ":" + boolStr + "}";
        }
    }

    /**
     * Set a nested JSON object/array value
     */
    public static String SetJson(String json, String key, String jsonValue) {
        if (json == null || json.trim().isEmpty()) {
            json = "{}";
        }
        json = json.trim();
        if (!json.startsWith("{")) {
            return json;
        }

        String result = Remove(json, key);

        if (result.equals("{}")) {
            return "{" + quote(key) + ":" + jsonValue + "}";
        } else {
            return result.substring(0, result.length() - 1) + "," +
                   quote(key) + ":" + jsonValue + "}";
        }
    }

    /**
     * Remove a key from JSON object
     */
    public static String Remove(String json, String key) {
        if (json == null) return "{}";
        json = json.trim();
        if (!json.startsWith("{")) return json;

        // Use regex to remove the key-value pair
        String quotedKey = Pattern.quote("\"" + key + "\"");
        // Match key: value (handling nested objects/arrays)
        String pattern = ",?\\s*" + quotedKey + "\\s*:\\s*(?:\"(?:[^\"\\\\]|\\\\.)*\"|\\d+(?:\\.\\d+)?|true|false|null|\\{[^{}]*\\}|\\[[^\\[\\]]*\\])\\s*,?";

        String result = json.replaceAll(pattern, "");
        // Clean up any double commas or leading/trailing commas
        result = result.replaceAll(",\\s*,", ",");
        result = result.replaceAll("\\{\\s*,", "{");
        result = result.replaceAll(",\\s*\\}", "}");

        return result.isEmpty() ? "{}" : result;
    }

    // ========================================================================
    // Array Operations
    // ========================================================================

    /**
     * Add value to end of JSON array
     */
    public static String Push(String json, String value) {
        if (json == null || json.trim().isEmpty()) {
            json = "[]";
        }
        json = json.trim();
        if (!json.startsWith("[")) {
            return json;
        }

        if (json.equals("[]")) {
            return "[" + valueToJson(value) + "]";
        } else {
            return json.substring(0, json.length() - 1) + "," + valueToJson(value) + "]";
        }
    }

    /**
     * Add JSON value to array (object or array)
     */
    public static String PushJson(String json, String jsonValue) {
        if (json == null || json.trim().isEmpty()) {
            json = "[]";
        }
        json = json.trim();
        if (!json.startsWith("[")) {
            return json;
        }

        if (json.equals("[]")) {
            return "[" + jsonValue + "]";
        } else {
            return json.substring(0, json.length() - 1) + "," + jsonValue + "]";
        }
    }

    // ========================================================================
    // Formatting
    // ========================================================================

    /**
     * Pretty-print JSON with indentation
     */
    public static String Pretty(String json) {
        if (json == null) return "";

        StringBuilder sb = new StringBuilder();
        int indent = 0;
        boolean inString = false;
        boolean escape = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escape) {
                sb.append(c);
                escape = false;
                continue;
            }

            if (c == '\\' && inString) {
                sb.append(c);
                escape = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                sb.append(c);
                continue;
            }

            if (inString) {
                sb.append(c);
                continue;
            }

            switch (c) {
                case '{':
                case '[':
                    sb.append(c);
                    sb.append('\n');
                    indent++;
                    sb.append("  ".repeat(indent));
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    sb.append("  ".repeat(indent));
                    sb.append(c);
                    break;
                case ',':
                    sb.append(c);
                    sb.append('\n');
                    sb.append("  ".repeat(indent));
                    break;
                case ':':
                    sb.append(c);
                    sb.append(' ');
                    break;
                default:
                    if (!Character.isWhitespace(c)) {
                        sb.append(c);
                    }
            }
        }

        return sb.toString();
    }

    /**
     * Minify JSON (remove whitespace)
     */
    public static String Minify(String json) {
        if (json == null) return "";

        StringBuilder sb = new StringBuilder();
        boolean inString = false;
        boolean escape = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escape) {
                sb.append(c);
                escape = false;
                continue;
            }

            if (c == '\\' && inString) {
                sb.append(c);
                escape = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                sb.append(c);
                continue;
            }

            if (inString || !Character.isWhitespace(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    // ========================================================================
    // Validation
    // ========================================================================

    /**
     * Check if string is valid JSON
     */
    public static boolean IsValid(String json) {
        if (json == null || json.trim().isEmpty()) return false;
        json = json.trim();
        try {
            if (json.startsWith("{")) {
                return isValidObject(json);
            } else if (json.startsWith("[")) {
                return isValidArray(json);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if JSON is an object
     */
    public static boolean IsObject(String json) {
        return json != null && json.trim().startsWith("{");
    }

    /**
     * Check if JSON is an array
     */
    public static boolean IsArray(String json) {
        return json != null && json.trim().startsWith("[");
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private static String quote(String s) {
        return "\"" + escape(s) + "\"";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private static String valueToJson(String value) {
        if (value == null) return "null";

        // Check if it's a number
        if (value.matches("-?\\d+(\\.\\d+)?")) {
            return value;
        }

        // Check if it's a boolean
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return value.toLowerCase();
        }

        // Check if it's null
        if (value.equalsIgnoreCase("null")) {
            return "null";
        }

        // Otherwise, treat as string
        return quote(value);
    }

    private static String getFromObject(String json, String key) {
        // Find the key
        String quotedKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(quotedKey);
        if (keyIndex < 0) return "";

        // Find the colon after the key
        int colonIndex = json.indexOf(':', keyIndex + quotedKey.length());
        if (colonIndex < 0) return "";

        // Parse the value starting after the colon
        return parseValue(json, colonIndex + 1);
    }

    private static String getFromArray(String json, int index) {
        int currentIndex = 0;
        int depth = 0;
        int valueStart = 1; // After opening bracket
        boolean inString = false;
        boolean escape = false;

        for (int i = 1; i < json.length() - 1; i++) {
            char c = json.charAt(i);

            if (escape) {
                escape = false;
                continue;
            }

            if (c == '\\') {
                escape = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                continue;
            }

            if (inString) continue;

            if (c == '[' || c == '{') {
                depth++;
            } else if (c == ']' || c == '}') {
                depth--;
            } else if (c == ',' && depth == 0) {
                if (currentIndex == index) {
                    return parseValue(json, valueStart);
                }
                currentIndex++;
                valueStart = i + 1;
            }
        }

        // Check last element
        if (currentIndex == index) {
            return parseValue(json, valueStart);
        }

        return "";
    }

    private static String parseValue(String json, int start) {
        // Skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()) return "";

        char c = json.charAt(start);

        // String value
        if (c == '"') {
            int end = start + 1;
            boolean escape = false;
            while (end < json.length()) {
                char ec = json.charAt(end);
                if (escape) {
                    escape = false;
                    end++;
                    continue;
                }
                if (ec == '\\') {
                    escape = true;
                    end++;
                    continue;
                }
                if (ec == '"') {
                    return unescape(json.substring(start + 1, end));
                }
                end++;
            }
            return "";
        }

        // Object or array
        if (c == '{' || c == '[') {
            char closeChar = c == '{' ? '}' : ']';
            int depth = 1;
            int end = start + 1;
            boolean inStr = false;
            boolean esc = false;

            while (end < json.length() && depth > 0) {
                char ec = json.charAt(end);
                if (esc) {
                    esc = false;
                    end++;
                    continue;
                }
                if (ec == '\\' && inStr) {
                    esc = true;
                    end++;
                    continue;
                }
                if (ec == '"') {
                    inStr = !inStr;
                } else if (!inStr) {
                    if (ec == c) depth++;
                    else if (ec == closeChar) depth--;
                }
                end++;
            }
            return json.substring(start, end);
        }

        // Number, boolean, or null
        int end = start;
        while (end < json.length()) {
            char ec = json.charAt(end);
            if (ec == ',' || ec == '}' || ec == ']' || Character.isWhitespace(ec)) {
                break;
            }
            end++;
        }
        return json.substring(start, end);
    }

    private static boolean isValidObject(String json) {
        // Basic validation - check matching braces
        int depth = 0;
        boolean inString = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (!inString) {
                if (c == '{') depth++;
                else if (c == '}') depth--;
            }
        }
        return depth == 0;
    }

    private static boolean isValidArray(String json) {
        int depth = 0;
        boolean inString = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (!inString) {
                if (c == '[') depth++;
                else if (c == ']') depth--;
            }
        }
        return depth == 0;
    }
}
