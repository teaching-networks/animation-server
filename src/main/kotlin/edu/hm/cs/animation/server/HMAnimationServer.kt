package edu.hm.cs.animation.server

import com.xenomachina.argparser.ArgParser
import edu.hm.cs.animation.server.animation.AnimationController
import edu.hm.cs.animation.server.security.AuthController
import edu.hm.cs.animation.server.security.CORSSecurityHandler
import edu.hm.cs.animation.server.security.SecurityConfigFactory
import edu.hm.cs.animation.server.user.UserController
import edu.hm.cs.animation.server.util.cmdargs.CMDLineArgumentParser
import edu.hm.cs.animation.server.util.file.FileWatcher
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.pac4j.core.context.HttpConstants
import org.pac4j.javalin.SecurityHandler
import java.net.URI
import java.nio.file.Paths

class HMAnimationServer {

    fun start(args: Array<String>) {
        ArgParser(args).parseInto(::CMDLineArgumentParser).run {
            // Create the Javalin server
            val app = Javalin.create().apply {
                server {
                    // Create custom Jetty server
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
                    enableDebugLogging()
                }

                if (corsEnabledOrigin.isNotEmpty()) {
                    enableCorsForOrigin(corsEnabledOrigin)
                } else if (debug) {
                    enableCorsForAllOrigins()
                }
            }.start()

            setupRoutes(app, jwtSalt)
        }
    }

    private fun setupSslContextFactory(keystorePath: String, keystorePassword: String): SslContextFactory {
        val sslContextFactory = SslContextFactory()

        sslContextFactory.keyStorePath = keystorePath
        sslContextFactory.setKeyStorePassword(keystorePassword)

        val pathToKeystore = Paths.get(URI.create(sslContextFactory.keyStorePath))
        FileWatcher.onFileChange(pathToKeystore, Runnable { sslContextFactory.reload { _ -> println("Certificates reloaded") } })

        return sslContextFactory
    }

    private fun setupRoutes(app: Javalin, jwtSalt: String) {
        // Set up security configuration of pac4j
        val securityConfig = SecurityConfigFactory(jwtSalt).build()

        app.routes {
            ApiBuilder.before("*") { ctx -> ctx.header(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true") }

            // Authentication controller
            ApiBuilder.before(AuthController.PATH, CORSSecurityHandler(SecurityHandler(securityConfig, "NoErrorDirectBasicAuthClient"))) // Basic authentication
            ApiBuilder.path(AuthController.PATH) {
                ApiBuilder.get { ctx -> AuthController.generateJWT(ctx, jwtSalt) }
            }

            ApiBuilder.path("api") {

                // API test
                ApiBuilder.path("hello") {
                    ApiBuilder.get { ctx -> ctx.result("Hello World") }
                }

                // User controller
                ApiBuilder.before(UserController.PATH + "/*", CORSSecurityHandler(SecurityHandler(securityConfig, "HeaderClient")))
                ApiBuilder.path(UserController.PATH) {
                    ApiBuilder.post(UserController::create)
                    ApiBuilder.get(UserController::readAll)
                    ApiBuilder.patch(UserController::update)

                    ApiBuilder.path(":id") {
                        ApiBuilder.get(UserController::read)
                        ApiBuilder.delete(UserController::delete)
                    }
                }

                // Animation controller
                ApiBuilder.before(AnimationController.PATH + "/*", CORSSecurityHandler(SecurityHandler(securityConfig, "HeaderClient"), HttpMethod.GET))
                ApiBuilder.path(AnimationController.PATH) {
                    ApiBuilder.post(AnimationController::create)
                    ApiBuilder.get(AnimationController::readAll)
                    ApiBuilder.patch(AnimationController::update)

                    ApiBuilder.path(":id") {
                        ApiBuilder.get(AnimationController::read)
                        ApiBuilder.delete(AnimationController::delete)
                    }
                }

            }
        }
    }

}