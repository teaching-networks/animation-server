/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.stomp

import io.javalin.websocket.WsMessageContext

/**
 * Decodes a STOMP Request send by a websocket to an instance of the class StompRequest.
 * The Decoder is used in every "before" part of a websocket connection and therefore called
 * before every request to any websocket endpoint.
 */
object STOMPParser {

    /**
     * Parses a STOMP request from the pure text of the {@link WsMessageContext} message method to a representative
     * STOMP Frame Object.
     * @param ctx the WsMessageContext.
     * @return STOMPFrame Object.
     */
    fun parseSTOMPRequestFromContext(ctx: WsMessageContext): STOMPFrame {
        val messageLines = ctx.message().split("\n")
        val builder = STOMPFrameBuilder()

        builder.setMethod(STOMPMethod.valueOf(messageLines[0]))

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
        while (index < messageLines.size) {
            body += messageLines[index]
            index++
        }
        return builder.setBody(body).build()
    }

    /**
     * Generates a STOMP Response in form of a String from a STOMP Frame Object.
     * @param responseFrame frame that should be converted.
     * @return String containing a valid STOMP response.
     */
    fun writeSTOMPResponseFromFrame(responseFrame: STOMPFrame): String {
        val response = StringBuilder().appendln(responseFrame.method.toString())

        for ((key, value) in responseFrame.header) {
            response.appendln("$key:$value")
        }
        response.appendln()

        responseFrame.body?.let {
            response.appendln(it)
        }
        response.append('\u0000')
        return response.toString()
    }
}
