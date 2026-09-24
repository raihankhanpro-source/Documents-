package com.example.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object SecurityManager {

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return bytesToHex(saltBytes)
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = "$password:$salt"
        val hashBytes = md.digest(combined.toByteArray(Charsets.UTF_8))
        return bytesToHex(hashBytes)
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computed = hashPassword(password, salt)
        return computed.equals(expectedHash, ignoreCase = true)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    // Offline encrypted backup encryption/decryption using AES-GCM
    fun encryptBackup(plainTextJson: String, masterSecret: String): String {
        val keyHash = MessageDigest.getInstance("SHA-256").digest(masterSecret.toByteArray(Charsets.UTF_8))
        val secretKey = SecretKeySpec(keyHash, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val cipherText = cipher.doFinal(plainTextJson.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptBackup(encryptedBase64: String, masterSecret: String): String {
        val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
        if (combined.size < 12) throw IllegalArgumentException("Invalid encrypted payload")
        val iv = ByteArray(12)
        System.arraycopy(combined, 0, iv, 0, 12)
        val cipherText = ByteArray(combined.size - 12)
        System.arraycopy(combined, 12, cipherText, 0, cipherText.size)

        val keyHash = MessageDigest.getInstance("SHA-256").digest(masterSecret.toByteArray(Charsets.UTF_8))
        val secretKey = SecretKeySpec(keyHash, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val plainBytes = cipher.doFinal(cipherText)
        return String(plainBytes, Charsets.UTF_8)
    }
}
