package edu.hm.cs.animation.server.util.stomp

import io.javalin.websocket.WsMessageContext

/**
 * Decodes a STOMP Request send by a websocket to an instance of the class StompRequest.
 * The Decoder is used in every "before" part of a websocket connection and therefore called
 * before every request to any websocket endpoint.
 */
object STOMPDecoder {
    fun decodeSTOMPRequest(ctx: WsMessageContext): STOMPRequest {
        val messageLines = ctx.message().split("\n")
        val builder = STOMPRequestBuilder()

        builder.setMethod(STOMPMethod.stringToMethod(messageLines[0]))

        var index = 1
        var line = messageLines[index]
        val header = mutableMapOf<String, String>()
        while (line != "") {
            val keyValue = line.split(":")
            header[keyValue[0]] = keyValue[1]
            index++
            line = messageLines[index]
        }
        builder.setHeader(header)

        var body = ""
        while (messageLines[++index] != "\u0000") {
            body += messageLines[index]
        }
        return builder.setBody(body).build()
    }

    /**
     * A Builder for a STOMP request.
     */
    class STOMPRequestBuilder {
        var method: STOMPMethod? = null
        var body: String? = null
        var header: Map<String, String>? = null

        fun setMethod(method: STOMPMethod): STOMPRequestBuilder {
            this.method = method
            return this
        }

        fun setBody(body: String?): STOMPRequestBuilder {
            this.body = body
            return this
        }

        fun setHeader(header: Map<String, String>): STOMPRequestBuilder {
            this.header = header
            return this
        }

        fun build(): STOMPRequest {
            return STOMPRequest(method!!, body, header!!)
        }
    }
}
