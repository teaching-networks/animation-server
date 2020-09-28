package edu.hm.cs.animation.server.yaars.vote

import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPOpenPollSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.answer.dao.OpenAnswerDAO
import edu.hm.cs.animation.server.yaars.poll.answer.model.OpenAnswer
import edu.hm.cs.animation.server.yaars.poll.dao.OpenPollDAO
import io.javalin.http.Context

/**
 * Manages voting for a specific answer of an OpenPoll.
 */
object OpenPollVotingController {
    const val PATH = "vote"

    private val openPollDAO = OpenPollDAO()
    private val openAnswerDAO = OpenAnswerDAO()

    /**
     * Votes for an OpenPoll. Needs the id of the poll as path parameter and the body must be in form:
     * "text": x where x stands for the answer text.
     */
    fun vote(ctx: Context) {
        // fetches the poll
        val id = ctx.pathParam("idP").toLong()
        val poll = openPollDAO.find(id)

        // illegal request if the poll is inactive
        if (!poll.active) {
            ctx.status(400)
            return
        }

        val text = ctx.body<Map<String, String>>()["text"]

        // checks if this specific poll already has an answer with text = text
        val alreadyVoted = poll.answers.filter { openAnswer ->
            openAnswer.text.toLowerCase().equals(text?.toLowerCase())
        }.size == 1

        // if the set contains such an answer we vote for it, otherwise a new answer is created and the poll is updated
        if (alreadyVoted) {
            val answer = poll.answers.filter { openAnswer ->
                openAnswer.text.toLowerCase().equals(text?.toLowerCase())
            }[0].answerId
            openAnswerDAO.vote(answer)
        } else {
            val newAnswer = openAnswerDAO.create(OpenAnswer(text = text!!, timesMentioned = 1, answerId = 0))
            poll.answers.add(newAnswer)
            openPollDAO.update(poll)
        }
        STOMPOpenPollSubscriptionManager.notifyAboutChange(openPollDAO.find(id))
    }
}
