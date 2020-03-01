package edu.hm.cs.animation.server.yaars.poll

import edu.hm.cs.animation.server.util.rest.CRUDController
import edu.hm.cs.animation.server.yaars.poll.dao.PollDAO
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import io.javalin.http.Context

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
}