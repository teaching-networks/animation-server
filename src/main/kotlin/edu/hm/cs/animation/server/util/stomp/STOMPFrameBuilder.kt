/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.stomp

/**
 * A Builder for a STOMP request.
 */
class STOMPFrameBuilder {
    var method: STOMPMethod? = null
    var body: String? = null
    var header: Map<String, String>? = null

    fun setMethod(method: STOMPMethod): STOMPFrameBuilder {
        this.method = method
        return this
    }

    fun setBody(body: String?): STOMPFrameBuilder {
        this.body = body
        return this
    }

    fun setHeader(header: Map<String, String>): STOMPFrameBuilder {
        this.header = header
        return this
    }

    fun build(): STOMPFrame {
        return STOMPFrame(method!!, body, header!!)
    }
}
