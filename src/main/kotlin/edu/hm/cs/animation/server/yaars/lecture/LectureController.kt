/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.lecture

import edu.hm.cs.animation.server.util.rest.CRUDController
import edu.hm.cs.animation.server.util.stomp.STOMPMethod
import edu.hm.cs.animation.server.util.stomp.STOMPMethodVerificator.verifyForMethodOrNull
import edu.hm.cs.animation.server.util.stomp.STOMPParser
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPLectureSubscriptionManager
import edu.hm.cs.animation.server.yaars.lecture.dao.LectureDAO
import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import io.javalin.http.Context
import io.javalin.websocket.WsMessageContext

object LectureController : CRUDController {

    const val PATH = "lecture"
    private val lectureDAO = LectureDAO()

    override fun create(ctx: Context) {
        val lecture = ctx.bodyValidator<Lecture>().get()

        ctx.json(lectureDAO.create(lecture))
    }

    override fun read(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        ctx.json(lectureDAO.find(id))
    }

    override fun readAll(ctx: Context) {
        ctx.json(lectureDAO.findAll())
    }

    override fun update(ctx: Context) {
        val lecture = ctx.bodyValidator<Lecture>().get()

        lectureDAO.update(lecture)
    }

    override fun delete(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        lectureDAO.remove(id)
    }

    /**
     * Reacts on a websocket message. At this point we either get subscribe or unsubscribe methods, everything else
     * will be handled as a "Bad request".
     * @param ctx the WebsocketMessageContext.
     */
    fun onMessageSubscribe(ctx: WsMessageContext) {
        val clientRequest = STOMPParser.parseSTOMPRequestFromContext(ctx)

        if (clientRequest.method == STOMPMethod.SUBSCRIBE) {
            verifyForMethodOrNull(clientRequest, STOMPMethod.SUBSCRIBE, ctx)?.let { request ->
                // Add to the subscribers.
                val lectureId = ctx.pathParam("id").toLong()
                STOMPLectureSubscriptionManager.addSubscriber(ctx, request, lectureId)

                // Gets all the polls which are already active for this id and sends it to the subscribers.
                val activePolls = lectureDAO.getAllActiveForLectureId(lectureId)
                for (poll in activePolls) {
                    STOMPLectureSubscriptionManager.factorAndSendResponse(Triple(ctx, request, lectureId), poll)
                }
            }
        } else {
            verifyForMethodOrNull(clientRequest, STOMPMethod.UNSUBSCRIBE, ctx)?.let { request ->
                // Removes the related subscription.
                val lectureId = ctx.pathParam("id").toLong()
                STOMPLectureSubscriptionManager.removeAllSubscribersForId(lectureId, request.header["id"]!!)
            }
        }
    }
}
