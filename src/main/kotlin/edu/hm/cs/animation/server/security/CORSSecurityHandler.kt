/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security

import io.javalin.Context
import io.javalin.Handler
import org.eclipse.jetty.http.HttpMethod
import org.pac4j.core.context.HttpConstants
import org.pac4j.javalin.Pac4jContext
import org.pac4j.javalin.SecurityHandler

/**
 * Security handler which does not check OPTIONS HTTP Requests because they do not need to
 * be checked.
 */
class CORSSecurityHandler(private val securityHandler: SecurityHandler, private vararg val excludeMethods: HttpMethod) : Handler {

    override fun handle(ctx: Context) {
        if (!HttpConstants.HTTP_METHOD.OPTIONS.name.equals(ctx.method(), ignoreCase = true)) {
            if (excludeMethods.isNotEmpty()) {
                for (method in excludeMethods) {
                    if (ctx.method().equals(method.name, ignoreCase = true)) {
                        return;
                    }
                }
            }

            securityHandler.handle(ctx);
        }
    }

}
