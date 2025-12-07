package com.jvmbasic.runtime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * BasicDate - Date and Time functions for JVM BASIC 2.0
 *
 * Provides date/time operations accessible via Date.FunctionName() in BASIC code.
 * All methods are static for easy bytecode generation.
 * Uses Java 8+ java.time API (LocalDateTime, ZonedDateTime, Instant).
 * Dates are represented as ISO 8601 strings for simplicity.
 *
 * Example usage in BASIC:
 *   var now as String = Date.Now()
 *   var formatted as String = Date.Format(now, "yyyy-MM-dd")
 *   var tomorrow as String = Date.AddDays(now, 1)
 */
public final class BasicDate {

    private BasicDate() {} // Prevent instantiation

    // Default time zone for session (can be changed)
    private static ZoneId defaultZone = ZoneId.systemDefault();

    // ========================================================================
    // Current Time
    // ========================================================================

    /**
     * Returns current local datetime in ISO 8601 format: "2025-01-15T14:30:00"
     */
    public static String Now() {
        return LocalDateTime.now(defaultZone).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Returns current UTC datetime in ISO 8601 format: "2025-01-15T06:30:00Z"
     */
    public static String NowUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Returns current date only: "2025-01-15"
     */
    public static String Today() {
        return LocalDate.now(defaultZone).toString();
    }

    /**
     * Returns current time only: "14:30:00"
     */
    public static String Time() {
        return LocalTime.now(defaultZone).format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    /**
     * Returns Unix timestamp in milliseconds
     */
    public static long Timestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Returns Unix timestamp in seconds
     */
    public static long TimestampSeconds() {
        return Instant.now().getEpochSecond();
    }

    // ========================================================================
    // Formatting and Parsing
    // ========================================================================

    /**
     * Format a date string with a pattern.
     * Pattern examples: "yyyy-MM-dd", "MM/dd/yyyy", "HH:mm:ss", "EEE, MMM d, yyyy"
     */
    public static String Format(String dateStr, String pattern) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Parse a date string with a specific pattern.
     * Returns ISO 8601 format or empty string on failure.
     */
    public static String Parse(String dateStr, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            // Try parsing as datetime first, then date only
            try {
                LocalDateTime dt = LocalDateTime.parse(dateStr, formatter);
                return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e1) {
                LocalDate d = LocalDate.parse(dateStr, formatter);
                return d.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Convert to ISO 8601 format (normalize)
     */
    public static String ToIso(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Create datetime from Unix timestamp (milliseconds)
     */
    public static String FromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), defaultZone)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Create datetime from Unix timestamp (seconds)
     */
    public static String FromTimestampSeconds(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), defaultZone)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Convert datetime to Unix timestamp (milliseconds)
     */
    public static long ToTimestamp(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.atZone(defaultZone).toInstant().toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }

    // ========================================================================
    // Date/Time Components
    // ========================================================================

    /**
     * Get year component (e.g., 2025)
     */
    public static int Year(String dateStr) {
        try {
            return parseDateTime(dateStr).getYear();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get month component (1-12)
     */
    public static int Month(String dateStr) {
        try {
            return parseDateTime(dateStr).getMonthValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get day of month component (1-31)
     */
    public static int Day(String dateStr) {
        try {
            return parseDateTime(dateStr).getDayOfMonth();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get hour component (0-23)
     */
    public static int Hour(String dateStr) {
        try {
            return parseDateTime(dateStr).getHour();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get minute component (0-59)
     */
    public static int Minute(String dateStr) {
        try {
            return parseDateTime(dateStr).getMinute();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get second component (0-59)
     */
    public static int Second(String dateStr) {
        try {
            return parseDateTime(dateStr).getSecond();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get millisecond component (0-999)
     */
    public static int Millisecond(String dateStr) {
        try {
            return parseDateTime(dateStr).getNano() / 1_000_000;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get day of week (1=Monday, 7=Sunday)
     */
    public static int DayOfWeek(String dateStr) {
        try {
            return parseDateTime(dateStr).getDayOfWeek().getValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get day of year (1-366)
     */
    public static int DayOfYear(String dateStr) {
        try {
            return parseDateTime(dateStr).getDayOfYear();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get ISO week of year (1-53)
     */
    public static int WeekOfYear(String dateStr) {
        try {
            return parseDateTime(dateStr).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get quarter (1-4)
     */
    public static int Quarter(String dateStr) {
        try {
            return parseDateTime(dateStr).get(IsoFields.QUARTER_OF_YEAR);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if year is a leap year
     */
    public static boolean IsLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * Get number of days in a month
     */
    public static int DaysInMonth(int year, int month) {
        try {
            return YearMonth.of(year, month).lengthOfMonth();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get day name (e.g., "Monday", "Tuesday")
     */
    public static String DayName(String dateStr) {
        try {
            DayOfWeek dow = parseDateTime(dateStr).getDayOfWeek();
            return dow.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get month name (e.g., "January", "February")
     */
    public static String MonthName(String dateStr) {
        try {
            java.time.Month month = parseDateTime(dateStr).getMonth();
            return month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
        } catch (Exception e) {
            return "";
        }
    }

    // ========================================================================
    // Date Arithmetic
    // ========================================================================

    /**
     * Add days to a date
     */
    public static String AddDays(String dateStr, int days) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Add weeks to a date
     */
    public static String AddWeeks(String dateStr, int weeks) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusWeeks(weeks).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Add months to a date
     */
    public static String AddMonths(String dateStr, int months) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusMonths(months).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Add years to a date
     */
    public static String AddYears(String dateStr, int years) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusYears(years).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Add hours to a date
     */
    public static String AddHours(String dateStr, int hours) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusHours(hours).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Add minutes to a date
     */
    public static String AddMinutes(String dateStr, int minutes) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusMinutes(minutes).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Add seconds to a date
     */
    public static String AddSeconds(String dateStr, int seconds) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.plusSeconds(seconds).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Calculate days between two dates
     */
    public static long DaysBetween(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return ChronoUnit.DAYS.between(dt1, dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate months between two dates
     */
    public static long MonthsBetween(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return ChronoUnit.MONTHS.between(dt1, dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate years between two dates
     */
    public static long YearsBetween(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return ChronoUnit.YEARS.between(dt1, dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate hours between two dates
     */
    public static long HoursBetween(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return ChronoUnit.HOURS.between(dt1, dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate minutes between two dates
     */
    public static long MinutesBetween(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return ChronoUnit.MINUTES.between(dt1, dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate seconds between two dates
     */
    public static long SecondsBetween(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return ChronoUnit.SECONDS.between(dt1, dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    // ========================================================================
    // Date Comparison
    // ========================================================================

    /**
     * Check if date1 is before date2
     */
    public static boolean IsBefore(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return dt1.isBefore(dt2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if date1 is after date2
     */
    public static boolean IsAfter(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return dt1.isAfter(dt2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if two dates are equal (same moment)
     */
    public static boolean IsEqual(String dateStr1, String dateStr2) {
        try {
            LocalDateTime dt1 = parseDateTime(dateStr1);
            LocalDateTime dt2 = parseDateTime(dateStr2);
            return dt1.isEqual(dt2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if date is today
     */
    public static boolean IsToday(String dateStr) {
        try {
            LocalDate d = parseDateTime(dateStr).toLocalDate();
            return d.equals(LocalDate.now(defaultZone));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if date is in the past
     */
    public static boolean IsPast(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.isBefore(LocalDateTime.now(defaultZone));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if date is in the future
     */
    public static boolean IsFuture(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.isAfter(LocalDateTime.now(defaultZone));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if date falls on a weekend (Saturday or Sunday)
     */
    public static boolean IsWeekend(String dateStr) {
        try {
            DayOfWeek dow = parseDateTime(dateStr).getDayOfWeek();
            return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if date falls on a weekday (Monday-Friday)
     */
    public static boolean IsWeekday(String dateStr) {
        try {
            DayOfWeek dow = parseDateTime(dateStr).getDayOfWeek();
            return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // Time Zones
    // ========================================================================

    /**
     * Convert datetime to UTC
     */
    public static String ToUtc(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            ZonedDateTime zdt = dt.atZone(defaultZone);
            return zdt.withZoneSameInstant(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Convert UTC datetime to local timezone
     */
    public static String ToLocal(String dateStr) {
        try {
            // Try parsing as instant (with Z suffix)
            if (dateStr.endsWith("Z")) {
                Instant instant = Instant.parse(dateStr);
                return LocalDateTime.ofInstant(instant, defaultZone)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            // Otherwise just return as-is
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Convert datetime to a specific timezone
     * Zone examples: "America/New_York", "Europe/London", "Asia/Tokyo", "UTC"
     */
    public static String ToZone(String dateStr, String zoneId) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            ZonedDateTime zdt = dt.atZone(defaultZone);
            ZoneId targetZone = ZoneId.of(zoneId);
            return zdt.withZoneSameInstant(targetZone)
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get current timezone ID (e.g., "America/New_York")
     */
    public static String GetTimeZone() {
        return defaultZone.getId();
    }

    /**
     * Set default timezone for session
     */
    public static void SetTimeZone(String zoneId) {
        try {
            defaultZone = ZoneId.of(zoneId);
        } catch (Exception e) {
            // Keep current zone if invalid
        }
    }

    /**
     * Get timezone offset in hours (e.g., -5 for EST)
     */
    public static int GetTimeZoneOffset() {
        ZoneOffset offset = defaultZone.getRules().getOffset(Instant.now());
        return offset.getTotalSeconds() / 3600;
    }

    // ========================================================================
    // Creation Helpers
    // ========================================================================

    /**
     * Create a date from components
     */
    public static String Create(int year, int month, int day) {
        try {
            return LocalDate.of(year, month, day).atStartOfDay()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Create a datetime from components
     */
    public static String Create(int year, int month, int day, int hour, int minute, int second) {
        try {
            return LocalDateTime.of(year, month, day, hour, minute, second)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get start of day for a date
     */
    public static String StartOfDay(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.toLocalDate().atStartOfDay()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get end of day for a date (23:59:59)
     */
    public static String EndOfDay(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.toLocalDate().atTime(23, 59, 59)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get start of month for a date
     */
    public static String StartOfMonth(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.withDayOfMonth(1).toLocalDate().atStartOfDay()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get end of month for a date
     */
    public static String EndOfMonth(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return dt.withDayOfMonth(dt.toLocalDate().lengthOfMonth())
                    .toLocalDate().atTime(23, 59, 59)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get start of year for a date
     */
    public static String StartOfYear(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return LocalDate.of(dt.getYear(), 1, 1).atStartOfDay()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get end of year for a date
     */
    public static String EndOfYear(String dateStr) {
        try {
            LocalDateTime dt = parseDateTime(dateStr);
            return LocalDate.of(dt.getYear(), 12, 31).atTime(23, 59, 59)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateStr;
        }
    }

    // ========================================================================
    // Validation
    // ========================================================================

    /**
     * Check if a string is a valid date/datetime
     */
    public static boolean IsValid(String dateStr) {
        try {
            parseDateTime(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if year/month/day is a valid date
     */
    public static boolean IsValidDate(int year, int month, int day) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Parse a datetime string, trying multiple formats
     */
    private static LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Empty date string");
        }

        // Try ISO datetime format first (most common)
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {}

        // Try ISO date only (add midnight time)
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {}

        // Try instant format (UTC with Z suffix)
        try {
            Instant instant = Instant.parse(dateStr);
            return LocalDateTime.ofInstant(instant, defaultZone);
        } catch (DateTimeParseException ignored) {}

        // Try common formats
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "MMM d, yyyy",
            "MMMM d, yyyy"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
                try {
                    return LocalDateTime.parse(dateStr, formatter);
                } catch (DateTimeParseException e1) {
                    try {
                        return LocalDate.parse(dateStr, formatter).atStartOfDay();
                    } catch (DateTimeParseException ignored2) {}
                }
            } catch (Exception ignored) {}
        }

        throw new IllegalArgumentException("Cannot parse date: " + dateStr);
    }
}
