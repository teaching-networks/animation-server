/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animgroup

import edu.hm.cs.animation.server.animgroup.dao.AnimGroupDAO
import edu.hm.cs.animation.server.animgroup.model.AnimGroup
import edu.hm.cs.animation.server.util.rest.CRUDController
import io.javalin.http.Context

/**
 * CRUD Controller for animation groups.
 */
object AnimGroupController : CRUDController {

    /**
     * Path the user controller is reachable under.
     */
    const val PATH = "group"

    /**
     * DAO to get animation groups from.
     */
    private val animGroupDAO = AnimGroupDAO()

    override fun create(ctx: Context) {
        val group = ctx.bodyValidator<AnimGroup>().get()

        ctx.json(animGroupDAO.create(group))
    }

    override fun read(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        ctx.json(animGroupDAO.find(id))
    }

    override fun readAll(ctx: Context) {
        ctx.json(animGroupDAO.findAll())
    }

    override fun update(ctx: Context) {
        val group = ctx.bodyValidator<AnimGroup>().get()

        animGroupDAO.update(group)
    }

    override fun delete(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        animGroupDAO.remove(id)
    }

}
