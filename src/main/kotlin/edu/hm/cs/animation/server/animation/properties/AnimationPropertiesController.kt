package edu.hm.cs.animation.server.animation.properties

import edu.hm.cs.animation.server.animation.properties.dao.AnimationPropertyDAO
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

/**
 * REST Controller handling animation property communication.
 */
object AnimationPropertiesController {

    /**
     * Path the controller is reachable under.
     */
    const val PATH = "animation/property"

    /**
     * DAO to get properties from.
     */
    private val propertyDAO = AnimationPropertyDAO()

    fun getProperties(ctx: Context) {
        val animationId = ctx.pathParam("animationid").toLong()
        val locale = ctx.pathParam("locale")

        val key = ctx.queryParam("key")

        if (key == null) {
            ctx.json(propertyDAO.findValues(animationId, locale))
        } else {
            val result = propertyDAO.findValue(animationId, locale, key)
            if (result == null) {
                ctx.status(HttpStatus.NOT_FOUND_404)
            } else {
                ctx.json(result)
            }
        }
    }

    fun setValue(ctx: Context) {
        val animationId = ctx.pathParam("animationid").toLong()
        val locale = ctx.pathParam("locale")

        val key = ctx.queryParam("key")!!

        val value = ctx.body()

        propertyDAO.setValue(animationId, locale, key, value)

        ctx.status(HttpStatus.OK_200)
    }

}