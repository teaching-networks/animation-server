/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animation

import edu.hm.cs.animation.server.animation.dao.AnimationDAO
import edu.hm.cs.animation.server.animation.model.Animation
import edu.hm.cs.animation.server.util.rest.CRUDController
import io.javalin.http.Context

/**
 * REST Controller handling animation matters.
 */
object AnimationController : CRUDController {

    /**
     * Path the user controller is reachable under.
     */
    const val PATH = "animation"

    /**
     * DAO to get animations from.
     */
    private val animationDAO = AnimationDAO()

    override fun create(ctx: Context) {
        val animation = ctx.bodyValidator<Animation>().get()

        ctx.json(animationDAO.createAnimation(animation))
    }

    override fun read(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        ctx.json(animationDAO.findAnimation(id))
    }

    override fun readAll(ctx: Context) {
        ctx.json(animationDAO.findAllAnimations())
    }

    override fun update(ctx: Context) {
        val animation = ctx.bodyValidator<Animation>().get()

        animationDAO.updateAnimation(animation)
    }

    override fun delete(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        animationDAO.removeAnimation(id)
    }

}
