package edu.hm.cs.animation.server.util.stomp.subscriptions

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import edu.hm.cs.animation.server.util.stomp.STOMPFrame
import edu.hm.cs.animation.server.util.stomp.STOMPFrameBuilder
import edu.hm.cs.animation.server.util.stomp.STOMPMethod
import edu.hm.cs.animation.server.util.stomp.STOMPParser
import io.javalin.websocket.WsMessageContext

/**
 * A Class which represents the Interface of a STOMP Subscription Manager. It handels the subscription to a specified
 * resource and the notification in case of change.
 * @param T the type of Object the subscriber wants to be notified about.
 */
abstract class STOMPSubscriptionManager<T> {
    protected val subscribers: MutableList<Triple<WsMessageContext, STOMPFrame, Long>> = mutableListOf()

    /**
     * Adds a subscriber to the List of subscribers.
     * Each subscriber is represented by its MessageContext (to answer them later), the received, intital subscription
     * request and the object id to which they've subscribed.
     * @param ctx the message context.
     * @param request the initial subscribe request.
     * @param objectId the id of the object they've subscribed to.
     */
    fun addSubscriber(ctx: WsMessageContext, request: STOMPFrame, objectId: Long): Boolean =
            subscribers.add(Triple(ctx, request, objectId))

    /**
     * Removes all subscribers for a special resource.
     * @param objectId the id of the object they've subscribed to.
     * @param subscriptionId the id of the subscriber.
     */
    fun removeAllSubscribersForId(objectId: Long, subscriptionId: String): Boolean =
            subscribers.removeAll { triple ->
                triple.third == objectId && triple.second.header["id"]!! == subscriptionId
            }

    /**
     * Function that notifies all subscribers about a change.
     * @param changedObject the object all subscribers should be notified about.
     */
    abstract fun notifyAboutChange(changedObject: T)

    /**
     * Builds an answer Frame and sends it to a subscriber.
     * @param subscriber a subscriber represented by the Triple context, request and id.
     * @param changedObject the changed object.
     */
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
