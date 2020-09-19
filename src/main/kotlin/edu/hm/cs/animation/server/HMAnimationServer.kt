/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server

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
import edu.hm.cs.animation.server.yaars.lecture.LectureController
import edu.hm.cs.animation.server.yaars.poll.OpenPollController
import edu.hm.cs.animation.server.yaars.poll.PollController
import edu.hm.cs.animation.server.yaars.vote.OpenPollVotingController
import edu.hm.cs.animation.server.yaars.vote.PollVotingController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.http.Context
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
import java.util.*

class HMAnimationServer {

    var jwtProvider: JWTProvider? = null

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
                roleMapping[Roles.ADMINISTRATOR.name] = Roles.ADMINISTRATOR
                roleMapping[Roles.ANYONE.name] = Roles.ANYONE

                config.accessManager { handler, ctx, permittedRoles ->
                    // Check if current request is a Websocket upgrade request or not and set the right response header
                    if (ctx.req.getHeader("Upgrade") == "websocket" && ctx.req.getHeader("Sec-WebSocket-Protocol") != null) {
                        ctx.header("Sec-WebSocket-Protocol", "v10.stomp")
                    }
                    // if the request is also to a Admin endpoint, check the token
                    if (ctx.req.getHeader("Upgrade") == "websocket" && !permittedRoles.contains(Roles.ANYONE)) {
                        val claimedRoleString = getTokenFromQueryPath(ctx)
                                .flatMap(jwtProvider!!::validateToken)
                                .get().getClaim("role").asString()

                        val userRole: Role = roleMapping[claimedRoleString]!!
                        if (permittedRoles.contains(userRole)) {
                            handler.handle(ctx)
                        } else {
                            ctx.status(401).result("Unauthorized")
                        }
                    } else {
                        JWTAccessManager("role", roleMapping, Roles.ANYONE)
                                .manage(handler, ctx, permittedRoles)
                    }
                }
            }

            setupRoutes(app.start(), setupJWTProvider(jwtSalt))
        }
    }

    private fun getTokenFromQueryPath(ctx: Context): Optional<String> {
        return Optional.ofNullable(ctx.queryParam("Authorization"))
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

        this.jwtProvider = JWTProvider(algorithm, generator, verifier)
        return this.jwtProvider!!
    }

    private fun setupRoutes(app: Javalin, jwtProvider: JWTProvider) {
        val decodeHandler = JavalinJWT.createHeaderDecodeHandler(jwtProvider)
        app.before(decodeHandler)
        app.wsBefore { ws ->
            ws.onConnect { ctx -> ctx.send("CONNECTED\r\nversion:1.0\r\n\r\n\u0000") }
        }

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

                // YAARS controller
                ApiBuilder.path("yaars") {

                    // Lecture controller
                    ApiBuilder.path(LectureController.PATH) {
                        ApiBuilder.post(LectureController::create, roles(Roles.ADMINISTRATOR))
                        ApiBuilder.get(LectureController::readAll, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.patch(LectureController::update, roles(Roles.ADMINISTRATOR))

                        ApiBuilder.path(":id") {
                            ApiBuilder.get(LectureController::read, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                            ApiBuilder.delete(LectureController::delete, roles(Roles.ADMINISTRATOR))
                            ApiBuilder.ws({ ws -> ws.onMessage(LectureController::onMessageSubscribe) }, roles(Roles.ANYONE))
                        }
                    }

                    // Poll controller
                    ApiBuilder.path(PollController.PATH) {
                        ApiBuilder.post(PollController::create, roles(Roles.ADMINISTRATOR))
                        ApiBuilder.get(PollController::readAll, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.patch(PollController::update, roles(Roles.ADMINISTRATOR))
                        ApiBuilder.ws({ ws -> ws.onMessage(PollController::onMessageSend) }, roles(Roles.ADMINISTRATOR))

                        ApiBuilder.path(":id") {
                            ApiBuilder.get(PollController::read, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                            ApiBuilder.delete(PollController::delete, roles(Roles.ADMINISTRATOR))
                            ApiBuilder.ws({ ws -> ws.onMessage(PollController::onMessageSubscribe) }, roles(Roles.ADMINISTRATOR))
                        }

                        // Voting controller
                        ApiBuilder.path(PollVotingController.PATH) {
                            ApiBuilder.path(":idP/:idA") {
                                ApiBuilder.patch(PollVotingController::vote, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                                ApiBuilder.ws({ ws -> ws.onMessage(PollVotingController::voteWs) }, roles(Roles.ANYONE))
                            }
                        }
                    }

                    // Open Poll controller
                    ApiBuilder.path(OpenPollController.PATH) {
                        ApiBuilder.post(OpenPollController::create, roles(Roles.ADMINISTRATOR))
                        ApiBuilder.get(OpenPollController::readAll, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                        ApiBuilder.patch(OpenPollController::update, roles(Roles.ADMINISTRATOR))

                        ApiBuilder.path(":id") {
                            ApiBuilder.get(OpenPollController::read, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                            ApiBuilder.delete(OpenPollController::delete, roles(Roles.ADMINISTRATOR))
                        }

                        // Voting Controller
                        ApiBuilder.path(OpenPollVotingController.PATH) {
                            ApiBuilder.path(":idP") {
                                ApiBuilder.patch(OpenPollVotingController::vote, roles(Roles.ANYONE, Roles.ADMINISTRATOR))
                            }
                        }
                    }

                }

            }
        }
    }

}
