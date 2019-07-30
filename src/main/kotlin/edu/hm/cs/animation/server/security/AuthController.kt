/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security

import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.ProfileManager
import org.pac4j.javalin.Pac4jContext
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import java.util.*

/**
 * REST Controller handling authentication matters.
 */
object AuthController {

    const val PATH = "auth"

    /**
     * Generate and retrieve a JSON Web Token.
     */
    fun generateJWT(ctx: Context, jwtSalt: String) {
        val context: Pac4jContext = Pac4jContext(ctx)
        val profileManager = ProfileManager<CommonProfile>(context)
        val profile: Optional<CommonProfile> = profileManager.get(true)

        if (profile.isPresent) {
            val generator = JwtGenerator<CommonProfile>(SecretSignatureConfiguration(jwtSalt))
            var token = generator.generate(profile.get())

            ctx.result(token)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }
    }

}
