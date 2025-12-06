package com.jvmbasic.runtime;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * BasicFile - File I/O functions for JVM BASIC 2.0
 *
 * Provides file operations accessible via File.FunctionName() in BASIC code.
 * All methods are static for easy bytecode generation.
 *
 * Example usage in BASIC:
 *   var content as String = File.ReadAllText("myfile.txt")
 *   File.WriteAllText("output.txt", "Hello, World!")
 */
public final class BasicFile {

    private BasicFile() {} // Prevent instantiation

    // ========================================================================
    // File Reading
    // ========================================================================

    /**
     * Read entire file as a string
     */
    public static String ReadAllText(String path) {
        try {
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Read file as array of lines
     */
    public static String[] ReadAllLines(String path) {
        try {
            List<String> lines = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
            return lines.toArray(new String[0]);
        } catch (IOException e) {
            return new String[0];
        }
    }

    /**
     * Read file as array of bytes
     */
    public static byte[] ReadAllBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            return new byte[0];
        }
    }

    // ========================================================================
    // File Writing
    // ========================================================================

    /**
     * Write string to file (overwrites existing)
     */
    public static boolean WriteAllText(String path, String content) {
        try {
            Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Write lines to file (overwrites existing)
     */
    public static boolean WriteAllLines(String path, String[] lines) {
        try {
            Files.write(Path.of(path), Arrays.asList(lines), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Write bytes to file (overwrites existing)
     */
    public static boolean WriteAllBytes(String path, byte[] data) {
        try {
            Files.write(Path.of(path), data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Append text to file
     */
    public static boolean AppendAllText(String path, String content) {
        try {
            Files.writeString(Path.of(path), content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Append lines to file
     */
    public static boolean AppendAllLines(String path, String[] lines) {
        try {
            Files.write(Path.of(path), Arrays.asList(lines), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ========================================================================
    // File Operations
    // ========================================================================

    /**
     * Check if file exists
     */
    public static boolean Exists(String path) {
        return Files.exists(Path.of(path));
    }

    /**
     * Check if path is a file (not directory)
     */
    public static boolean IsFile(String path) {
        return Files.isRegularFile(Path.of(path));
    }

    /**
     * Check if path is a directory
     */
    public static boolean IsDirectory(String path) {
        return Files.isDirectory(Path.of(path));
    }

    /**
     * Delete a file
     */
    public static boolean Delete(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Copy a file
     */
    public static boolean Copy(String source, String dest) {
        try {
            Files.copy(Path.of(source), Path.of(dest), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Move/rename a file
     */
    public static boolean Move(String source, String dest) {
        try {
            Files.move(Path.of(source), Path.of(dest), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Create a directory (including parent directories)
     */
    public static boolean CreateDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ========================================================================
    // File Information
    // ========================================================================

    /**
     * Get file size in bytes
     */
    public static long Size(String path) {
        try {
            return Files.size(Path.of(path));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Get file name (without directory)
     */
    public static String GetFileName(String path) {
        Path p = Path.of(path).getFileName();
        return p != null ? p.toString() : "";
    }

    /**
     * Get file extension
     */
    public static String GetExtension(String path) {
        String name = GetFileName(path);
        int dotIndex = name.lastIndexOf('.');
        return dotIndex >= 0 ? name.substring(dotIndex) : "";
    }

    /**
     * Get file name without extension
     */
    public static String GetFileNameWithoutExtension(String path) {
        String name = GetFileName(path);
        int dotIndex = name.lastIndexOf('.');
        return dotIndex >= 0 ? name.substring(0, dotIndex) : name;
    }

    /**
     * Get parent directory
     */
    public static String GetDirectory(String path) {
        Path p = Path.of(path).getParent();
        return p != null ? p.toString() : "";
    }

    /**
     * Get absolute path
     */
    public static String GetFullPath(String path) {
        return Path.of(path).toAbsolutePath().toString();
    }

    /**
     * Combine path parts
     */
    public static String Combine(String path1, String path2) {
        return Path.of(path1, path2).toString();
    }

    // ========================================================================
    // Directory Listing
    // ========================================================================

    /**
     * List files in directory
     */
    public static String[] ListFiles(String path) {
        try (Stream<Path> stream = Files.list(Path.of(path))) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(p -> p.toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }

    /**
     * List directories in directory
     */
    public static String[] ListDirectories(String path) {
        try (Stream<Path> stream = Files.list(Path.of(path))) {
            return stream
                    .filter(Files::isDirectory)
                    .map(p -> p.toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }

    /**
     * List all entries in directory (files and directories)
     */
    public static String[] ListAll(String path) {
        try (Stream<Path> stream = Files.list(Path.of(path))) {
            return stream
                    .map(p -> p.toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }

    /**
     * List files matching a glob pattern (e.g., "*.txt")
     */
    public static String[] ListFilesWithPattern(String directory, String pattern) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(directory), pattern)) {
            List<String> result = new ArrayList<>();
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    result.add(path.toString());
                }
            }
            return result.toArray(new String[0]);
        } catch (IOException e) {
            return new String[0];
        }
    }

    // ========================================================================
    // Temporary Files
    // ========================================================================

    /**
     * Create a temporary file and return its path
     */
    public static String CreateTempFile(String prefix, String suffix) {
        try {
            return Files.createTempFile(prefix, suffix).toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Get the system's temporary directory
     */
    public static String GetTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    // ========================================================================
    // Working Directory
    // ========================================================================

    /**
     * Get current working directory
     */
    public static String GetCurrentDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * Get user's home directory
     */
    public static String GetHomeDirectory() {
        return System.getProperty("user.home");
    }
}
