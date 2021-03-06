/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.poll

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import edu.hm.cs.animation.server.util.PollCleanupUtil
import edu.hm.cs.animation.server.util.rest.CRUDController
import edu.hm.cs.animation.server.util.stomp.STOMPFrame
import edu.hm.cs.animation.server.util.stomp.STOMPFrameBuilder
import edu.hm.cs.animation.server.util.stomp.STOMPMethod
import edu.hm.cs.animation.server.util.stomp.STOMPMethodVerificator.verifyForMethodOrNull
import edu.hm.cs.animation.server.util.stomp.STOMPParser
import edu.hm.cs.animation.server.util.stomp.STOMPParser.parseSTOMPRequestFromContext
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPLectureSubscriptionManager
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPOpenPollSubscriptionManager
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPPollSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.dao.PollDAO
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import edu.hm.cs.animation.server.yaars.poll.model.YaarsPoll
import io.javalin.http.Context
import io.javalin.websocket.WsMessageContext

object PollController : CRUDController {
    const val PATH: String = "poll"

    private val pollDAO = PollDAO()

    override fun create(ctx: Context) {
        val poll = ctx.bodyValidator<Poll>().get()

        ctx.json(pollDAO.create(poll))
    }

    override fun read(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        ctx.json(pollDAO.find(id))
    }

    override fun readAll(ctx: Context) {
        ctx.json(pollDAO.findAll())
    }

    override fun update(ctx: Context) {
        val poll = ctx.bodyValidator<Poll>().get()

        pollDAO.update(poll)
    }

    override fun delete(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        pollDAO.remove(id)
    }

    /**
     * Reacts on a STOMP Send Message which represents the creation of a new poll.
     */
    fun onMessageSend(ctx: WsMessageContext) {
        verifyForMethodOrNull(parseSTOMPRequestFromContext(ctx), STOMPMethod.SEND, ctx)?.let { request ->
            val mapper = jacksonObjectMapper()
            val poll = mapper.readValue<Poll>(request.body!!)
            val createdPollId = pollDAO.create(poll).id

            val response = STOMPFrameBuilder()
                    .setMethod(STOMPMethod.RECEIPT)
                    .setHeader(
                            mapOf("receipt-id" to request.header["receipt"]!!, "poll-id" to createdPollId.toString()))
                    .build()

            ctx.send(STOMPParser.writeSTOMPResponseFromFrame(response))
        }
    }

    /**
     * Reacts to a STOMP Subscribe or Unsubscribe message.
     */
    fun onMessageSubscribe(ctx: WsMessageContext) {
        val clientRequest = parseSTOMPRequestFromContext(ctx)

        if (clientRequest.method == STOMPMethod.SUBSCRIBE) {
            verifyForMethodOrNull(clientRequest, STOMPMethod.SUBSCRIBE, ctx)?.let { request ->
                // Set the status of the poll to active
                val id = ctx.pathParam("id").toLong()
                val changedPoll = pollDAO.setActive(id, true)
                PollCleanupUtil.checkForOpenPollWithSimilarName(changedPoll)

                // Notify all clients (Lecture subscribers and frontend subscribers) about the new status and
                // add the poll subscriber to the list
                STOMPLectureSubscriptionManager.notifyAboutChange(changedPoll)
                STOMPPollSubscriptionManager.notifyAboutChange(changedPoll)
                STOMPPollSubscriptionManager.addSubscriber(ctx, request, id)
            }
        } else {
            verifyForMethodOrNull(clientRequest, STOMPMethod.UNSUBSCRIBE, ctx)?.let { request ->
                // Set the status to inactive
                val id = ctx.pathParam("id").toLong()
                val changedPoll = pollDAO.setActive(id, false)

                // Notify all clients (Lecture subscribers and frontend subscribers) about the new status and remove
                // the poll subscriber to the list
                STOMPLectureSubscriptionManager.notifyAboutChange(changedPoll)
                STOMPPollSubscriptionManager.notifyAboutChange(changedPoll)
                STOMPPollSubscriptionManager.removeAllSubscribersForId(id, request.header["id"]!!)
            }
        }
    }

    /**
     * Does nearly the same as onMessageSubscribe but without setting the requested poll to active. This endpoint is
     * just for a Frontend Client that only wants to be informed about the current state of the poll.
     */
    fun onFrontendMethodSubscribe(ctx: WsMessageContext) {
        val clientRequest = parseSTOMPRequestFromContext(ctx)
        if (clientRequest.method == STOMPMethod.SUBSCRIBE) {
            verifyForMethodOrNull(clientRequest, STOMPMethod.SUBSCRIBE, ctx)?.let { request ->
                // insert client into list of subscribers
                val id = ctx.pathParam("id").toLong()
                // get the current poll status and send it to the client
                val currentPoll = pollDAO.find(id)

                STOMPPollSubscriptionManager.factorAndSendResponse(Triple(ctx, request, id), currentPoll)
                STOMPPollSubscriptionManager.addSubscriber(ctx, request, id)
            }
        } else {
            verifyForMethodOrNull(clientRequest, STOMPMethod.UNSUBSCRIBE, ctx)?.let { request ->
                // Set the status to inactive
                val id = ctx.pathParam("id").toLong()

                STOMPPollSubscriptionManager.removeAllSubscribersForId(id, request.header["id"]!!)
            }
        }
    }
}
