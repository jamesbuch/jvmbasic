package com.jvmbasic.runtime;

/**
 * BasicMath - Math functions for JVM BASIC 2.0
 *
 * Provides mathematical functions accessible via Math.FunctionName() in BASIC code.
 * All methods are static for easy bytecode generation.
 *
 * Example usage in BASIC:
 *   var result as Double = Math.Sqrt(16.0)
 *   var angle as Double = Math.Sin(Math.Pi / 2)
 */
public final class BasicMath {

    private BasicMath() {} // Prevent instantiation

    // ========================================================================
    // Constants
    // ========================================================================

    public static double Pi() {
        return Math.PI;
    }

    public static double E() {
        return Math.E;
    }

    // ========================================================================
    // Basic Operations
    // ========================================================================

    public static int Abs(int value) {
        return Math.abs(value);
    }

    public static long Abs(long value) {
        return Math.abs(value);
    }

    public static float Abs(float value) {
        return Math.abs(value);
    }

    public static double Abs(double value) {
        return Math.abs(value);
    }

    public static int Sign(int value) {
        return Integer.signum(value);
    }

    public static int Sign(long value) {
        return Long.signum(value);
    }

    public static int Sign(double value) {
        return (int) Math.signum(value);
    }

    public static double Sqrt(double value) {
        return Math.sqrt(value);
    }

    public static double Cbrt(double value) {
        return Math.cbrt(value);
    }

    public static double Pow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public static double Exp(double value) {
        return Math.exp(value);
    }

    public static double Log(double value) {
        return Math.log(value);
    }

    public static double Log10(double value) {
        return Math.log10(value);
    }

    public static double Log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    // ========================================================================
    // Rounding
    // ========================================================================

    public static double Floor(double value) {
        return Math.floor(value);
    }

    public static double Ceil(double value) {
        return Math.ceil(value);
    }

    public static long Round(double value) {
        return Math.round(value);
    }

    public static int Round(float value) {
        return Math.round(value);
    }

    public static double Truncate(double value) {
        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }

    // ========================================================================
    // Min/Max
    // ========================================================================

    public static int Min(int a, int b) {
        return Math.min(a, b);
    }

    public static long Min(long a, long b) {
        return Math.min(a, b);
    }

    public static float Min(float a, float b) {
        return Math.min(a, b);
    }

    public static double Min(double a, double b) {
        return Math.min(a, b);
    }

    public static int Max(int a, int b) {
        return Math.max(a, b);
    }

    public static long Max(long a, long b) {
        return Math.max(a, b);
    }

    public static float Max(float a, float b) {
        return Math.max(a, b);
    }

    public static double Max(double a, double b) {
        return Math.max(a, b);
    }

    public static int Clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double Clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // ========================================================================
    // Trigonometric Functions
    // ========================================================================

    public static double Sin(double radians) {
        return Math.sin(radians);
    }

    public static double Cos(double radians) {
        return Math.cos(radians);
    }

    public static double Tan(double radians) {
        return Math.tan(radians);
    }

    public static double Asin(double value) {
        return Math.asin(value);
    }

    public static double Acos(double value) {
        return Math.acos(value);
    }

    public static double Atan(double value) {
        return Math.atan(value);
    }

    public static double Atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    // ========================================================================
    // Hyperbolic Functions
    // ========================================================================

    public static double Sinh(double value) {
        return Math.sinh(value);
    }

    public static double Cosh(double value) {
        return Math.cosh(value);
    }

    public static double Tanh(double value) {
        return Math.tanh(value);
    }

    // ========================================================================
    // Angle Conversion
    // ========================================================================

    public static double ToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double ToDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    // ========================================================================
    // Random Numbers
    // ========================================================================

    private static final java.util.Random random = new java.util.Random();

    /**
     * Returns a random double between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static double Random() {
        return random.nextDouble();
    }

    /**
     * Returns a random integer between 0 (inclusive) and max (exclusive)
     */
    public static int Random(int max) {
        return random.nextInt(max);
    }

    /**
     * Returns a random integer between min (inclusive) and max (exclusive)
     */
    public static int Random(int min, int max) {
        return random.nextInt(min, max);
    }

    /**
     * Seeds the random number generator for reproducible results
     */
    public static void Randomize(long seed) {
        random.setSeed(seed);
    }

    // ========================================================================
    // Utility Functions
    // ========================================================================

    public static double Hypot(double x, double y) {
        return Math.hypot(x, y);
    }

    public static double Lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static boolean IsNaN(double value) {
        return Double.isNaN(value);
    }

    public static boolean IsInfinite(double value) {
        return Double.isInfinite(value);
    }

    public static boolean IsFinite(double value) {
        return Double.isFinite(value);
    }

    // ========================================================================
    // Epsilon Comparison (for floating-point equality)
    // ========================================================================

    /** Default epsilon for float comparison (approximately 1e-7 * 10) */
    public static final double FLOAT_EPSILON = 1e-6;

    /** Default epsilon for double comparison (approximately 1e-15 * 10) */
    public static final double DOUBLE_EPSILON = 1e-14;

    /**
     * Compares two floats for approximate equality using default epsilon
     */
    public static boolean ApproxEqual(float a, float b) {
        return ApproxEqual(a, b, (float) FLOAT_EPSILON);
    }

    /**
     * Compares two floats for approximate equality with custom epsilon
     */
    public static boolean ApproxEqual(float a, float b, float epsilon) {
        if (a == b) return true; // Handles infinities and same value
        if (Float.isNaN(a) || Float.isNaN(b)) return false;
        return Math.abs(a - b) < epsilon;
    }

    /**
     * Compares two doubles for approximate equality using default epsilon
     */
    public static boolean ApproxEqual(double a, double b) {
        return ApproxEqual(a, b, DOUBLE_EPSILON);
    }

    /**
     * Compares two doubles for approximate equality with custom epsilon
     */
    public static boolean ApproxEqual(double a, double b, double epsilon) {
        if (a == b) return true; // Handles infinities and same value
        if (Double.isNaN(a) || Double.isNaN(b)) return false;
        return Math.abs(a - b) < epsilon;
    }

    /**
     * Relative comparison for doubles (handles values of different magnitudes better)
     */
    public static boolean ApproxEqualRelative(double a, double b, double relativeTolerance) {
        if (a == b) return true;
        if (Double.isNaN(a) || Double.isNaN(b)) return false;
        double diff = Math.abs(a - b);
        double maxAbs = Math.max(Math.abs(a), Math.abs(b));
        return diff <= relativeTolerance * maxAbs;
    }

    /**
     * Returns the machine epsilon for float
     */
    public static float FloatEpsilon() {
        return Math.ulp(1.0f);
    }

    /**
     * Returns the machine epsilon for double
     */
    public static double DoubleEpsilon() {
        return Math.ulp(1.0);
    }
}
