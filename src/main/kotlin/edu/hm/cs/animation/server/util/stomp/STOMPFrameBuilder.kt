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
