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
}

