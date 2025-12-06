package com.jvmbasic.runtime;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * BasicHttp - HTTP client functions for JVM BASIC 2.0
 *
 * Provides HTTP request capabilities accessible via Http.FunctionName() in BASIC code.
 * Uses Java 11+ HttpClient for modern, async-capable HTTP.
 *
 * Example usage in BASIC:
 *   var response as String = Http.Get("https://api.example.com/data")
 *   var statusCode as Integer = Http.GetStatus()
 *
 *   ' POST with JSON
 *   Http.SetHeader("Content-Type", "application/json")
 *   var result as String = Http.Post("https://api.example.com/users", "{\"name\":\"Alice\"}")
 */
public final class BasicHttp {

    private BasicHttp() {} // Prevent instantiation

    // Thread-local state for headers and last response
    private static final ThreadLocal<Map<String, String>> requestHeaders =
        ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Integer> lastStatusCode =
        ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Map<String, String>> lastResponseHeaders =
        ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Integer> timeoutSeconds =
        ThreadLocal.withInitial(() -> 30);

    // Shared HTTP client
    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    // ========================================================================
    // Request Configuration
    // ========================================================================

    /**
     * Set a header for the next request
     */
    public static void SetHeader(String name, String value) {
        requestHeaders.get().put(name, value);
    }

    /**
     * Clear all request headers
     */
    public static void ClearHeaders() {
        requestHeaders.get().clear();
    }

    /**
     * Set timeout in seconds for requests
     */
    public static void SetTimeout(int seconds) {
        timeoutSeconds.set(seconds);
    }

    // ========================================================================
    // HTTP Methods
    // ========================================================================

    /**
     * Perform GET request
     */
    public static String Get(String url) {
        return request("GET", url, null);
    }

    /**
     * Perform POST request with body
     */
    public static String Post(String url, String body) {
        return request("POST", url, body);
    }

    /**
     * Perform PUT request with body
     */
    public static String Put(String url, String body) {
        return request("PUT", url, body);
    }

    /**
     * Perform PATCH request with body
     */
    public static String Patch(String url, String body) {
        return request("PATCH", url, body);
    }

    /**
     * Perform DELETE request
     */
    public static String Delete(String url) {
        return request("DELETE", url, null);
    }

    /**
     * Perform HEAD request (returns headers only, body will be empty)
     */
    public static String Head(String url) {
        return request("HEAD", url, null);
    }

    // ========================================================================
    // Response Access
    // ========================================================================

    /**
     * Get status code from last request
     */
    public static int GetStatus() {
        return lastStatusCode.get();
    }

    /**
     * Get a response header from last request
     */
    public static String GetResponseHeader(String name) {
        return lastResponseHeaders.get().getOrDefault(name, "");
    }

    /**
     * Check if last request was successful (2xx status)
     */
    public static boolean IsSuccess() {
        int status = lastStatusCode.get();
        return status >= 200 && status < 300;
    }

    /**
     * Check if last request resulted in client error (4xx status)
     */
    public static boolean IsClientError() {
        int status = lastStatusCode.get();
        return status >= 400 && status < 500;
    }

    /**
     * Check if last request resulted in server error (5xx status)
     */
    public static boolean IsServerError() {
        int status = lastStatusCode.get();
        return status >= 500 && status < 600;
    }

    // ========================================================================
    // Convenience Methods
    // ========================================================================

    /**
     * GET request expecting JSON, sets Accept header automatically
     */
    public static String GetJson(String url) {
        SetHeader("Accept", "application/json");
        return Get(url);
    }

    /**
     * POST JSON data, sets Content-Type automatically
     */
    public static String PostJson(String url, String json) {
        SetHeader("Content-Type", "application/json");
        SetHeader("Accept", "application/json");
        return Post(url, json);
    }

    /**
     * POST form data (application/x-www-form-urlencoded)
     */
    public static String PostForm(String url, String... pairs) {
        if (pairs.length % 2 != 0) {
            System.err.println("PostForm requires key-value pairs");
            return "";
        }

        StringBuilder body = new StringBuilder();
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) body.append("&");
            body.append(UrlEncode(pairs[i]))
                .append("=")
                .append(UrlEncode(pairs[i + 1]));
        }

        SetHeader("Content-Type", "application/x-www-form-urlencoded");
        return Post(url, body.toString());
    }

    // ========================================================================
    // URL Utilities
    // ========================================================================

    /**
     * URL-encode a string
     */
    public static String UrlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * URL-decode a string
     */
    public static String UrlDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * Build a URL with query parameters
     */
    public static String BuildUrl(String baseUrl, String... pairs) {
        if (pairs.length % 2 != 0 || pairs.length == 0) {
            return baseUrl;
        }

        StringBuilder url = new StringBuilder(baseUrl);
        url.append(baseUrl.contains("?") ? "&" : "?");

        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) url.append("&");
            url.append(UrlEncode(pairs[i]))
               .append("=")
               .append(UrlEncode(pairs[i + 1]));
        }

        return url.toString();
    }

    /**
     * Parse query string into key-value pairs
     * Returns alternating keys and values
     */
    public static String[] ParseQueryString(String query) {
        if (query == null || query.isEmpty()) {
            return new String[0];
        }

        // Remove leading ? if present
        if (query.startsWith("?")) {
            query = query.substring(1);
        }

        List<String> result = new ArrayList<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex > 0) {
                result.add(UrlDecode(pair.substring(0, eqIndex)));
                result.add(UrlDecode(pair.substring(eqIndex + 1)));
            }
        }

        return result.toArray(new String[0]);
    }

    // ========================================================================
    // Basic Authentication
    // ========================================================================

    /**
     * Set Basic Authentication header
     */
    public static void SetBasicAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        SetHeader("Authorization", "Basic " + encoded);
    }

    /**
     * Set Bearer token authentication
     */
    public static void SetBearerToken(String token) {
        SetHeader("Authorization", "Bearer " + token);
    }

    // ========================================================================
    // Download
    // ========================================================================

    /**
     * Download a file from URL to local path
     * Returns true if successful
     */
    public static boolean Download(String url, String filePath) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeoutSeconds.get()))
                    .GET();

            // Add headers
            for (Map.Entry<String, String> header : requestHeaders.get().entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }

            HttpRequest request = builder.build();
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

            lastStatusCode.set(response.statusCode());
            lastResponseHeaders.get().clear();
            response.headers().map().forEach((k, v) -> {
                if (!v.isEmpty()) {
                    lastResponseHeaders.get().put(k, v.get(0));
                }
            });

            // Clear request headers after use
            requestHeaders.get().clear();

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                java.nio.file.Files.write(
                    java.nio.file.Path.of(filePath),
                    response.body());
                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Download error: " + e.getMessage());
            lastStatusCode.set(-1);
            requestHeaders.get().clear();
            return false;
        }
    }

    // ========================================================================
    // Internal Helper
    // ========================================================================

    private static String request(String method, String url, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeoutSeconds.get()));

            // Set method and body
            if (body != null) {
                builder.method(method, BodyPublishers.ofString(body));
            } else if (method.equals("DELETE")) {
                builder.DELETE();
            } else if (method.equals("HEAD")) {
                builder.method("HEAD", BodyPublishers.noBody());
            } else {
                builder.GET();
            }

            // Add headers
            for (Map.Entry<String, String> header : requestHeaders.get().entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            // Store response info
            lastStatusCode.set(response.statusCode());
            lastResponseHeaders.get().clear();
            response.headers().map().forEach((k, v) -> {
                if (!v.isEmpty()) {
                    lastResponseHeaders.get().put(k, v.get(0));
                }
            });

            // Clear request headers after use
            requestHeaders.get().clear();

            return response.body();

        } catch (Exception e) {
            System.err.println("HTTP error: " + e.getMessage());
            lastStatusCode.set(-1);
            requestHeaders.get().clear();
            return "";
        }
    }
}
