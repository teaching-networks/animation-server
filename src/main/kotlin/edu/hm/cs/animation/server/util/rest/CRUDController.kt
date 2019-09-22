/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.rest

import io.javalin.http.Context

/**
 * Base interface for CRUD REST Controllers.
 */
interface CRUDController {

    /**
     * Create a resource.
     */
    fun create(ctx: Context)

    /**
     * Read a resource.
     */
    fun read(ctx: Context)

    /**
     * Read all items from a resource.
     */
    fun readAll(ctx: Context)

    /**
     * Update a resource.
     */
    fun update(ctx: Context)

    /**
     * Delete a resource.
     */
    fun delete(ctx: Context)

}
