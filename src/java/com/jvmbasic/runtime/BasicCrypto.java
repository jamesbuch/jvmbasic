package com.jvmbasic.runtime;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.*;
import javax.crypto.spec.*;

// BouncyCastle imports for advanced crypto
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.crypto.generators.BCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

/**
 * BasicCrypto - Cryptographic functions for JVM BASIC 2.0
 *
 * Provides cryptographic operations accessible via Crypto.FunctionName() in BASIC code.
 * Uses Java's built-in crypto and BouncyCastle for advanced algorithms.
 *
 * Example usage in BASIC:
 *   var hash as String = Crypto.Sha256("hello")
 *   var encrypted as String = Crypto.AesEncrypt(data, key)
 *   var encoded as String = Crypto.Base64Encode("hello")
 */
public final class BasicCrypto {

    private BasicCrypto() {} // Prevent instantiation

    // Register BouncyCastle provider on class load
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // ========================================================================
    // Hash Functions (Message Digests)
    // ========================================================================

    /**
     * MD5 hash (128-bit) - NOT SECURE, for compatibility only
     */
    public static String Md5(String data) {
        return hashWithAlgorithm(data, "MD5");
    }

    /**
     * SHA-1 hash (160-bit) - NOT SECURE, for compatibility only
     */
    public static String Sha1(String data) {
        return hashWithAlgorithm(data, "SHA-1");
    }

    /**
     * SHA-224 hash
     */
    public static String Sha224(String data) {
        return hashWithAlgorithm(data, "SHA-224");
    }

    /**
     * SHA-256 hash (recommended)
     */
    public static String Sha256(String data) {
        return hashWithAlgorithm(data, "SHA-256");
    }

    /**
     * SHA-384 hash
     */
    public static String Sha384(String data) {
        return hashWithAlgorithm(data, "SHA-384");
    }

    /**
     * SHA-512 hash
     */
    public static String Sha512(String data) {
        return hashWithAlgorithm(data, "SHA-512");
    }

    /**
     * SHA3-256 hash (latest standard)
     */
    public static String Sha3_256(String data) {
        return hashWithAlgorithm(data, "SHA3-256");
    }

    /**
     * SHA3-512 hash
     */
    public static String Sha3_512(String data) {
        return hashWithAlgorithm(data, "SHA3-512");
    }

    /**
     * BLAKE2b-256 hash (fast, secure)
     */
    public static String Blake2b256(String data) {
        return hashWithAlgorithm(data, "BLAKE2B-256");
    }

    /**
     * RIPEMD-160 hash (used in Bitcoin)
     */
    public static String Ripemd160(String data) {
        return hashWithAlgorithm(data, "RIPEMD160");
    }

    /**
     * Generic hash function - specify algorithm by name
     */
    public static String Hash(String data, String algorithm) {
        return hashWithAlgorithm(data, algorithm);
    }

    private static String hashWithAlgorithm(String data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available: " + algorithm, e);
        }
    }

    /**
     * Hash a file by path
     */
    public static String HashFile(String path, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] fileBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
            byte[] hash = md.digest(fileBytes);
            return bytesToHex(hash);
        } catch (Exception e) {
            return "";
        }
    }

    // ========================================================================
    // HMAC (Keyed-Hash Message Authentication)
    // ========================================================================

    /**
     * HMAC-MD5
     */
    public static String HmacMd5(String data, String key) {
        return hmacWithAlgorithm(data, key, "HmacMD5");
    }

    /**
     * HMAC-SHA1
     */
    public static String HmacSha1(String data, String key) {
        return hmacWithAlgorithm(data, key, "HmacSHA1");
    }

    /**
     * HMAC-SHA256 (recommended)
     */
    public static String HmacSha256(String data, String key) {
        return hmacWithAlgorithm(data, key, "HmacSHA256");
    }

    /**
     * HMAC-SHA384
     */
    public static String HmacSha384(String data, String key) {
        return hmacWithAlgorithm(data, key, "HmacSHA384");
    }

    /**
     * HMAC-SHA512
     */
    public static String HmacSha512(String data, String key) {
        return hmacWithAlgorithm(data, key, "HmacSHA512");
    }

    private static String hmacWithAlgorithm(String data, String key, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmac);
        } catch (Exception e) {
            throw new RuntimeException("HMAC error: " + e.getMessage(), e);
        }
    }

    // ========================================================================
    // AES Encryption (Symmetric)
    // ========================================================================

    /**
     * Generate a random AES key (256-bit by default)
     */
    public static String GenerateAesKey() {
        return GenerateAesKey(256);
    }

    /**
     * Generate an AES key of specified bit length (128, 192, or 256)
     */
    public static String GenerateAesKey(int bits) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(bits);
            SecretKey key = keyGen.generateKey();
            return bytesToHex(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Key generation error: " + e.getMessage(), e);
        }
    }

    /**
     * AES-GCM encryption (authenticated encryption - recommended)
     * Returns Base64-encoded ciphertext with IV prepended
     */
    public static String AesGcmEncrypt(String plaintext, String keyHex) {
        try {
            byte[] key = hexToBytes(keyHex);
            byte[] iv = new byte[12]; // 96-bit IV for GCM
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128-bit auth tag
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), spec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to ciphertext
            byte[] result = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM encryption error: " + e.getMessage(), e);
        }
    }

    /**
     * AES-GCM decryption
     * Expects Base64-encoded ciphertext with IV prepended
     */
    public static String AesGcmDecrypt(String ciphertextBase64, String keyHex) {
        try {
            byte[] key = hexToBytes(keyHex);
            byte[] combined = Base64.getDecoder().decode(ciphertextBase64);

            // Extract IV and ciphertext
            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[combined.length - 12];
            System.arraycopy(combined, 0, iv, 0, 12);
            System.arraycopy(combined, 12, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), spec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM decryption error: " + e.getMessage(), e);
        }
    }

    /**
     * AES-CBC encryption (legacy compatibility)
     * Key and IV must be hex-encoded
     */
    public static String AesCbcEncrypt(String plaintext, String keyHex, String ivHex) {
        try {
            byte[] key = hexToBytes(keyHex);
            byte[] iv = hexToBytes(ivHex);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException("AES-CBC encryption error: " + e.getMessage(), e);
        }
    }

    /**
     * AES-CBC decryption
     */
    public static String AesCbcDecrypt(String ciphertextBase64, String keyHex, String ivHex) {
        try {
            byte[] key = hexToBytes(keyHex);
            byte[] iv = hexToBytes(ivHex);
            byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-CBC decryption error: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a random IV (16 bytes for AES)
     */
    public static String GenerateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return bytesToHex(iv);
    }

    // ========================================================================
    // Password Hashing
    // ========================================================================

    /**
     * Argon2id password hash (recommended for passwords)
     * Returns the hash in a format that includes salt and parameters
     */
    public static String Argon2Hash(String password) {
        try {
            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);

            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withParallelism(1)
                    .withMemoryAsKB(65536) // 64MB
                    .withIterations(3)
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] hash = new byte[32];
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash);

            // Return format: $argon2id$v=19$m=65536,t=3,p=1$<salt>$<hash>
            return "$argon2id$v=19$m=65536,t=3,p=1$" +
                    Base64.getEncoder().withoutPadding().encodeToString(salt) + "$" +
                    Base64.getEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Argon2 error: " + e.getMessage(), e);
        }
    }

    /**
     * Verify an Argon2 password hash
     */
    public static boolean Argon2Verify(String password, String hashString) {
        try {
            String[] parts = hashString.split("\\$");
            if (parts.length != 6 || !parts[1].equals("argon2id")) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[4]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[5]);

            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withParallelism(1)
                    .withMemoryAsKB(65536)
                    .withIterations(3)
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] hash = new byte[32];
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash);

            return MessageDigest.isEqual(hash, expectedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * BCrypt password hash
     */
    public static String BcryptHash(String password) {
        return BcryptHash(password, 12); // Default cost factor
    }

    /**
     * BCrypt password hash with custom cost factor (4-31)
     */
    public static String BcryptHash(String password, int cost) {
        try {
            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);

            byte[] hash = BCrypt.generate(
                    password.getBytes(StandardCharsets.UTF_8),
                    salt,
                    cost
            );

            // Return in standard BCrypt format
            return "$2a$" + String.format("%02d", cost) + "$" +
                    bcryptBase64Encode(salt) + bcryptBase64Encode(hash);
        } catch (Exception e) {
            throw new RuntimeException("BCrypt error: " + e.getMessage(), e);
        }
    }

    /**
     * Verify a BCrypt password hash
     */
    public static boolean BcryptVerify(String password, String hashString) {
        try {
            if (!hashString.startsWith("$2a$") && !hashString.startsWith("$2b$")) {
                return false;
            }

            String[] parts = hashString.split("\\$");
            if (parts.length != 4) return false;

            int cost = Integer.parseInt(parts[2]);
            String saltAndHash = parts[3];
            byte[] salt = bcryptBase64Decode(saltAndHash.substring(0, 22));

            byte[] hash = BCrypt.generate(
                    password.getBytes(StandardCharsets.UTF_8),
                    salt,
                    cost
            );

            String newHash = "$2a$" + String.format("%02d", cost) + "$" +
                    bcryptBase64Encode(salt) + bcryptBase64Encode(hash);

            return MessageDigest.isEqual(
                    hashString.getBytes(StandardCharsets.UTF_8),
                    newHash.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * PBKDF2 key derivation
     */
    public static String Pbkdf2(String password, String saltHex, int iterations, int keyLength) {
        try {
            byte[] salt = hexToBytes(saltHex);
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    keyLength * 8 // bits
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] key = factory.generateSecret(spec).getEncoded();
            return bytesToHex(key);
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 error: " + e.getMessage(), e);
        }
    }

    // ========================================================================
    // Digital Signatures
    // ========================================================================

    /**
     * Generate an RSA key pair
     * Returns array: [privateKeyBase64, publicKeyBase64]
     */
    public static String[] GenerateRsaKeyPair() {
        return GenerateRsaKeyPair(2048);
    }

    /**
     * Generate an RSA key pair with specified key size
     */
    public static String[] GenerateRsaKeyPair(int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            KeyPair pair = keyGen.generateKeyPair();

            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

            return new String[]{privateKey, publicKey};
        } catch (Exception e) {
            throw new RuntimeException("RSA key generation error: " + e.getMessage(), e);
        }
    }

    /**
     * Sign data with RSA private key (SHA256withRSA)
     */
    public static String RsaSign(String data, String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("RSA signing error: " + e.getMessage(), e);
        }
    }

    /**
     * Verify RSA signature
     */
    public static boolean RsaVerify(String data, String signatureBase64, String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = factory.generatePublic(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            return signature.verify(Base64.getDecoder().decode(signatureBase64));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate an ECDSA key pair (P-256 curve)
     */
    public static String[] GenerateEcKeyPair() {
        return GenerateEcKeyPair("secp256r1");
    }

    /**
     * Generate an ECDSA key pair with specified curve
     * Curves: "secp256r1" (P-256), "secp384r1" (P-384), "secp521r1" (P-521)
     */
    public static String[] GenerateEcKeyPair(String curve) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(curve);
            keyGen.initialize(ecSpec);
            KeyPair pair = keyGen.generateKeyPair();

            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

            return new String[]{privateKey, publicKey};
        } catch (Exception e) {
            throw new RuntimeException("EC key generation error: " + e.getMessage(), e);
        }
    }

    /**
     * Sign data with ECDSA private key
     */
    public static String EcdsaSign(String data, String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = factory.generatePrivate(spec);

            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("ECDSA signing error: " + e.getMessage(), e);
        }
    }

    /**
     * Verify ECDSA signature
     */
    public static boolean EcdsaVerify(String data, String signatureBase64, String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("EC");
            PublicKey publicKey = factory.generatePublic(spec);

            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            return signature.verify(Base64.getDecoder().decode(signatureBase64));
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // Encoding/Decoding
    // ========================================================================

    /**
     * Base64 encode
     */
    public static String Base64Encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 decode
     */
    public static String Base64Decode(String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * URL-safe Base64 encode
     */
    public static String Base64UrlEncode(String data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * URL-safe Base64 decode
     */
    public static String Base64UrlDecode(String encoded) {
        try {
            return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Hex encode
     */
    public static String HexEncode(String data) {
        return bytesToHex(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Hex decode
     */
    public static String HexDecode(String hex) {
        try {
            return new String(hexToBytes(hex), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    // ========================================================================
    // Random Number Generation
    // ========================================================================

    /**
     * Generate cryptographically secure random bytes (hex-encoded)
     */
    public static String RandomBytes(int length) {
        try {
            byte[] bytes = new byte[length];
            SecureRandom.getInstanceStrong().nextBytes(bytes);
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Random generation error: " + e.getMessage(), e);
        }
    }

    /**
     * Generate random bytes as Base64
     */
    public static String RandomBase64(int length) {
        try {
            byte[] bytes = new byte[length];
            SecureRandom.getInstanceStrong().nextBytes(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Random generation error: " + e.getMessage(), e);
        }
    }

    /**
     * Generate cryptographically secure random integer
     */
    public static int RandomInt(int min, int max) {
        try {
            return min + SecureRandom.getInstanceStrong().nextInt(max - min);
        } catch (Exception e) {
            return new SecureRandom().nextInt(max - min) + min;
        }
    }

    /**
     * Generate UUID v4 (random)
     */
    public static String Uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate UUID v4 without dashes
     */
    public static String UuidCompact() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // ========================================================================
    // Utility Functions
    // ========================================================================

    /**
     * Constant-time string comparison (prevents timing attacks)
     */
    public static boolean ConstantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate a random salt (hex-encoded)
     */
    public static String GenerateSalt() {
        return GenerateSalt(16);
    }

    /**
     * Generate a random salt of specified length (hex-encoded)
     */
    public static String GenerateSalt(int length) {
        return RandomBytes(length);
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // BCrypt uses a custom Base64 alphabet
    private static final char[] BCRYPT_BASE64 = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private static String bcryptBase64Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int off = 0;
        int len = data.length;
        while (off < len) {
            int c1 = data[off++] & 0xff;
            sb.append(BCRYPT_BASE64[(c1 >> 2) & 0x3f]);
            c1 = (c1 & 0x03) << 4;
            if (off >= len) {
                sb.append(BCRYPT_BASE64[c1 & 0x3f]);
                break;
            }
            int c2 = data[off++] & 0xff;
            c1 |= (c2 >> 4) & 0x0f;
            sb.append(BCRYPT_BASE64[c1 & 0x3f]);
            c1 = (c2 & 0x0f) << 2;
            if (off >= len) {
                sb.append(BCRYPT_BASE64[c1 & 0x3f]);
                break;
            }
            c2 = data[off++] & 0xff;
            c1 |= (c2 >> 6) & 0x03;
            sb.append(BCRYPT_BASE64[c1 & 0x3f]);
            sb.append(BCRYPT_BASE64[c2 & 0x3f]);
        }
        return sb.toString();
    }

    private static byte[] bcryptBase64Decode(String s) {
        byte[] result = new byte[(s.length() * 3) / 4];
        int off = 0, soff = 0, len = s.length();
        while (soff < len) {
            int c1 = bcryptBase64Index(s.charAt(soff++));
            int c2 = bcryptBase64Index(s.charAt(soff++));
            result[off++] = (byte) ((c1 << 2) | ((c2 & 0x30) >> 4));
            if (soff >= len || off >= result.length) break;
            int c3 = bcryptBase64Index(s.charAt(soff++));
            result[off++] = (byte) (((c2 & 0x0f) << 4) | ((c3 & 0x3c) >> 2));
            if (soff >= len || off >= result.length) break;
            int c4 = bcryptBase64Index(s.charAt(soff++));
            result[off++] = (byte) (((c3 & 0x03) << 6) | c4);
        }
        return result;
    }

    private static int bcryptBase64Index(char c) {
        for (int i = 0; i < BCRYPT_BASE64.length; i++) {
            if (BCRYPT_BASE64[i] == c) return i;
        }
        return -1;
    }
}
