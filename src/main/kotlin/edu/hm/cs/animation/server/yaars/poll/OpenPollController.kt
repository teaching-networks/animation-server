package edu.hm.cs.animation.server.yaars.poll

import edu.hm.cs.animation.server.util.rest.CRUDController
import edu.hm.cs.animation.server.yaars.poll.dao.OpenPollDAO
import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll
import io.javalin.http.Context

object OpenPollController : CRUDController {
    const val PATH: String = "openpoll"

    private val openPollDAO = OpenPollDAO()

    override fun create(ctx: Context) {
        val poll = ctx.bodyValidator<OpenQuestionPoll>().get()

        ctx.json(openPollDAO.create(poll))
    }

    override fun read(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        ctx.json(openPollDAO.find(id))
    }

    override fun readAll(ctx: Context) {
        ctx.json(openPollDAO.findAll())
    }

    override fun update(ctx: Context) {
        val poll = ctx.bodyValidator<OpenQuestionPoll>().get()

        openPollDAO.update(poll)
    }

    override fun delete(ctx: Context) {
        val id = ctx.pathParam("id").toLong()

        openPollDAO.remove(id)
    }
}
