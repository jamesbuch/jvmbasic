' Test Crypto Namespace - Phase 10 Priority 3
' Crypto.Sha256, Crypto.Sha512, Crypto.Md5, Crypto.AesEncrypt, Crypto.AesDecrypt
' Crypto.Base64Encode, Crypto.Base64Decode, Crypto.RandomBytes

Console.WriteLine("=== Crypto Namespace Test ===")

' Test hashing
Console.WriteLine("")
Console.WriteLine("--- Hashing Tests ---")

Dim testString As String
testString = "Hello, World!"

Dim sha256Hash As String
sha256Hash = Crypto.Sha256(testString)
Console.WriteLine("SHA-256 of '" + testString + "':")
Console.WriteLine("  " + sha256Hash)

Dim md5Hash As String
md5Hash = Crypto.Md5(testString)
Console.WriteLine("MD5 of '" + testString + "':")
Console.WriteLine("  " + md5Hash)

Dim sha512Hash As String
sha512Hash = Crypto.Sha512(testString)
Console.WriteLine("SHA-512 of '" + testString + "':")
Console.WriteLine("  " + sha512Hash)

' Test AES encryption/decryption
Console.WriteLine("")
Console.WriteLine("--- AES Encryption Tests ---")

Dim plaintext As String
plaintext = "This is a secret message!"
Dim password As String
password = "MySecretKey123"

Console.WriteLine("Original: " + plaintext)

Dim encrypted As String
encrypted = Crypto.AesEncrypt(plaintext, password)
Console.WriteLine("Encrypted: " + encrypted)

Dim decrypted As String
decrypted = Crypto.AesDecrypt(encrypted, password)
Console.WriteLine("Decrypted: " + decrypted)

IF decrypted == plaintext THEN
    Console.WriteLine("AES encryption/decryption: SUCCESS")
ELSE
    Console.WriteLine("AES encryption/decryption: FAILED")
ENDIF

' Test Base64 encoding/decoding
Console.WriteLine("")
Console.WriteLine("--- Base64 Tests ---")

Dim original As String
original = "Hello, Base64!"

Dim encoded As String
encoded = Crypto.Base64Encode(original)
Console.WriteLine("Original: " + original)
Console.WriteLine("Base64 encoded: " + encoded)

Dim decoded As String
decoded = Crypto.Base64Decode(encoded)
Console.WriteLine("Base64 decoded: " + decoded)

IF decoded == original THEN
    Console.WriteLine("Base64 encoding/decoding: SUCCESS")
ELSE
    Console.WriteLine("Base64 encoding/decoding: FAILED")
ENDIF

' Test random bytes
Console.WriteLine("")
Console.WriteLine("--- Random Bytes Test ---")

Dim random16 As String
random16 = Crypto.RandomBytes(16)
Console.WriteLine("16 random bytes (hex): " + random16)

Dim random32 As String
random32 = Crypto.RandomBytes(32)
Console.WriteLine("32 random bytes (hex): " + random32)

Console.WriteLine("=== Crypto Namespace Test Complete ===")
