package com.example

import com.example.security.SecurityManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecurityManagerTest {

    @Test
    fun testPasswordHashingAndVerification() {
        val password = "masterAdminPassword123"
        val salt = SecurityManager.generateSalt()
        val hash = SecurityManager.hashPassword(password, salt)

        assertTrue(SecurityManager.verifyPassword(password, salt, hash))
        assertFalse(SecurityManager.verifyPassword("wrongPassword", salt, hash))
    }

    @Test
    fun testAesEncryptionAndDecryption() {
        val secret = "offlineSecurePassKey!99"
        val plainText = "{\"appName\":\"Civil Portal\",\"version\":1}"

        val encrypted = SecurityManager.encryptBackup(plainText, secret)
        assertNotEquals(plainText, encrypted)

        val decrypted = SecurityManager.decryptBackup(encrypted, secret)
        assertEquals(plainText, decrypted)
    }
}
