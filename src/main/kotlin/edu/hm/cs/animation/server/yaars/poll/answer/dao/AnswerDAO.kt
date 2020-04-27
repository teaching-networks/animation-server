package edu.hm.cs.animation.server.yaars.poll.answer.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.poll.answer.model.Answer

/**
 * DAO class which manages the Answer Entity.
 */
class AnswerDAO {

    /**
     * Votes for a specific answer, which means adding 1 to the current timesVoted counter.
     * @param id that should be voted for.
     */
    fun vote(id: Long) {
        return PersistenceUtil.transaction {
            val answer = it.find(Answer::class.java, id)

            answer.timesVoted += 1
            it.merge(answer)
        }
    }
}
