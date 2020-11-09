package edu.hm.cs.animation.server.yaars.vote

import edu.hm.cs.animation.server.util.LevenshteinDistanceCalculator
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPOpenPollSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.answer.dao.OpenAnswerDAO
import edu.hm.cs.animation.server.yaars.poll.answer.model.OpenAnswer
import edu.hm.cs.animation.server.yaars.poll.dao.OpenPollDAO
import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll
import io.javalin.http.Context
import kotlin.math.round

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

        val text = ctx.body<Map<String, String>>()["text"] ?: run {
            ctx.status(400)
            error("No body with property text!!")
        }

        // checks if this specific poll already has an answer with text = text or if the levenshtein distance
        // is less than 1/5 of the length of the string -> in that case the two strings are viewed as equal and
        // the vote counts towards the string that's already in the database
        val filter = { openAnswer: OpenAnswer ->
            val threshold = round(1.0 / 5.0 * openAnswer.text.toLowerCase().length.toDouble())
            val levenshteinDistance = LevenshteinDistanceCalculator
                    .calculateSimilarity(text.toLowerCase(), openAnswer.text.toLowerCase())

            openAnswer.text.toLowerCase() == text.toLowerCase() || levenshteinDistance <= threshold
        }

        // votes for the existent answer or creates a new answer
        when (val possibleExistentAnswer = poll.replies.find(filter)) {
            null -> {
                val newAnswer = openAnswerDAO
                        .create(OpenAnswer(text = text, timesMentioned = 1, answerId = 0))
                poll.replies.add(newAnswer)
                openPollDAO.update(poll)
            }
            else -> openAnswerDAO.vote(possibleExistentAnswer.answerId)
        }
        STOMPOpenPollSubscriptionManager.notifyAboutChange(openPollDAO.find(id))
    }
}
