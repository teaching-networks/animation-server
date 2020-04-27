package edu.hm.cs.animation.server.util.stomp.subscriptions

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import edu.hm.cs.animation.server.util.stomp.STOMPFrame
import edu.hm.cs.animation.server.util.stomp.STOMPFrameBuilder
import edu.hm.cs.animation.server.util.stomp.STOMPMethod
import edu.hm.cs.animation.server.util.stomp.STOMPParser
import io.javalin.websocket.WsMessageContext

abstract class STOMPSubscriptionManager<T> {
    protected val subscribers: MutableList<Triple<WsMessageContext, STOMPFrame, Long>> = mutableListOf()

    fun addSubscriber(ctx: WsMessageContext, request: STOMPFrame, objectId: Long): Boolean =
            subscribers.add(Triple(ctx, request, objectId))

    fun removeAllSubscribersForId(objectId: Long, subscriptionId: String): Boolean =
            subscribers.removeAll { triple ->
                triple.third == objectId && triple.second.header["id"]!! == subscriptionId
            }

    abstract fun notifyAboutChange(changedObject: T)

    fun factorAndSendResponse(subscriber: Triple<WsMessageContext, STOMPFrame, Long>, changedObject: T) {
        val mapper = jacksonObjectMapper()
        val response = STOMPFrameBuilder()
                .setMethod(STOMPMethod.MESSAGE)
                .setHeader(
                        mapOf("subscription" to subscriber.second.header["id"]!!,
                                "destination" to subscriber.second.header["destination"]!!))
                .setBody(mapper.writeValueAsString(changedObject))
                .build()

        subscriber.first.send(STOMPParser.writeSTOMPResponseFromFrame(response))
    }
}
