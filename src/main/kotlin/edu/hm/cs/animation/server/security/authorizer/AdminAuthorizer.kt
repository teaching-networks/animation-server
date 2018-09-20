package edu.hm.cs.animation.server.security.authorizer

import org.pac4j.core.authorization.authorizer.ProfileAuthorizer
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.CommonProfile

/**
 * Simple authorizer which just gives you access to everything.
 */
class AdminAuthorizer : ProfileAuthorizer<CommonProfile>() {

    override fun isAuthorized(context: WebContext?, profiles: MutableList<CommonProfile>?): Boolean = isAnyAuthorized(context, profiles)

    override fun isProfileAuthorized(context: WebContext?, profile: CommonProfile?): Boolean = profile != null

}