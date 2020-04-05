package edu.hm.cs.animation.server.yaars.poll

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import edu.hm.cs.animation.server.util.rest.CRUDController
import edu.hm.cs.animation.server.util.stomp.STOMPFrameBuilder
import edu.hm.cs.animation.server.util.stomp.STOMPMethod
import edu.hm.cs.animation.server.util.stomp.STOMPMethodVerificator.verifyForMethodOrNull
import edu.hm.cs.animation.server.util.stomp.STOMPParser
import edu.hm.cs.animation.server.util.stomp.STOMPParser.parseSTOMPRequestFromContext
import edu.hm.cs.animation.server.util.stomp.STOMPSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.dao.PollDAO
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import io.javalin.http.Context
import io.javalin.websocket.WsMessageContext

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

    fun onMessageSend(ctx: WsMessageContext) {
        verifyForMethodOrNull(parseSTOMPRequestFromContext(ctx), STOMPMethod.SEND, ctx)?.let { request ->
            val mapper = jacksonObjectMapper()
            val poll = mapper.readValue<Poll>(request.body!!)
            val createdPollId = pollDAO.create(poll).id

            val response = STOMPFrameBuilder()
                    .setMethod(STOMPMethod.RECEIPT)
                    .setHeader(mapOf("receipt-id" to request.header["receipt"]!!, "poll-id" to createdPollId.toString()))
                    .build()

            ctx.send(STOMPParser.writeSTOMPResponseFromFrame(response))
        }
    }

    fun onMessageSubscribe(ctx: WsMessageContext) {
        verifyForMethodOrNull(parseSTOMPRequestFromContext(ctx), STOMPMethod.SUBSCRIBE, ctx)?.let { request ->
            val id = ctx.pathParam("id").toLong()
            pollDAO.setActive(id)
            STOMPSubscriptionManager.addSubscriber(ctx, request, id)
        }
    }
}
