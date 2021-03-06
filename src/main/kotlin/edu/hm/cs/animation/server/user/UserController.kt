/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.user

import edu.hm.cs.animation.server.security.util.PasswordUtil
import edu.hm.cs.animation.server.user.dao.UserDAO
import edu.hm.cs.animation.server.user.model.User
import edu.hm.cs.animation.server.util.rest.CRUDController
import io.javalin.http.Context
import javalinjwt.JavalinJWT

/**
 * REST Controller handling user matters.
 */
object UserController : CRUDController {

    /**
     * Path the user controller is reachable under.
     */
    const val PATH = "user"

    /**
     * Imaginary user id -> In that case the server should fetch the currently
     * authenticated user.
     */
    private const val AUTHENTICATED_ID = -1L

    /**
     * CRUDController to get users from.
     */
    private val userDAO = UserDAO()

    /**
     * Create a user.
     */
    override fun create(ctx: Context) {
        val user = ctx.bodyValidator<User>().get()

        user.id = null // For safety reasons

        if (user.name.isEmpty()) {
            throw Exception("Cannot create user with empty user name.")
        }

        // Encode password
        user.passwordSalt = PasswordUtil.getSalt(PasswordUtil.DEFAULT_SALT_LENGTH)
        user.password = PasswordUtil.securePassword(user.password!!, user.passwordSalt!!)

        ctx.json(userDAO.createUser(user))
    }

    /**
     * Read a user.
     */
    override fun read(ctx: Context) {
        var id = ctx.pathParam("id").toLong()

        // If ID == Authenticated_ID -> fetch currently authenticated user.
        if (id == AUTHENTICATED_ID) {
            id = getAuthenticatedUserId(ctx)
        }

        ctx.json(userDAO.findUser(id))
    }

    /**
     * Reads all users.
     */
    override fun readAll(ctx: Context) {
        ctx.json(userDAO.findAllUsers())
    }

    /**
     * Update a user.
     */
    override fun update(ctx: Context) {
        val user = ctx.bodyValidator<User>().get()

        // Encode password (if it will be updated)
        if (user.password != null) {
            user.passwordSalt = PasswordUtil.getSalt(PasswordUtil.DEFAULT_SALT_LENGTH)
            user.password = PasswordUtil.securePassword(user.password!!, user.passwordSalt!!)
        }

        userDAO.updateUser(user)
    }

    /**
     * Delete a user.
     */
    override fun delete(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        userDAO.removeUser(id)
    }

    private fun getAuthenticatedUserId(ctx: Context): Long {
        val decodedJwt = JavalinJWT.getDecodedFromContext(ctx)
        return decodedJwt.getClaim("id").asLong()
    }

}
