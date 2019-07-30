/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security.client

import org.pac4j.core.client.DirectClient
import org.pac4j.core.context.HttpConstants
import org.pac4j.core.context.Pac4jConstants
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.UsernamePasswordCredentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.extractor.BasicAuthExtractor
import org.pac4j.core.profile.CommonProfile

/**
 * Default DirectBasicAuthClient returns Basic in HTTP Header so that the browser will
 * show a popup where it asks to enter credentials, we want the web application to handle that itself.
 */
class NoErrorDirectBasicAuthClient(usernamePasswordAuthenticator: Authenticator<UsernamePasswordCredentials>) : DirectClient<UsernamePasswordCredentials, CommonProfile>() {

    init {
        defaultAuthenticator(usernamePasswordAuthenticator)
    }

    override fun clientInit() {
        defaultCredentialsExtractor(BasicAuthExtractor())
    }

    override fun retrieveCredentials(context: WebContext): UsernamePasswordCredentials? {
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "BasicCustom realm=\"${Pac4jConstants.DEFAULT_REALM_NAME}\"")

        return super.retrieveCredentials(context)
    }

}
