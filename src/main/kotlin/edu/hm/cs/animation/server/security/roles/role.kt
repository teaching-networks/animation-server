/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.security.roles

import io.javalin.core.security.Role

enum class Roles : Role {
    ADMINISTRATOR,
    ANYONE
}
