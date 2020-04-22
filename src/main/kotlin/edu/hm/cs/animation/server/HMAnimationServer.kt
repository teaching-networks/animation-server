/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server

import SettingsController
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.xenomachina.argparser.ArgParser
import edu.hm.cs.animation.server.animation.AnimationController
import edu.hm.cs.animation.server.animation.properties.AnimationPropertiesController
import edu.hm.cs.animation.server.animgroup.AnimGroupController
import edu.hm.cs.animation.server.security.AuthController
import edu.hm.cs.animation.server.security.roles.Roles
import edu.hm.cs.animation.server.user.UserController
import edu.hm.cs.animation.server.user.model.User
import edu.hm.cs.animation.server.util.cmdargs.CMDLineArgumentParser
import edu.hm.cs.animation.server.util.file.FileWatcher
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import javalinjwt.JWTAccessManager
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import javalinjwt.JavalinJWT
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import java.net.URI
import java.nio.file.Paths

class HMAnimationServer {

    fun start(args: Array<String>) {
        ArgParser(args).parseInto(::CMDLineArgumentParser).run {
            // Create and configure Javalin.
            val app = Javalin.create { config ->
                config.server {
                    val server = Server()

                    if (keystorePath.isNotEmpty()) {
                        val sslContextFactory = setupSslContextFactory(keystorePath, keystorePassword)
                        val sslConnector = ServerConnector(server, sslContextFactory)
                        sslConnector.port = port

                        server.connectors = arrayOf<Connector>(sslConnector)
                    } else {
                        val connector = ServerConnector(server)
                        connector.port = port

                        server.connectors = arrayOf<Connector>(connector)
                    }

                    server
                }

                if (debug) {
                    config.enableDevLogging()
                }

                if (corsEnabledOrigin.isNotEmpty()) {
                    config.enableCorsForOrigin(corsEnabledOrigin)
                } else if (debug) {
                    config.enableCorsForAllOrigins()
                }

                // Setup access manager to handle JSON Web Token authentication
                val roleMapping = hashMapOf<String, Role>()
                roleMapping.put(Roles.ADMINISTRATOR.name, Roles.ADMINISTRATOR)
                roleMapping.put(Roles.ANYONE.name, Roles.ANYONE)
                config.accessManager(JWTAccessManager("role", roleMapping, Roles.ANYONE))
            }

            setupRoutes(app.start(), setupJWTProvider(jwtSalt))
        }
    }

    private fun setupSslContextFactory(keystorePath: String, keystorePassword: String): SslContextFactory {
        val sslContextFactory = SslContextFactory.Server()

        sslContextFactory.keyStorePath = keystorePath
        sslContextFactory.setKeyStorePassword(keystorePassword)

        val pathToKeystore = Paths.get(URI.create(sslContextFactory.keyStorePath))
        FileWatcher.onFileChange(pathToKeystore, Runnable {
            sslContextFactory.reload {
                println("Certificates reloaded")
            }
        })

        return sslContextFactory
    }

    private fun setupJWTProvider(salt: String): JWTProvider {
        val algorithm = Algorithm.HMAC256(salt)

        val generator = JWTGenerator { user: User, alg: Algorithm ->
            val token = JWT.create()
                    .withClaim("id", user.id)
                    .withClaim("name", user.name)
                    .withClaim("role", user.role.name)

            token.sign(alg)
        }

        val verifier = JWT.require(algorithm).build()

        return JWTProvider(algorithm, generator, verifier)
    }

    private fun setupRoutes(app: Javalin, jwtProvider: JWTProvider) {
        val decodeHandler = JavalinJWT.createHeaderDecodeHandler(jwtProvider)
        app.before(decodeHandler)

        app.routes {
            ApiBuilder.before("*") { ctx -> ctx.header("Access-Control-Allow-Credentials", "true") }
            ApiBuilder.after {
                it.contentType("application/json; charset=utf-8")
            }

            // Authentication controller
            ApiBuilder.path(AuthController.PATH) {
                ApiBuilder.get({ ctx -> AuthController.authenticate(ctx, jwtProvider) }, roles(Roles.ANYONE))
            }

            ApiBuilder.path("api") {

                // API test
                ApiBuilder.path("hello") {
                    ApiBuilder.get({ ctx -> ctx.result("Hello World") }, roles(Roles.ADMINISTRATOR))
                }

                // User controller
                ApiBuilder.path(UserController.PATH) {
                    ApiBuilder.post(UserController::create, roles(Roles.ADMINISTRATOR))
                    ApiBuilder.get(UserController::readAll, roles(Roles.ADMINISTRATOR))
                    ApiBuilder.patch(UserController::update, roles(Roles.ADMINISTRATOR))

                    ApiBuilder.path(":id") {
                        ApiBuilder.get(UserController::read, roles(Roles.ADMINISTRATOR))
                        ApiBuilder.delete(UserController::delete, roles(Roles.ADMINISTRATOR))
                    }
                }

                // Animation controller
                ApiBuilder.path(AnimationController.PATH) {
                    ApiBuilder.post(AnimationController::create, roles(Roles.ADMINISTRATOR))
                    ApiBuilder.get(AnimationController::readAll, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                    ApiBuilder.patch(AnimationController::update, roles(Roles.ADMINISTRATOR))

                    // Animation properties controller
                    ApiBuilder.path(AnimationPropertiesController.PATH) {
                        ApiBuilder.get(AnimationPropertiesController::getProperties,
                                roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.post(AnimationPropertiesController::setValue, roles(Roles.ADMINISTRATOR))
                    }
                    ApiBuilder.path(":id") {
                        ApiBuilder.get(AnimationController::read, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.delete(AnimationController::delete, roles(Roles.ADMINISTRATOR))
                    }
                }

                // Animation group controller
                ApiBuilder.path(AnimGroupController.PATH) {
                    ApiBuilder.post(AnimGroupController::create, roles(Roles.ADMINISTRATOR))
                    ApiBuilder.get(AnimGroupController::readAll, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                    ApiBuilder.patch(AnimGroupController::update, roles(Roles.ADMINISTRATOR))

                    ApiBuilder.path(":id") {
                        ApiBuilder.get(AnimGroupController::read, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.delete(AnimGroupController::delete, roles(Roles.ADMINISTRATOR))
                    }
                }

                // Settings controller
                ApiBuilder.path(SettingsController.PATH) {
                    ApiBuilder.get(SettingsController::readAll, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                    ApiBuilder.post(SettingsController::create, roles(Roles.ADMINISTRATOR))
                    ApiBuilder.patch(SettingsController::update, roles(Roles.ADMINISTRATOR))

                    ApiBuilder.path(":key") {
                        ApiBuilder.get(SettingsController::read, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.delete(SettingsController::delete, roles(Roles.ADMINISTRATOR))
                    }
                }

            }
        }
    }

}
