package edu.hm.cs.animation.server.yaars.lecture

import edu.hm.cs.animation.server.util.rest.CRUDController
import edu.hm.cs.animation.server.yaars.lecture.dao.LectureDAO
import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import io.javalin.http.Context

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
}
