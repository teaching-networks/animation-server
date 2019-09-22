/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animation.properties

import edu.hm.cs.animation.server.animation.properties.dao.AnimationPropertyDAO
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

/**
 * REST Controller handling animation property communication.
 */
object AnimationPropertiesController {

    /**
     * Path the controller is reachable under.
     */
    const val PATH = "property"

    /**
     * DAO to get properties from.
     */
    private val propertyDAO = AnimationPropertyDAO()

    fun getProperties(ctx: Context) {
        val animationId = ctx.queryParam("animationid")
        val locale = ctx.queryParam("locale")
        val key = ctx.queryParam("key")

        if (locale != null && animationId != null && key != null) {
            val result = propertyDAO.findValue(animationId.toLong(), locale, key)
            if (result == null) {
                ctx.status(HttpStatus.NOT_FOUND_404)
            } else {
                ctx.json(result)
            }
        } else if (locale != null && animationId != null) {
            ctx.json(propertyDAO.findValuesByAnimationIdAndLocale(animationId.toLong(), locale))
        } else if (locale != null && key != null) {
            ctx.json(propertyDAO.findValuesByKeyAndLocale(locale, key))
        } else if (animationId != null && key != null) {
            ctx.json(propertyDAO.findValuesByAnimationIdAndKey(animationId.toLong(), key))
        } else if (locale != null) {
            ctx.json(propertyDAO.findValuesByLocale(locale))
        } else if (animationId != null) {
            ctx.json(propertyDAO.findValuesByAnimationId(animationId.toLong()))
        } else if (key != null) {
            ctx.json(propertyDAO.findValuesByKey(key))
        } else {
            ctx.json(propertyDAO.findAllValues())
        }
    }

    fun setValue(ctx: Context) {
        val animationId = ctx.queryParam("animationid")!!
        val locale = ctx.queryParam("locale")!!
        val key = ctx.queryParam("key")!!

        val value = ctx.body()

        propertyDAO.setValue(animationId.toLong(), locale, key, value)

        ctx.status(HttpStatus.OK_200)
    }

}
