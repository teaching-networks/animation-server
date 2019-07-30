/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.cmdargs

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

/**
 * Here go all command line arguments the application can evaluate.
 */
class CMDLineArgumentParser(parser: ArgParser) {

    val debug by parser.flagging("-d", "--debug", help = "Enable debug mode (Useful for development)")

    val port by parser.storing("-p", "--port", help = "Port the server should run on") { toInt() }

    val jwtSalt by parser.storing("-S", "--jwt-salt", help = "Salt used for generating/validating JSON Web Tokens")

    val corsEnabledOrigin by parser.storing("-C", "--cors-enabled-origin", help = "Origin for which CORS is enabled").default("")

    val keystorePath by parser.storing("-K", "--keystore-path", help = "Path to keystore to use").default("")

    val keystorePassword by parser.storing("-P", "--keystore-password", help = "Password to use for the keystore").default("")

}
