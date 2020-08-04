/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.vote

import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPPollSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.answer.dao.AnswerDAO
import edu.hm.cs.animation.server.yaars.poll.dao.PollDAO
import io.javalin.http.Context
import io.javalin.websocket.WsMessageContext

object VotingController {

    const val PATH = "vote"

    private val pollDAO = PollDAO()
    private val answerDAO = AnswerDAO()

    /**
     * Votes for a Poll. Needs the id of the poll as path parameter and the body must be in form:
     * "id": x where x stands for the answerId of the question you want to vote for
     */
    fun vote(ctx: Context) {
        val id = ctx.pathParam("idP").toLong()
        val poll = pollDAO.find(id)
        val votedAnswerId = ctx.pathParam("idA").toLong()

        if (poll.active) {
            answerDAO.vote(votedAnswerId)
            STOMPPollSubscriptionManager.notifyAboutChange(pollDAO.find(id))
            ctx.status(200)
        } else {
            ctx.status(400)
        }
    }

    /**
     * Votes for a Poll. Does the same as {@link vote} above but with a Websocket Context.
     */
    fun voteWs(ctx: WsMessageContext) {
        val id = ctx.pathParam("idP").toLong()
        val poll = pollDAO.find(id)
        val votedAnswerId = ctx.pathParam("idA").toLong()

        if (poll.active) {
            answerDAO.vote(votedAnswerId)
            STOMPPollSubscriptionManager.notifyAboutChange(pollDAO.find(id))
        }
    }
}
