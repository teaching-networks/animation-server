package edu.hm.cs.animation.server.yaars.poll.answer.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.poll.answer.model.Answer

class AnswerDAO {

    fun vote(id: Long) {
        return PersistenceUtil.transaction {
            val answer = it.find(Answer::class.java, id)

            answer.timesVoted += 1
            it.merge(answer)
        }
    }
}
