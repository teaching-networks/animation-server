package edu.hm.cs.animation.server

import com.xenomachina.argparser.ArgParser
import edu.hm.cs.animation.server.animation.AnimationController
import edu.hm.cs.animation.server.security.AuthController
import edu.hm.cs.animation.server.security.CORSSecurityHandler
import edu.hm.cs.animation.server.security.SecurityConfigFactory
import edu.hm.cs.animation.server.user.UserController
import edu.hm.cs.animation.server.util.cmdargs.CMDLineArgumentParser
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.eclipse.jetty.http.HttpMethod
import org.pac4j.core.context.HttpConstants
import org.pac4j.javalin.SecurityHandler

/**
 * Entry point for the server.
 *
 * @see CMDLineArgumentParser for available command line options
 */
fun main(args: Array<String>) {
    ArgParser(args).parseInto(::CMDLineArgumentParser).run {
        // Set up security configuration of pac4j
        val securityConfig = SecurityConfigFactory(jwtSalt).build()

        // Create the Javalin server
        val app = Javalin.create().apply {
            port(port)

            if (debug) {
                enableDebugLogging()
            }

            if (corsEnabledOrigin.isNotEmpty()) {
                enableCorsForOrigin(corsEnabledOrigin)
            } else if (debug) {
                enableCorsForAllOrigins()
            }
        }.start()

        // Here go all routes!
        app.routes {
            before("*") { ctx -> ctx.header(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true") }

            // Authentication controller
            before(AuthController.PATH, CORSSecurityHandler(SecurityHandler(securityConfig, "NoErrorDirectBasicAuthClient"))) // Basic authentication
            path(AuthController.PATH) {
                get { ctx -> AuthController.generateJWT(ctx, jwtSalt) }
            }

            path("api") {

                // API test
                path("hello") {
                    get { ctx -> ctx.result("Hello World") }
                }

                // User controller
                before(UserController.PATH + "/*", CORSSecurityHandler(SecurityHandler(securityConfig, "HeaderClient")))
                path(UserController.PATH) {
                    post(UserController::create)
                    get(UserController::readAll)
                    patch(UserController::update)

                    path(":id") {
                        get(UserController::read)
                        delete(UserController::delete)
                    }
                }

                // Animation controller
                before(AnimationController.PATH + "/*", CORSSecurityHandler(SecurityHandler(securityConfig, "HeaderClient"), HttpMethod.GET))
                path(AnimationController.PATH) {
                    post(AnimationController::create)
                    get(AnimationController::readAll)
                    patch(AnimationController::update)

                    path(":id") {
                        get(AnimationController::read)
                        delete(AnimationController::delete)
                    }
                }

            }
        }
    }
}
