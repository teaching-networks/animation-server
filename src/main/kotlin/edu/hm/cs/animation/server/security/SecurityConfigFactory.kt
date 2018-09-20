package edu.hm.cs.animation.server.security

import edu.hm.cs.animation.server.security.authenticator.UserPasswordAuthenticator
import edu.hm.cs.animation.server.security.authorizer.AdminAuthorizer
import edu.hm.cs.animation.server.security.client.NoErrorDirectBasicAuthClient
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.config.ConfigFactory
import org.pac4j.http.client.direct.HeaderClient
import org.pac4j.javalin.DefaultHttpActionAdapter
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import java.time.Duration

/**
 * All authorization/authentication matters are defined here.
 * See Pac4j documentation for more details.
 */
class SecurityConfigFactory(private val jwtSalt: String) : ConfigFactory {

    override fun build(vararg parameters: Any?): Config {
        val authenticator = UserPasswordAuthenticator("admin", "admin", 10, Duration.ofMinutes(30))

        // Direct basic authentication for initially retrieving JSON Web Token (Login)
        val directBasicAuthenticationClient = NoErrorDirectBasicAuthClient(authenticator)

        val headerClient = HeaderClient("Authorization", JwtAuthenticator(SecretSignatureConfiguration(jwtSalt)))

        // List of clients that can be used within the application.
        val clients = Clients(directBasicAuthenticationClient, headerClient)

        val config = Config(clients)
        config.addAuthorizer("admin", AdminAuthorizer())
        config.httpActionAdapter = DefaultHttpActionAdapter()

        return config
    }

}