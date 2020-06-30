/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

import edu.hm.cs.animation.server.settings.dao.SettingsDAO
import edu.hm.cs.animation.server.settings.model.Setting
import edu.hm.cs.animation.server.util.rest.CRUDController
import io.javalin.http.Context

/**
 * Rest controller for settings.
 */
object SettingsController : CRUDController {

    /**
     * Path the user controller is reachable under.
     */
    const val PATH = "settings"

    /**
     * DAO to get settings from.
     */
    private val settingsDAO = SettingsDAO()

    override fun create(ctx: Context) {
        val setting = ctx.bodyValidator<Setting>().get()

        if (setting.key.isEmpty()) {
            throw Exception("Settings needs to be non-empty")
        }

        ctx.json(settingsDAO.create(setting))
    }

    override fun read(ctx: Context) {
        val key = ctx.pathParam("key")

        ctx.json(settingsDAO.find(key))
    }

    override fun readAll(ctx: Context) {
        ctx.json(settingsDAO.findAll())
    }

    override fun update(ctx: Context) {
        val setting = ctx.bodyValidator<Setting>().get()

        ctx.json(settingsDAO.update(setting))
    }

    override fun delete(ctx: Context) {
        val key = ctx.pathParam("key")

        ctx.json(settingsDAO.remove(key))
    }

}
