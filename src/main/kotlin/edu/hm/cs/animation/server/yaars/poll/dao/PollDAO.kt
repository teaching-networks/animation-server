package edu.hm.cs.animation.server.yaars.poll.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.poll.model.Poll

class PollDAO {

    fun create(poll: Poll): Poll {
        return PersistenceUtil.transaction {
            for (elem in poll.answers) {
                elem.relatedPoll = poll
            }
            it.persist(poll)
            return@transaction poll
        }
    }

    fun find(id: Long): Poll {
        return PersistenceUtil.transaction {
            return@transaction it.find(Poll::class.java, id)
        }
    }

    fun findAll(): List<Poll> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT p FROM Poll p", Poll::class.java).resultList!!
        }
    }

    fun update(poll: Poll) {
        PersistenceUtil.transaction {
            val updatedPoll = it.find(Poll::class.java, poll.id)
            updatedPoll.active = poll.active
            updatedPoll.answers = poll.answers
            updatedPoll.lecture = poll.lecture
            updatedPoll.question = poll.question

            it.merge(updatedPoll)
        }
    }

    fun remove(id: Long) {
        PersistenceUtil.transaction {
            val poll = it.find(Poll::class.java, id)

            it.remove(poll)
        }
    }

    fun setActive(id: Long) {
        PersistenceUtil.transaction {
            val poll = it.find(Poll::class.java, id)
            poll.active = true

            it.merge(poll)
        }
    }
}
