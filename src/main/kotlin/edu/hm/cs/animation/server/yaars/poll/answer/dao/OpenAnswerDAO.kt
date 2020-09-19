package edu.hm.cs.animation.server.yaars.poll.answer.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.poll.answer.model.OpenAnswer

/**
 * DAO Class which manages the OpenAnswer Entity.
 */
class OpenAnswerDAO {

    /**
     * Creates a new Entity.
     */
    fun create(openAnswer: OpenAnswer): OpenAnswer {
        return PersistenceUtil.transaction {
            it.persist(openAnswer)
            return@transaction openAnswer
        }
    }

    /**
     * Votes for a given answer. Voting in this context means adding one to the timesMentioned field of the answer.
     */
    fun vote(id: Long) {
        return PersistenceUtil.transaction {
            val answer = it.find(OpenAnswer::class.java, id)
            answer.timesMentioned += 1
            it.merge(answer)
        }
    }
}
