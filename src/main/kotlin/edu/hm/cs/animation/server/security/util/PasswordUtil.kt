/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security.util

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password utility is handling password encryption and validation.
 */
object PasswordUtil {

    const val DEFAULT_SALT_LENGTH = 32

    private val RAND = SecureRandom()
    private val SALT_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val ITERATIONS = 10000
    private val KEY_LENGTH = 256

    /**
     * Get a random salt.
     * You can use the result to hash a password.
     *
     * @param length of the salt
     * @return the resuling salt of the provided length
     */
    fun getSalt(length: Int): String {
        val sB = StringBuilder(length)

        for (i in 0 until length) {
            sB.append(SALT_CHARACTERS[RAND.nextInt(SALT_CHARACTERS.length)])
        }

        return String(sB)
    }

    /**
     * Hash a password using the passed salt.
     *
     * @param password to hash
     * @param salt to use to hash the password
     * @return the hashed password
     */
    private fun hash(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        Arrays.fill(password, Character.MIN_VALUE)

        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    /**
     * Generate a secure password using the passed plain-text password and salt.
     *
     * @param password to secure
     * @param salt to use
     * @return The secured password
     */
    fun securePassword(password: String, salt: String): String {
        val securePassword = hash(password.toCharArray(), salt.toByteArray())

        return Base64.getEncoder().encodeToString(securePassword)
    }

    /**
     * Verify a password with a already hashed one.
     *
     * @param password The password to verify
     * @param hashedPassword The already hashed password to verify the new password against
     * @param salt The salt used to hash the already hashed password
     * @return Whether the password could be verified
     */
    fun verifyPassword(password: String, hashedPassword: String, salt: String): Boolean {
        val newHashedPassword = securePassword(password, salt)

        return newHashedPassword.equals(hashedPassword);
    }

}
