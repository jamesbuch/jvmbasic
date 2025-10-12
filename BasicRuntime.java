package basicrt;

/**
 * Runtime support library for JVM BASIC
 * Provides built-in math and string functions
 */
public class BasicRuntime {
    
    // ===== MATH FUNCTIONS =====
    
    public static int abs_i(int x) {
        return Math.abs(x);
    }
    
    public static float abs_f(float x) {
        return Math.abs(x);
    }
    
    public static float sqr(float x) {
        return (float) Math.sqrt(x);
    }
    
    public static int int_f(float x) {
        return (int) Math.floor(x);
    }
    
    public static int sgn_i(int x) {
        return Integer.compare(x, 0);
    }
    
    public static int sgn_f(float x) {
        return Float.compare(x, 0.0f);
    }
    
    public static float rnd() {
        return (float) Math.random();
    }
    
    public static int rnd_i(int n) {
        if (n <= 0) return 0;
        return (int) (Math.random() * n);
    }

    public static int rnd_i_ranged(int min, int max) {
        java.util.Random random = new java.util.Random();
        return random.nextInt(max - min + 1) + min;
    }
    
    // Trigonometry (radians)
    public static float sin(float x) {
        return (float) Math.sin(x);
    }
    
    public static float cos(float x) {
        return (float) Math.cos(x);
    }
    
    public static float tan(float x) {
        return (float) Math.tan(x);
    }
    
    public static float asin(float x) {
        return (float) Math.asin(x);
    }
    
    public static float acos(float x) {
        return (float) Math.acos(x);
    }
    
    public static float atan(float x) {
        return (float) Math.atan(x);
    }
    
    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x);
    }
    
    // Powers and logarithms
    public static float pow(float x, float y) {
        return (float) Math.pow(x, y);
    }
    
    public static float exp(float x) {
        return (float) Math.exp(x);
    }
    
    public static float log(float x) {
        return (float) Math.log(x);
    }
    
    public static float log10(float x) {
        return (float) Math.log10(x);
    }
    
    // Rounding
    public static int round(float x) {
        return Math.round(x);
    }
    
    public static float ceil(float x) {
        return (float) Math.ceil(x);
    }
    
    public static float floor(float x) {
        return (float) Math.floor(x);
    }
    
    // Constants
    public static float pi() {
        return (float) Math.PI;
    }
    
    public static float e() {
        return (float) Math.E;
    }
    
    // Min/Max
    public static int min_ii(int a, int b) {
        return Math.min(a, b);
    }
    
    public static float min_ff(float a, float b) {
        return Math.min(a, b);
    }
    
    public static int max_ii(int a, int b) {
        return Math.max(a, b);
    }
    
    public static float max_ff(float a, float b) {
        return Math.max(a, b);
    }
    
    // ===== STRING FUNCTIONS =====
    
    public static int len(String s) {
        if (s == null) return 0;
        return s.length();
    }
    
    public static String left(String s, int n) {
        if (s == null) return "";
        if (n < 0) n = 0;
        if (n >= s.length()) return s;
        return s.substring(0, n);
    }
    
    public static String right(String s, int n) {
        if (s == null) return "";
        if (n < 0) n = 0;
        int len = s.length();
        if (n >= len) return s;
        return s.substring(len - n);
    }
    
    public static String mid(String s, int start, int len) {
        if (s == null) return "";
        if (start < 0) start = 0;
        if (len < 0) len = 0;
        if (start >= s.length()) return "";
        int end = Math.min(start + len, s.length());
        return s.substring(start, end);
    }
    
    public static String upper(String s) {
        if (s == null) return "";
        return s.toUpperCase();
    }
    
    public static String lower(String s) {
        if (s == null) return "";
        return s.toLowerCase();
    }
    
    public static String trim(String s) {
        if (s == null) return "";
        return s.trim();
    }
    
    public static String ltrim(String s) {
        if (s == null) return "";
        return s.replaceAll("^\\s+", "");
    }
    
    public static String rtrim(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+$", "");
    }
    
    public static String reverse(String s) {
        if (s == null) return "";
        return new StringBuilder(s).reverse().toString();
    }
    
    // String search
    public static int instr(String haystack, String needle) {
        if (haystack == null || needle == null) return -1;
        return haystack.indexOf(needle);
    }
    
    public static boolean contains(String haystack, String needle) {
        if (haystack == null || needle == null) return false;
        return haystack.contains(needle);
    }
    
    // Character functions
    public static int asc(String s) {
        if (s == null || s.isEmpty()) return 0;
        return (int) s.charAt(0);
    }
    
    public static String chr(int code) {
        if (code < 0 || code > 65535) return "";
        return String.valueOf((char) code);
    }
    
    // Type conversion
    public static String str_i(int n) {
        return String.valueOf(n);
    }
    
    public static String str_f(float n) {
        return String.valueOf(n);
    }
    
    public static String str_b(boolean b) {
        return String.valueOf(b);
    }
    
    public static int val_i(String s) {
        if (s == null) return 0;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static float val_f(String s) {
        if (s == null) return 0.0f;
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }
    
    // String building
    public static String space(int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public static String string(int n, String c) {
        if (n <= 0 || c == null || c.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c.charAt(0));
        }
        return sb.toString();
    }
    
    // Type checking
    public static boolean isnum(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            Float.parseFloat(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isint(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            Integer.parseInt(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // ===== ARRAY ALGORITHMS =====
    
    /**
     * Sort an integer array in-place (ascending order)
     */
    public static void sort_ia(int[] arr) {
        if (arr == null) return;
        java.util.Arrays.sort(arr);
    }
    
    /**
     * Sort a float array in-place (ascending order)
     */
    public static void sort_fa(float[] arr) {
        if (arr == null) return;
        java.util.Arrays.sort(arr);
    }
    
    /**
     * Sort a String array in-place (lexicographic order)
     */
    public static void sort_sa(String[] arr) {
        if (arr == null) return;
        java.util.Arrays.sort(arr);
    }
    
    /**
     * Reverse an integer array in-place
     */
    public static void reverse_ia(int[] arr) {
        if (arr == null) return;
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
    
    /**
     * Reverse a float array in-place
     */
    public static void reverse_fa(float[] arr) {
        if (arr == null) return;
        int left = 0, right = arr.length - 1;
        while (left < right) {
            float temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
    
    /**
     * Reverse a String array in-place
     */
    public static void reverse_sa(String[] arr) {
        if (arr == null) return;
        int left = 0, right = arr.length - 1;
        while (left < right) {
            String temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
    
    /**
     * Find minimum value in integer array
     */
    public static int min_ia(int[] arr) {
        if (arr == null || arr.length == 0) return 0;
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
        }
        return min;
    }
    
    /**
     * Find maximum value in integer array
     */
    public static int max_ia(int[] arr) {
        if (arr == null || arr.length == 0) return 0;
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }
    
    /**
     * Find minimum value in float array
     */
    public static float min_fa(float[] arr) {
        if (arr == null || arr.length == 0) return 0.0f;
        float min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
        }
        return min;
    }
    
    /**
     * Find maximum value in float array
     */
    public static float max_fa(float[] arr) {
        if (arr == null || arr.length == 0) return 0.0f;
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }
    
    /**
     * Sum all values in integer array
     */
    public static int sum_ia(int[] arr) {
        if (arr == null) return 0;
        int sum = 0;
        for (int val : arr) {
            sum += val;
        }
        return sum;
    }
    
    /**
     * Sum all values in float array
     */
    public static float sum_fa(float[] arr) {
        if (arr == null) return 0.0f;
        float sum = 0.0f;
        for (float val : arr) {
            sum += val;
        }
        return sum;
    }
    
    /**
     * Fill an integer array with a value
     */
    public static void fill_ia(int[] arr, int val) {
        if (arr == null) return;
        java.util.Arrays.fill(arr, val);
    }
    
    /**
     * Fill a float array with a value
     */
    public static void fill_fa(float[] arr, float val) {
        if (arr == null) return;
        java.util.Arrays.fill(arr, val);
    }
    
    /**
     * Fill a String array with a value
     */
    public static void fill_sa(String[] arr, String val) {
        if (arr == null) return;
        java.util.Arrays.fill(arr, val);
    }
    
    /**
     * Get array length
     */
    public static int ubound_ia(int[] arr) {
        return arr == null ? 0 : arr.length - 1;
    }
    
    public static int ubound_fa(float[] arr) {
        return arr == null ? 0 : arr.length - 1;
    }
    
    public static int ubound_sa(String[] arr) {
        return arr == null ? 0 : arr.length - 1;
    }
    
    public static int ubound_ba(boolean[] arr) {
        return arr == null ? 0 : arr.length - 1;
    }
    
    // ===== FILE I/O =====
    
    private static java.util.Map<Integer, java.io.BufferedReader> inputFiles = 
        new java.util.HashMap<>();
    private static java.util.Map<Integer, java.io.PrintWriter> outputFiles = 
        new java.util.HashMap<>();
    
    /**
     * Open file for reading
     * Returns file handle (use #1, #2, etc. in BASIC)
     */
    public static int openInput(String filename) {
        try {
            int handle = inputFiles.size() + outputFiles.size() + 1;
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(filename));
            inputFiles.put(handle, reader);
            return handle;
        } catch (java.io.IOException e) {
            return -1;  // Error
        }
    }
    
    /**
     * Open file for writing
     * Returns file handle
     */
    public static int openOutput(String filename) {
        try {
            int handle = inputFiles.size() + outputFiles.size() + 1;
            java.io.PrintWriter writer = new java.io.PrintWriter(
                new java.io.FileWriter(filename));
            outputFiles.put(handle, writer);
            return handle;
        } catch (java.io.IOException e) {
            return -1;  // Error
        }
    }
    
    /**
     * Read line from file
     */
    public static String readLine(int handle) {
        try {
            java.io.BufferedReader reader = inputFiles.get(handle);
            if (reader == null) return "";
            String line = reader.readLine();
            return line != null ? line : "";
        } catch (java.io.IOException e) {
            return "";
        }
    }
    
    /**
     * Write line to file (returns 0 for BASIC compatibility)
     */
    public static int writeLine(int handle, String text) {
        try {
            java.io.PrintWriter writer = outputFiles.get(handle);
            if (writer != null) {
                writer.println(text);
            }
        } catch (Exception e) {
            // Silently fail
        }
        return 0;
    }
    
    /**
     * Write text to file (no newline, returns 0)
     */
    public static int writeText(int handle, String text) {
        try {
            java.io.PrintWriter writer = outputFiles.get(handle);
            if (writer != null) {
                writer.print(text);
            }
        } catch (Exception e) {
            // Silently fail
        }
        return 0;
    }
    
    /**
     * Close file (returns 0 for compatibility with LET assignment)
     */
    public static int closeFile(int handle) {
        try {
            if (inputFiles.containsKey(handle)) {
                inputFiles.get(handle).close();
                inputFiles.remove(handle);
            }
            if (outputFiles.containsKey(handle)) {
                outputFiles.get(handle).flush();
                outputFiles.get(handle).close();
                outputFiles.remove(handle);
            }
        } catch (java.io.IOException e) {
            // Silently fail
        }
        return 0;
    }
    
    /**
     * Check if file exists
     */
    public static boolean fileExists(String filename) {
        return new java.io.File(filename).exists();
    }
    
    /**
     * Delete file
     */
    public static boolean deleteFile(String filename) {
        return new java.io.File(filename).delete();
    }
    
    // ===== REGULAR EXPRESSIONS =====
    
    /**
     * Test if string matches regex pattern
     */
    public static boolean regexMatch(String pattern, String text) {
        if (pattern == null || text == null) return false;
        try {
            return text.matches(pattern);
        } catch (java.util.regex.PatternSyntaxException e) {
            return false;
        }
    }
    
    /**
     * Find first match of pattern in text
     * Returns match or empty string
     */
    public static String regexFind(String pattern, String text) {
        if (pattern == null || text == null) return "";
        try {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                return m.group();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Replace all matches with replacement
     */
    public static String regexReplace(String pattern, String text, String replacement) {
        if (pattern == null || text == null || replacement == null) return text;
        try {
            return text.replaceAll(pattern, replacement);
        } catch (Exception e) {
            return text;
        }
    }
    
    /**
     * Get nth capture group from regex match (0 = whole match)
     */
    public static String regexGroup(String pattern, String text, int groupNum) {
        if (pattern == null || text == null) return "";
        try {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                if (groupNum >= 0 && groupNum <= m.groupCount()) {
                    String result = m.group(groupNum);
                    return result != null ? result : "";
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    // ===== ENHANCED STRING FUNCTIONS =====
    
    /**
     * Split string by delimiter, returns array
     * Note: In BASIC, you'd need to know max size or use dynamic approach
     */
    public static String[] split(String text, String delimiter) {
        if (text == null) return new String[0];
        if (delimiter == null || delimiter.isEmpty()) {
            return new String[]{text};
        }
        return text.split(java.util.regex.Pattern.quote(delimiter));
    }
    
    /**
     * Join string array with delimiter
     */
    public static String join(String[] arr, String delimiter) {
        if (arr == null || arr.length == 0) return "";
        if (delimiter == null) delimiter = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(delimiter);
            sb.append(arr[i]);
        }
        return sb.toString();
    }
    
    /**
     * Format string with one argument (simple sprintf-like)
     */
    public static String format1(String template, String arg) {
        if (template == null) return "";
        return template.replace("{0}", arg != null ? arg : "");
    }
    
    public static String format1_f(String template, float arg) {
        if (template == null) return "";
        return template.replace("{0}", String.valueOf(arg));
    }
    
    public static String format1_i(String template, int arg) {
        if (template == null) return "";
        return template.replace("{0}", String.valueOf(arg));
    }
}


