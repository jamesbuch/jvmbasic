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
    
    // ===== PHASE 8: ADVANCED STRING FUNCTIONS =====
    
    /**
     * Replace first occurrence of oldStr with newStr
     */
    public static String replace(String text, String oldStr, String newStr) {
        if (text == null || oldStr == null || newStr == null) return text;
        if (oldStr.isEmpty()) return text;
        int index = text.indexOf(oldStr);
        if (index == -1) return text;
        return text.substring(0, index) + newStr + text.substring(index + oldStr.length());
    }
    
    /**
     * Replace all occurrences of oldStr with newStr
     */
    public static String replaceAll(String text, String oldStr, String newStr) {
        if (text == null || oldStr == null || newStr == null) return text;
        if (oldStr.isEmpty()) return text;
        return text.replace(oldStr, newStr);
    }
    
    /**
     * Check if string starts with prefix
     */
    public static boolean startsWith(String text, String prefix) {
        if (text == null || prefix == null) return false;
        return text.startsWith(prefix);
    }
    
    /**
     * Check if string ends with suffix
     */
    public static boolean endsWith(String text, String suffix) {
        if (text == null || suffix == null) return false;
        return text.endsWith(suffix);
    }
    
    /**
     * Find index of substring (alias for instr, but returns -1 if not found)
     */
    public static int indexOf(String text, String search) {
        if (text == null || search == null) return -1;
        return text.indexOf(search);
    }
    
    /**
     * Find last index of substring
     */
    public static int lastIndexOf(String text, String search) {
        if (text == null || search == null) return -1;
        return text.lastIndexOf(search);
    }
    
    /**
     * Concatenate two strings
     */
    public static String concat(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        return s1 + s2;
    }
    
    /**
     * Concatenate three strings
     */
    public static String concat3(String s1, String s2, String s3) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        if (s3 == null) s3 = "";
        return s1 + s2 + s3;
    }
    
    /**
     * Repeat string n times
     */
    public static String repeat(String text, int count) {
        if (text == null || count <= 0) return "";
        StringBuilder sb = new StringBuilder(text.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(text);
        }
        return sb.toString();
    }
    
    /**
     * Pad string on left to given width with spaces
     */
    public static String padLeft(String text, int width) {
        return padLeft(text, width, ' ');
    }
    
    /**
     * Pad string on left to given width with specified character
     */
    public static String padLeft(String text, int width, char padChar) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        StringBuilder sb = new StringBuilder(width);
        for (int i = text.length(); i < width; i++) {
            sb.append(padChar);
        }
        sb.append(text);
        return sb.toString();
    }
    
    /**
     * Pad string on right to given width with spaces
     */
    public static String padRight(String text, int width) {
        return padRight(text, width, ' ');
    }
    
    /**
     * Pad string on right to given width with specified character
     */
    public static String padRight(String text, int width, char padChar) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        StringBuilder sb = new StringBuilder(width);
        sb.append(text);
        for (int i = text.length(); i < width; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }
    
    /**
     * Substring from start to end of string
     */
    public static String substring(String text, int start) {
        if (text == null) return "";
        if (start < 0) start = 0;
        if (start >= text.length()) return "";
        return text.substring(start);
    }
    
    /**
     * Substring with start and length
     */
    public static String substringLen(String text, int start, int length) {
        if (text == null) return "";
        if (start < 0) start = 0;
        if (length < 0) length = 0;
        if (start >= text.length()) return "";
        int end = Math.min(start + length, text.length());
        return text.substring(start, end);
    }
    
    /**
     * Compare two strings (returns -1, 0, or 1)
     */
    public static int strcmp(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        return Integer.compare(s1.compareTo(s2), 0);
    }
    
    /**
     * Compare two strings case-insensitive (returns -1, 0, or 1)
     */
    public static int stricmp(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        return Integer.compare(s1.compareToIgnoreCase(s2), 0);
    }
    
    /**
     * Check if two strings are equal
     */
    public static boolean equals(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }
    
    /**
     * Check if two strings are equal (case-insensitive)
     */
    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equalsIgnoreCase(s2);
    }
    
    /**
     * Get character at index as string
     */
    public static String charAt(String text, int index) {
        if (text == null || index < 0 || index >= text.length()) return "";
        return String.valueOf(text.charAt(index));
    }
    
    /**
     * Get character at index as ASCII code
     */
    public static int charCodeAt(String text, int index) {
        if (text == null || index < 0 || index >= text.length()) return 0;
        return (int) text.charAt(index);
    }
    
    /**
     * Split string into lines
     */
    public static String[] splitLines(String text) {
        if (text == null) return new String[0];
        return text.split("\\r?\\n");
    }
    
    // ===== PHASE 8: DATE/TIME FUNCTIONS =====
    
    /**
     * Get current time in milliseconds since epoch (as float for larger range)
     */
    public static float now() {
        return (float) System.currentTimeMillis();
    }
    
    /**
     * Get current date as string (YYYY-MM-DD)
     */
    public static String date() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }
    
    /**
     * Get current time as string (HH:mm:ss)
     */
    public static String time() {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    }
    
    /**
     * Get current date and time as string (YYYY-MM-DD HH:mm:ss)
     */
    public static String datetime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }
    
    /**
     * Get year from milliseconds
     */
    public static int year(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.YEAR);
    }
    
    /**
     * Get month from milliseconds (1-12)
     */
    public static int month(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.MONTH) + 1;  // 0-indexed to 1-indexed
    }
    
    /**
     * Get day of month from milliseconds (1-31)
     */
    public static int day(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Get day of week from milliseconds (0=Sunday, 6=Saturday)
     */
    public static int dayOfWeek(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;  // Make 0-indexed
    }
    
    /**
     * Get day of year from milliseconds (1-366)
     */
    public static int dayOfYear(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Get hour from milliseconds (0-23)
     */
    public static int hour(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.HOUR_OF_DAY);
    }
    
    /**
     * Get minute from milliseconds (0-59)
     */
    public static int minute(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.MINUTE);
    }
    
    /**
     * Get second from milliseconds (0-59)
     */
    public static int second(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.SECOND);
    }
    
    /**
     * Get millisecond component (0-999)
     */
    public static int millisecond(float millis) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        return cal.get(java.util.Calendar.MILLISECOND);
    }
    
    /**
     * Add days to a date
     */
    public static float addDays(float millis, int days) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        cal.add(java.util.Calendar.DAY_OF_MONTH, days);
        return (float) cal.getTimeInMillis();
    }
    
    /**
     * Add hours to a date
     */
    public static float addHours(float millis, int hours) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        cal.add(java.util.Calendar.HOUR_OF_DAY, hours);
        return (float) cal.getTimeInMillis();
    }
    
    /**
     * Add minutes to a date
     */
    public static float addMinutes(float millis, int minutes) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        cal.add(java.util.Calendar.MINUTE, minutes);
        return (float) cal.getTimeInMillis();
    }
    
    /**
     * Add seconds to a date
     */
    public static float addSeconds(float millis, int seconds) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        cal.add(java.util.Calendar.SECOND, seconds);
        return (float) cal.getTimeInMillis();
    }
    
    /**
     * Add months to a date
     */
    public static float addMonths(float millis, int months) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        cal.add(java.util.Calendar.MONTH, months);
        return (float) cal.getTimeInMillis();
    }
    
    /**
     * Add years to a date
     */
    public static float addYears(float millis, int years) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis((long) millis);
        cal.add(java.util.Calendar.YEAR, years);
        return (float) cal.getTimeInMillis();
    }
    
    /**
     * Get difference between two dates in days
     */
    public static int dateDiff(float millis1, float millis2) {
        long diff = (long) millis2 - (long) millis1;
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
    
    /**
     * Format date with pattern
     */
    public static String formatDate(float millis, String pattern) {
        try {
            return new java.text.SimpleDateFormat(pattern).format(new java.util.Date((long) millis));
        } catch (Exception e) {
            return "";
        }
    }
    
    // ===== PHASE 8: TIMING FUNCTIONS =====
    
    /**
     * Get timer (seconds since midnight)
     */
    public static float timer() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = cal.get(java.util.Calendar.MINUTE);
        int second = cal.get(java.util.Calendar.SECOND);
        int millis = cal.get(java.util.Calendar.MILLISECOND);
        return hour * 3600.0f + minute * 60.0f + second + millis / 1000.0f;
    }
    
    /**
     * Get high-precision nanosecond timer (as float for BASIC compatibility)
     */
    public static float nanoseconds() {
        return (float) System.nanoTime();
    }
    
    /**
     * Sleep for specified milliseconds
     */
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }
    
    /**
     * Sleep for specified milliseconds (returns 0 for BASIC compatibility)
     */
    public static int sleep_i(int milliseconds) {
        sleep(milliseconds);
        return 0;
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
    
    // ===== PHASE 8: CHARACTER I/O =====
    
    /**
     * Read single character from file (returns ASCII code, -1 on EOF)
     */
    public static int readChar(int handle) {
        try {
            java.io.BufferedReader reader = inputFiles.get(handle);
            if (reader == null) return -1;
            int ch = reader.read();
            return ch;
        } catch (java.io.IOException e) {
            return -1;
        }
    }
    
    /**
     * Write single character to file
     */
    public static void writeChar(int handle, int charCode) {
        try {
            java.io.PrintWriter writer = outputFiles.get(handle);
            if (writer != null) {
                writer.write((char) charCode);
            }
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    /**
     * Write single character to file (returns 0 for BASIC compatibility)
     */
    public static int writeChar_i(int handle, int charCode) {
        writeChar(handle, charCode);
        return 0;
    }
    
    /**
     * Check if file has more data (not EOF)
     */
    public static boolean hasMore(int handle) {
        try {
            java.io.BufferedReader reader = inputFiles.get(handle);
            if (reader == null) return false;
            reader.mark(1);
            int ch = reader.read();
            reader.reset();
            return ch != -1;
        } catch (java.io.IOException e) {
            return false;
        }
    }
    
    /**
     * Check if at end of file
     */
    public static boolean isEof(int handle) {
        return !hasMore(handle);
    }
    
    /**
     * Flush output stream
     */
    public static void flush(int handle) {
        try {
            java.io.PrintWriter writer = outputFiles.get(handle);
            if (writer != null) {
                writer.flush();
            }
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    /**
     * Flush output stream (returns 0 for BASIC compatibility)
     */
    public static int flush_i(int handle) {
        flush(handle);
        return 0;
    }
    
    // ===== PHASE 8: ADVANCED FILE I/O =====
    
    /**
     * Get file size in bytes (as float for large files)
     */
    public static float fileSize(String filename) {
        java.io.File file = new java.io.File(filename);
        return file.exists() ? (float) file.length() : -1.0f;
    }
    
    /**
     * Rename or move file
     */
    public static boolean rename(String oldName, String newName) {
        java.io.File oldFile = new java.io.File(oldName);
        java.io.File newFile = new java.io.File(newName);
        return oldFile.renameTo(newFile);
    }
    
    /**
     * Copy file
     */
    public static boolean copy(String source, String dest) {
        try {
            java.io.FileInputStream in = new java.io.FileInputStream(source);
            java.io.FileOutputStream out = new java.io.FileOutputStream(dest);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }
    
    /**
     * Move file (rename with fallback to copy+delete)
     */
    public static boolean move(String source, String dest) {
        if (rename(source, dest)) {
            return true;
        }
        // Fallback: copy then delete
        if (copy(source, dest)) {
            return deleteFile(source);
        }
        return false;
    }
    
    /**
     * Check if path is a file
     */
    public static boolean isFile(String path) {
        java.io.File file = new java.io.File(path);
        return file.isFile();
    }
    
    /**
     * Check if path is a directory
     */
    public static boolean isDir(String path) {
        java.io.File file = new java.io.File(path);
        return file.isDirectory();
    }
    
    /**
     * Create directory
     */
    public static boolean mkdir(String path) {
        java.io.File file = new java.io.File(path);
        return file.mkdir();
    }
    
    /**
     * Create directory and all parent directories
     */
    public static boolean mkdirs(String path) {
        java.io.File file = new java.io.File(path);
        return file.mkdirs();
    }
    
    /**
     * Remove directory (must be empty)
     */
    public static boolean rmdir(String path) {
        java.io.File file = new java.io.File(path);
        return file.delete();
    }
    
    /**
     * List directory contents
     */
    public static String[] listDir(String path) {
        java.io.File file = new java.io.File(path);
        String[] files = file.list();
        return files != null ? files : new String[0];
    }
    
    /**
     * Get current working directory
     */
    public static String currentDir() {
        return System.getProperty("user.dir");
    }
    
    /**
     * Get absolute path
     */
    public static String absolutePath(String path) {
        java.io.File file = new java.io.File(path);
        try {
            return file.getCanonicalPath();
        } catch (java.io.IOException e) {
            return file.getAbsolutePath();
        }
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
    
    // ===== PHASE 8.2: COLLECTIONS =====
    
    // Storage for collections (using IDs to avoid object type issues in BASIC)
    private static java.util.Map<Integer, java.util.ArrayList<Integer>> intLists = 
        new java.util.HashMap<>();
    private static int nextIntListId = 1;
    
    private static java.util.Map<Integer, java.util.ArrayList<String>> stringLists = 
        new java.util.HashMap<>();
    private static int nextStringListId = 1000000;  // Start at 1M to avoid ID conflicts
    
    private static java.util.Map<Integer, java.util.HashMap<String, String>> maps = 
        new java.util.HashMap<>();
    private static int nextMapId = 2000000;  // Start at 2M
    
    private static java.util.Map<Integer, java.util.Stack<String>> stacks = 
        new java.util.HashMap<>();
    private static int nextStackId = 3000000;  // Start at 3M
    
    private static java.util.Map<Integer, java.util.LinkedList<String>> queues = 
        new java.util.HashMap<>();
    private static int nextQueueId = 4000000;  // Start at 4M
    
    // ===== INT LIST =====
    
    public static int intListNew() {
        int id = nextIntListId++;
        intLists.put(id, new java.util.ArrayList<Integer>());
        return id;
    }
    
    public static int intListAdd(int listId, int value) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null) return -1;
        list.add(value);
        return list.size();
    }
    
    public static int intListGet(int listId, int index) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null || index < 0 || index >= list.size()) return 0;
        return list.get(index);
    }
    
    public static int intListSet(int listId, int index, int value) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null || index < 0 || index >= list.size()) return 0;
        int oldValue = list.get(index);
        list.set(index, value);
        return oldValue;
    }
    
    public static int intListSize(int listId) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        return list == null ? 0 : list.size();
    }
    
    public static int intListRemove(int listId, int index) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null || index < 0 || index >= list.size()) return 0;
        return list.remove(index);
    }
    
    public static boolean intListContains(int listId, int value) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null) return false;
        return list.contains(value);
    }
    
    public static int intListIndexOf(int listId, int value) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null) return -1;
        return list.indexOf(value);
    }
    
    public static int intListClear(int listId) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list != null) {
            list.clear();
        }
        return 0;
    }
    
    public static int[] intListToArray(int listId) {
        java.util.ArrayList<Integer> list = intLists.get(listId);
        if (list == null) return new int[0];
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
    
    // ===== STRING LIST =====
    
    public static int stringListNew() {
        int id = nextStringListId++;
        stringLists.put(id, new java.util.ArrayList<String>());
        return id;
    }
    
    public static int stringListAdd(int listId, String value) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null) return -1;
        list.add(value);
        return list.size();
    }
    
    public static String stringListGet(int listId, int index) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null || index < 0 || index >= list.size()) return "";
        String result = list.get(index);
        return result != null ? result : "";
    }
    
    public static String stringListSet(int listId, int index, String value) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null || index < 0 || index >= list.size()) return "";
        String oldValue = list.get(index);
        list.set(index, value);
        return oldValue != null ? oldValue : "";
    }
    
    public static int stringListSize(int listId) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        return list == null ? 0 : list.size();
    }
    
    public static String stringListRemove(int listId, int index) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null || index < 0 || index >= list.size()) return "";
        String removed = list.remove(index);
        return removed != null ? removed : "";
    }
    
    public static boolean stringListContains(int listId, String value) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null) return false;
        return list.contains(value);
    }
    
    public static int stringListIndexOf(int listId, String value) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null) return -1;
        return list.indexOf(value);
    }
    
    public static int stringListClear(int listId) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list != null) {
            list.clear();
        }
        return 0;
    }
    
    public static String[] stringListToArray(int listId) {
        java.util.ArrayList<String> list = stringLists.get(listId);
        if (list == null) return new String[0];
        return list.toArray(new String[0]);
    }
    
    // ===== MAP (String -> String) =====
    
    public static int mapNew() {
        int id = nextMapId++;
        maps.put(id, new java.util.HashMap<String, String>());
        return id;
    }
    
    public static String mapPut(int mapId, String key, String value) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map == null || key == null) return "";
        String oldValue = map.put(key, value);
        return oldValue != null ? oldValue : "";
    }
    
    public static String mapGet(int mapId, String key) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map == null || key == null) return "";
        String value = map.get(key);
        return value != null ? value : "";
    }
    
    public static boolean mapContainsKey(int mapId, String key) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map == null || key == null) return false;
        return map.containsKey(key);
    }
    
    public static String mapRemove(int mapId, String key) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map == null || key == null) return "";
        String removed = map.remove(key);
        return removed != null ? removed : "";
    }
    
    public static int mapSize(int mapId) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        return map == null ? 0 : map.size();
    }
    
    public static int mapClear(int mapId) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map != null) {
            map.clear();
        }
        return 0;
    }
    
    public static String[] mapKeys(int mapId) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map == null) return new String[0];
        return map.keySet().toArray(new String[0]);
    }
    
    public static String[] mapValues(int mapId) {
        java.util.HashMap<String, String> map = maps.get(mapId);
        if (map == null) return new String[0];
        return map.values().toArray(new String[0]);
    }
    
    // ===== STACK =====
    
    public static int stackNew() {
        int id = nextStackId++;
        stacks.put(id, new java.util.Stack<String>());
        return id;
    }
    
    public static int stackPush(int stackId, String value) {
        java.util.Stack<String> stack = stacks.get(stackId);
        if (stack == null) return -1;
        stack.push(value);
        return stack.size();
    }
    
    public static String stackPop(int stackId) {
        java.util.Stack<String> stack = stacks.get(stackId);
        if (stack == null || stack.isEmpty()) return "";
        return stack.pop();
    }
    
    public static String stackPeek(int stackId) {
        java.util.Stack<String> stack = stacks.get(stackId);
        if (stack == null || stack.isEmpty()) return "";
        return stack.peek();
    }
    
    public static boolean stackIsEmpty(int stackId) {
        java.util.Stack<String> stack = stacks.get(stackId);
        return stack == null || stack.isEmpty();
    }
    
    public static int stackSize(int stackId) {
        java.util.Stack<String> stack = stacks.get(stackId);
        return stack == null ? 0 : stack.size();
    }
    
    public static int stackClear(int stackId) {
        java.util.Stack<String> stack = stacks.get(stackId);
        if (stack != null) {
            stack.clear();
        }
        return 0;
    }
    
    // ===== QUEUE =====
    
    public static int queueNew() {
        int id = nextQueueId++;
        queues.put(id, new java.util.LinkedList<String>());
        return id;
    }
    
    public static int queueEnqueue(int queueId, String value) {
        java.util.LinkedList<String> queue = queues.get(queueId);
        if (queue == null) return -1;
        queue.addLast(value);
        return queue.size();
    }
    
    public static String queueDequeue(int queueId) {
        java.util.LinkedList<String> queue = queues.get(queueId);
        if (queue == null || queue.isEmpty()) return "";
        return queue.removeFirst();
    }
    
    public static String queuePeek(int queueId) {
        java.util.LinkedList<String> queue = queues.get(queueId);
        if (queue == null || queue.isEmpty()) return "";
        return queue.peekFirst();
    }
    
    public static boolean queueIsEmpty(int queueId) {
        java.util.LinkedList<String> queue = queues.get(queueId);
        return queue == null || queue.isEmpty();
    }
    
    public static int queueSize(int queueId) {
        java.util.LinkedList<String> queue = queues.get(queueId);
        return queue == null ? 0 : queue.size();
    }
    
    public static int queueClear(int queueId) {
        java.util.LinkedList<String> queue = queues.get(queueId);
        if (queue != null) {
            queue.clear();
        }
        return 0;
    }
    
    // ===== Phase 9: Console I/O (Modern VB-style) =====
    
    /**
     * Console.WriteLine - Write a line to console with newline
     */
    public static int consoleWriteLine(String text) {
        System.out.println(text);
        return 0;
    }
    
    /**
     * Console.Write - Write to console without newline
     */
    public static int consoleWrite(String text) {
        System.out.print(text);
        return 0;
    }
    
    /**
     * Console.ReadLine - Read a line from console
     */
    public static String consoleReadLine() {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(System.in));
            return reader.readLine();
        } catch (java.io.IOException e) {
            return "";
        }
    }
    
    /**
     * Console.ReadKey - Read a single character from console
     */
    public static String consoleReadKey() {
        try {
            int ch = System.in.read();
            return String.valueOf((char)ch);
        } catch (java.io.IOException e) {
            return "";
        }
    }
    
    // ===== Phase 9: Console Namespace (OO-style dot syntax) =====
    
    /**
     * Console.WriteLine via namespace syntax
     */
    public static int console_WriteLine(String text) {
        System.out.println(text);
        return 0;
    }
    
    public static int console_Write(String text) {
        System.out.print(text);
        return 0;
    }
    
    public static String console_ReadLine() {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(System.in));
            String line = reader.readLine();
            return line == null ? "" : line;
        } catch (java.io.IOException e) {
            return "";
        }
    }
    
    public static String console_ReadKey() {
        try {
            int ch = System.in.read();
            return ch == -1 ? "" : String.valueOf((char)ch);
        } catch (java.io.IOException e) {
            return "";
        }
    }
    
    // ===== Phase 9: Math Namespace =====
    
    public static float math_Sin(float x) { return (float) Math.sin(x); }
    public static float math_Cos(float x) { return (float) Math.cos(x); }
    public static float math_Tan(float x) { return (float) Math.tan(x); }
    public static float math_Asin(float x) { return (float) Math.asin(x); }
    public static float math_Acos(float x) { return (float) Math.acos(x); }
    public static float math_Atan(float x) { return (float) Math.atan(x); }
    public static float math_Atan2(float y, float x) { return (float) Math.atan2(y, x); }
    public static float math_Sqrt(float x) { return (float) Math.sqrt(x); }
    public static float math_Pow(float x, float y) { return (float) Math.pow(x, y); }
    public static float math_Exp(float x) { return (float) Math.exp(x); }
    public static float math_Log(float x) { return (float) Math.log(x); }
    public static float math_Log10(float x) { return (float) Math.log10(x); }
    public static float math_Abs(float x) { return Math.abs(x); }
    public static float math_Ceil(float x) { return (float) Math.ceil(x); }
    public static float math_Floor(float x) { return (float) Math.floor(x); }
    public static int math_Round(float x) { return Math.round(x); }
    public static float math_Min(float a, float b) { return Math.min(a, b); }
    public static float math_Max(float a, float b) { return Math.max(a, b); }
    public static float math_PI() { return (float) Math.PI; }
    public static float math_E() { return (float) Math.E; }
    
    // ===== Phase 9: File Namespace =====
    
    public static String file_ReadAllText(String filename) {
        try {
            return new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(filename)));
        } catch (Exception e) {
            return "";
        }
    }
    
    public static int file_WriteAllText(String filename, String content) {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(filename), 
                content.getBytes());
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static int file_Exists(String filename) {
        return java.nio.file.Files.exists(java.nio.file.Paths.get(filename)) ? 1 : 0;
    }
    
    public static int file_Delete(String filename) {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(filename));
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static int file_Copy(String source, String dest) {
        try {
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(source),
                java.nio.file.Paths.get(dest),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static int file_Move(String source, String dest) {
        try {
            java.nio.file.Files.move(
                java.nio.file.Paths.get(source),
                java.nio.file.Paths.get(dest),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static float file_Size(String filename) {
        try {
            return (float) java.nio.file.Files.size(java.nio.file.Paths.get(filename));
        } catch (Exception e) {
            return -1.0f;
        }
    }
    
    public static int file_IsDirectory(String path) {
        return java.nio.file.Files.isDirectory(java.nio.file.Paths.get(path)) ? 1 : 0;
    }
    
    // ===== Phase 9: Http Namespace =====
    
    public static String http_Get(String url) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String http_Post(String url, String data) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            
            java.io.OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String http_UrlEncode(String text) {
        try {
            return java.net.URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }
    
    public static String http_UrlDecode(String text) {
        try {
            return java.net.URLDecoder.decode(text, "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }
    
    // ===== Phase 9: Json Namespace =====
    // JSON implementation using Google Gson library
    
    private static java.util.Map<Integer, com.google.gson.JsonObject> jsonObjects = new java.util.HashMap<>();
    private static int nextJsonId = 1;
    private static com.google.gson.Gson gson = new com.google.gson.Gson();
    
    public static int json_Parse(String jsonString) {
        try {
            com.google.gson.JsonObject obj = gson.fromJson(jsonString, com.google.gson.JsonObject.class);
            int id = nextJsonId++;
            jsonObjects.put(id, obj);
            return id;
        } catch (Exception e) {
            System.err.println("JSON Parse Error: " + e.getMessage());
            return -1;
        }
    }
    
    public static String json_GetString(int objId, String key) {
        try {
            com.google.gson.JsonObject obj = jsonObjects.get(objId);
            if (obj != null && obj.has(key)) {
                com.google.gson.JsonElement elem = obj.get(key);
                if (elem.isJsonPrimitive()) {
                    return elem.getAsString();
                }
                return elem.toString();
            }
        } catch (Exception e) {
            System.err.println("JSON GetString Error: " + e.getMessage());
        }
        return "";
    }
    
    public static int json_GetInt(int objId, String key) {
        try {
            com.google.gson.JsonObject obj = jsonObjects.get(objId);
            if (obj != null && obj.has(key)) {
                return obj.get(key).getAsInt();
            }
        } catch (Exception e) {
            System.err.println("JSON GetInt Error: " + e.getMessage());
        }
        return 0;
    }
    
    public static float json_GetFloat(int objId, String key) {
        try {
            com.google.gson.JsonObject obj = jsonObjects.get(objId);
            if (obj != null && obj.has(key)) {
                return obj.get(key).getAsFloat();
            }
        } catch (Exception e) {
            System.err.println("JSON GetFloat Error: " + e.getMessage());
        }
        return 0.0f;
    }
    
    public static int json_NewObject() {
        int id = nextJsonId++;
        jsonObjects.put(id, new com.google.gson.JsonObject());
        return id;
    }
    
    public static int json_Put(int objId, String key, String value) {
        try {
            com.google.gson.JsonObject obj = jsonObjects.get(objId);
            if (obj != null) {
                obj.addProperty(key, value);
                return 0;
            }
        } catch (Exception e) {
            System.err.println("JSON Put Error: " + e.getMessage());
        }
        return -1;
    }
    
    public static int json_PutInt(int objId, String key, int value) {
        try {
            com.google.gson.JsonObject obj = jsonObjects.get(objId);
            if (obj != null) {
                obj.addProperty(key, value);
                return 0;
            }
        } catch (Exception e) {
            System.err.println("JSON PutInt Error: " + e.getMessage());
        }
        return -1;
    }
    
    public static String json_ToString(int objId) {
        try {
            com.google.gson.JsonObject obj = jsonObjects.get(objId);
            if (obj != null) {
                return gson.toJson(obj);
            }
        } catch (Exception e) {
            System.err.println("JSON ToString Error: " + e.getMessage());
        }
        return "";
    }
    
    // ===== Phase 9: Xml Namespace =====
    // XML parsing using Java's built-in DOM APIs
    
    private static java.util.Map<Integer, org.w3c.dom.Document> xmlDocuments = new java.util.HashMap<>();
    private static int nextXmlId = 1;
    
    public static int xml_Parse(String xmlString) {
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            org.w3c.dom.Document doc = builder.parse(input);
            doc.getDocumentElement().normalize();
            
            int id = nextXmlId++;
            xmlDocuments.put(id, doc);
            return id;
        } catch (Exception e) {
            System.err.println("XML Parse Error: " + e.getMessage());
            return -1;
        }
    }
    
    public static String xml_GetText(int docId, String xpath) {
        try {
            org.w3c.dom.Document doc = xmlDocuments.get(docId);
            if (doc == null) return "";
            
            javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xpathObj = xpathFactory.newXPath();
            return xpathObj.evaluate(xpath, doc);
        } catch (Exception e) {
            System.err.println("XPath Error: " + e.getMessage());
            return "";
        }
    }
    
    // ===== Phase 9: Db Namespace =====
    // Database connection management
    
    private static java.util.Map<Integer, java.sql.Connection> dbConnections = new java.util.HashMap<>();
    private static java.util.Map<Integer, java.sql.ResultSet> dbResults = new java.util.HashMap<>();
    private static int nextDbId = 1;
    
    public static int db_Connect(String url, String user, String password) {
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, password);
            int id = nextDbId++;
            dbConnections.put(id, conn);
            return id;
        } catch (Exception e) {
            System.err.println("DB Error: " + e.getMessage());
            return -1;
        }
    }
    
    public static int db_Query(int connId, String sql) {
        try {
            java.sql.Connection conn = dbConnections.get(connId);
            if (conn != null) {
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql);
                int id = nextDbId++;
                dbResults.put(id, rs);
                return id;
            }
            return -1;
        } catch (Exception e) {
            System.err.println("DB Error: " + e.getMessage());
            return -1;
        }
    }
    
    public static int db_Next(int resultId) {
        try {
            java.sql.ResultSet rs = dbResults.get(resultId);
            return (rs != null && rs.next()) ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    public static String db_GetString(int resultId, String columnName) {
        try {
            java.sql.ResultSet rs = dbResults.get(resultId);
            return rs != null ? rs.getString(columnName) : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    public static int db_GetInt(int resultId, String columnName) {
        try {
            java.sql.ResultSet rs = dbResults.get(resultId);
            return rs != null ? rs.getInt(columnName) : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    public static int db_Close(int connId) {
        try {
            java.sql.Connection conn = dbConnections.get(connId);
            if (conn != null) {
                conn.close();
                dbConnections.remove(connId);
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
}


