package edu.hm.cs.animation.server.security.authenticator

import edu.hm.cs.animation.server.security.util.PasswordUtil
import edu.hm.cs.animation.server.user.dao.UserDAO
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.UsernamePasswordCredentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.profile.CommonProfile

/**
 * Simple authenticator which works with comparing username and password to a given pair.
 * Set the default username and password in case you have no users yet. This default user can only be used until the first user has been created.
 */
class UserPasswordAuthenticator(val defaultUsername: String, val defaultPassword: String) : Authenticator<UsernamePasswordCredentials> {

    private val userDAO = UserDAO()

    override fun validate(credentials: UsernamePasswordCredentials, context: WebContext) {
        val userCount = userDAO.getUserCount()

        if (userCount == 0L) {
            if (credentials.username == defaultUsername && credentials.password == defaultPassword) {
                onUserVerified(credentials, -1)
            } else {
                throw CredentialsException("Invalid credentials")
            }
        } else {
            val user = userDAO.findUserByName(credentials.username) ?: throw CredentialsException("Invalid credentials")

            if (PasswordUtil.verifyPassword(credentials.password, user.password, user.passwordSalt!!)) {
                onUserVerified(credentials, user.id!!)
            } else {
                throw CredentialsException("Invalid credentials")
            }
        }
    }

    private fun onUserVerified(credentials: UsernamePasswordCredentials, userId: Long) {
        val profile = CommonProfile()

        profile.id = credentials.username
        profile.addAttribute("username", credentials.username)
        profile.addAttribute("id", userId)

        credentials.userProfile = profile
    }

}