/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security.authenticator

import edu.hm.cs.animation.server.security.authenticator.exception.UserLockedException
import edu.hm.cs.animation.server.security.util.PasswordUtil
import edu.hm.cs.animation.server.user.dao.UserDAO
import edu.hm.cs.animation.server.user.model.User
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.UsernamePasswordCredentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.profile.CommonProfile
import java.sql.Timestamp
import java.time.Duration
import java.util.*

/**
 * Simple authenticator which works with comparing username and password to a given pair.
 * Set the default username and password in case you have no users yet. This default user can only be used until the first user has been created.
 */
class UserPasswordAuthenticator(private val defaultUsername: String, private val defaultPassword: String, private val maxLoginAttempts: Int, private val loginPauseDuration: Duration) : Authenticator<UsernamePasswordCredentials> {

    private val userDAO = UserDAO()

    override fun validate(credentials: UsernamePasswordCredentials, context: WebContext) {
        val userCount = userDAO.getUserCount()

        if (userCount == 0L) {
            if (credentials.username == defaultUsername && credentials.password == defaultPassword) {
                onUserVerified(credentials, -1, null)
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
                if (user.unsuccessfulLoginAttempts!! >= maxLoginAttempts) {
                    val lockEnd = Timestamp(user.lastUnsuccessfulLogin!!.time + loginPauseDuration.toMillis());
                    val now = Timestamp(Calendar.getInstance().time.time)

                    if (!now.after(lockEnd)) {
                        throw UserLockedException()
                    }
                }

                if (PasswordUtil.verifyPassword(credentials.password, user.password!!, user.passwordSalt!!)) {
                    onUserVerified(credentials, user.id!!, user)
                } else {
                    throw CredentialsException("Invalid credentials")
                }
            } catch (e: UserLockedException) {
                onLoginException(user, e)
            } catch (e: CredentialsException) {
                onLoginException(user, e)
            }
        }
    }

    private fun onLoginException(user: User, e: Exception) {
        user.lastUnsuccessfulLogin = Timestamp(Calendar.getInstance().time.time)

        if (user.unsuccessfulLoginAttempts!! < maxLoginAttempts) {
            user.unsuccessfulLoginAttempts = user.unsuccessfulLoginAttempts!! + 1
        }

        // Store user again.
        userDAO.updateUser(user)

        throw e;
    }

    private fun onUserVerified(credentials: UsernamePasswordCredentials, userId: Long, user: User?) {
        if (user != null && user.unsuccessfulLoginAttempts!! > 0) {
            user.unsuccessfulLoginAttempts = 0;

            userDAO.updateUser(user);
        }

        val profile = CommonProfile()

        profile.id = credentials.username
        profile.addAttribute("username", credentials.username)
        profile.addAttribute("id", userId)

        credentials.userProfile = profile
    }

}
