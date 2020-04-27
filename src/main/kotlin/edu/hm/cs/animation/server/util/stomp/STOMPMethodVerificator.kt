package edu.hm.cs.animation.server.util.stomp

import io.javalin.websocket.WsMessageContext

/**
 * Base Object for handling protocol errors and verifying correct STOMP requests.
 */
object STOMPMethodVerificator {

    /**
     * Verifies if a given requests is correct concerning the wanted function. It also manages the error handling by
     * sending correct error frames back to the client.
     * @param request the incoming STOMP request.
     * @param wantedMethod the method the request should suit. E.g. a SUBSCRIBE request should have special bodies.
     * @param ctx the WsMessageContext.
     * @return either the correct STOMPFrame or null if anything is wrong.
     */
    fun verifyForMethodOrNull(request: STOMPFrame, wantedMethod: STOMPMethod, ctx: WsMessageContext): STOMPFrame? {
        if (request.method == STOMPMethod.CONNECT) return null

        val result: STOMPFrame?
        when (wantedMethod) {
            STOMPMethod.SEND -> {
                result = if (request.body == null) {
                    sendError("Can't find body", ctx)
                    null
                } else {
                    request
                }
            }
            STOMPMethod.SUBSCRIBE -> {
                result = if (request.header["destination"] == null || request.header["id"] == null) {
                    sendError("Bad request", ctx)
                    null
                } else {
                    request
                }
            }
            STOMPMethod.UNSUBSCRIBE -> {
                result = if (request.header["id"] == null) {
                    sendError("Bad request", ctx)
                    null
                } else {
                    request
                }
            }
            else -> {
                sendError("Bad request", ctx)
                result = null
            }
        }
        return result
    }

    /**
     * Sends an error to the client with a specified message.
     * @param message the message to be send.
     * @param ctx the WsMessageContext.
     */
    private fun sendError(message: String, ctx: WsMessageContext) {
        val response = STOMPFrameBuilder()
                .setMethod(STOMPMethod.ERROR)
                .setHeader(mapOf("message" to message))
                .build()
        ctx.send(STOMPParser.writeSTOMPResponseFromFrame(response))
    }
}
