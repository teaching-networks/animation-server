/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security

import edu.hm.cs.animation.server.security.util.PasswordUtil
import edu.hm.cs.animation.server.user.dao.UserDAO
import edu.hm.cs.animation.server.user.model.User
import io.javalin.http.Context
import javalinjwt.JWTProvider
import org.eclipse.jetty.http.HttpStatus
import java.sql.Timestamp
import java.time.Duration
import java.util.*

/**
 * REST Controller handling authentication matters.
 */
object AuthController {

    const val PATH = "auth"

    private const val DEFAULT_USERNAME = "admin"

    private const val DEFAULT_PASSWORD = "admin"

    private const val MAXIMUM_LOGIN_ATTEMPTS = 10

    private const val LOGIN_PAUSE_SECONDS: Long = 30 * 60

    private val userDAO = UserDAO()

    /**
     * Try to authenticate and generate a JSON Web Token (JWT).
     */
    fun authenticate(ctx: Context, jwtProvider: JWTProvider) {
        try {
            val credentials = ctx.basicAuthCredentials()

            val userCount = userDAO.getUserCount()
            if (userCount == 0L) {
                if (credentials.username == DEFAULT_USERNAME && credentials.password == DEFAULT_PASSWORD) {
                    onUserVerified(null, jwtProvider, ctx)
                } else {
                    throw CredentialsException("Invalid credentials")
                }
            } else {
                val user: User
                try {
                    user = userDAO.findUserByName(credentials.username)
                } catch (e: Exception) {
                    throw CredentialsException("Invalid credentials")
                }

                try {
                    if (user.unsuccessfulLoginAttempts!! >= MAXIMUM_LOGIN_ATTEMPTS) {
                        val lockEnd = Timestamp(user.lastUnsuccessfulLogin!!.time + Duration.ofSeconds(LOGIN_PAUSE_SECONDS).toMillis())
                        val now = Timestamp(Calendar.getInstance().time.time)

                        if (!now.after(lockEnd)) {
                            throw UserLockedException()
                        }
                    }

                    if (PasswordUtil.verifyPassword(credentials.password, user.password!!, user.passwordSalt!!)) {
                        onUserVerified(user, jwtProvider, ctx)
                    } else {
                        throw CredentialsException("Invalid credentials")
                    }
                } catch (e: UserLockedException) {
                    onLoginException(user, e)
                } catch (e: CredentialsException) {
                    onLoginException(user, e)
                }
            }
        } catch (e: Exception) {
            ctx.status(HttpStatus.UNAUTHORIZED_401)
        }
    }

    private fun onLoginException(user: User, e: Exception) {
        user.lastUnsuccessfulLogin = Timestamp(Calendar.getInstance().time.time)

        if (user.unsuccessfulLoginAttempts!! < MAXIMUM_LOGIN_ATTEMPTS) {
            user.unsuccessfulLoginAttempts = user.unsuccessfulLoginAttempts!! + 1
        }

        userDAO.updateUser(user) // Store user again.

        throw e
    }

    private fun onUserVerified(user: User?, jwtProvider: JWTProvider, ctx: Context) {
        if (user != null && user.unsuccessfulLoginAttempts!! > 0) {
            user.unsuccessfulLoginAttempts = 0

            userDAO.updateUser(user)
        }

        ctx.result(jwtProvider.generateToken(user))
    }


}

class UserLockedException : Exception()

class CredentialsException(message: String) : Exception(message)
