package edu.hm.cs.animation.server.yaars.vote

import edu.hm.cs.animation.server.util.LevenshteinDistanceCalculator
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

        val text = ctx.body<Map<String, String>>()["text"] ?: error("No body with property text!!")

        // checks if this specific poll already has an answer with text = text or if the levenshtein distance
        // is less than 1/5 of the length of the string -> in that case the two strings are viewed as equal and
        // the vote counts towards the string that's already in the database. this is only done if the poll answers
        // does not have multiple lines
        val alreadyVoted = poll.replies.filter { openAnswer ->
            val threshold = 1.0 / 5.0 * openAnswer.text.toLowerCase().length.toDouble()
            val levenshteinDistance = LevenshteinDistanceCalculator
                    .calculateSimilarity(text.toLowerCase(), openAnswer.text.toLowerCase())

            (openAnswer.text.toLowerCase() == text.toLowerCase() || levenshteinDistance <= threshold)

        }.size == 1

        // if the set contains such an answer we vote for it, otherwise a new answer is created and the poll is updated
        if (alreadyVoted && !poll.isMultilineAnswer) {
            val answer = poll.replies.filter { openAnswer ->
                openAnswer.text.toLowerCase() == text.toLowerCase()
            }[0].answerId
            openAnswerDAO.vote(answer)
        } else {
            val newAnswer = openAnswerDAO.create(OpenAnswer(text = text, timesMentioned = 1, answerId = 0))
            poll.replies.add(newAnswer)
            openPollDAO.update(poll)
        }
        STOMPOpenPollSubscriptionManager.notifyAboutChange(openPollDAO.find(id))
    }
}
