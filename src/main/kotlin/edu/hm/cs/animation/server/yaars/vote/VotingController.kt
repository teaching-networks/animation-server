package edu.hm.cs.animation.server.yaars.vote

import edu.hm.cs.animation.server.util.stomp.STOMPFrameBuilder
import edu.hm.cs.animation.server.util.stomp.STOMPMethod
import edu.hm.cs.animation.server.util.stomp.STOMPParser
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
            ctx.status(200)
        } else {
            ctx.status(400)
        }
    }

    // Just for demonstration.
    fun onMessage(ctx: WsMessageContext) {
        val request = STOMPParser.parseSTOMPRequestFromContext(ctx)
        when (request.method) {
            STOMPMethod.SUBSCRIBE -> {
                val builder = STOMPFrameBuilder()
                        .setMethod(STOMPMethod.MESSAGE)
                        .setHeader(mapOf("subscription" to request.header["id"]!!, "destination" to request.header["destination"]!!))
                        .setBody("Hallo")
                ctx.send(STOMPParser.writeSTOMPResponseFromFrame(builder.build()))
            }
            else -> print("Other than SUBSCRIBE")
        }
    }
}
