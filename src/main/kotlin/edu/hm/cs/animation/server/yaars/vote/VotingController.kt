package edu.hm.cs.animation.server.yaars.vote

import edu.hm.cs.animation.server.yaars.poll.answer.dao.AnswerDAO
import edu.hm.cs.animation.server.yaars.poll.dao.PollDAO
import io.javalin.http.Context

object VotingController {

    const val PATH = "vote"

    private val pollDAO = PollDAO()
    private val answerDAO = AnswerDAO()

    /**
     * Votes for a Poll. Needs the id of the poll as path parameter and the body must be in form:
     * "id": x where x stands for the answerId of the question you want to vote for
     */
    fun vote(ctx: Context) {
        val id = ctx.pathParam("id").toLong()
        val poll = pollDAO.find(id)
        val votedAnswerId = ctx.bodyValidator<Map<String, Int>>().get()

        if (poll.active) {
            answerDAO.vote((votedAnswerId["id"] ?: error("")).toLong())
            ctx.status(200)
        } else {
            ctx.status(400)
        }
    }
}
