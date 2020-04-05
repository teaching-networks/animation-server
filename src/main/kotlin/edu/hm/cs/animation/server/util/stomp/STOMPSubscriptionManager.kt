package edu.hm.cs.animation.server.util.stomp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import io.javalin.websocket.WsMessageContext

object STOMPSubscriptionManager {
    private val subscribers: MutableList<Triple<WsMessageContext, STOMPFrame, Long>> = mutableListOf()

    fun addSubscriber(ctx: WsMessageContext, request: STOMPFrame, pollId: Long) =
            subscribers.add(Triple(ctx, request, pollId))

    fun notifyAboutChange(poll: Poll) {
        val mapper = jacksonObjectMapper()
        for (s in subscribers) {
            if (poll.id == s.third) {
                val response = STOMPFrameBuilder()
                        .setMethod(STOMPMethod.MESSAGE)
                        .setHeader(mapOf("subscription" to s.second.header["id"]!!, "destination" to s.second.header["destination"]!!))
                        .setBody(mapper.writeValueAsString(poll))
                        .build()

                s.first.send(STOMPParser.writeSTOMPResponseFromFrame(response))
            }
        }
    }
}
