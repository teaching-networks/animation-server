package edu.hm.cs.animation.server.yaars.poll.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.poll.model.Poll

/**
 * DAO which manages the Poll Entity.
 */
class PollDAO {

    /**
     * Creates a new poll in the database.
     * @param poll that should be created.
     */
    fun create(poll: Poll): Poll {
        return PersistenceUtil.transaction {
            for (elem in poll.answers) {
                elem.relatedPoll = poll
            }
            it.persist(poll)
            return@transaction poll
        }
    }

    /**
     * Finds a specific poll.
     * @param id of the poll.
     */
    fun find(id: Long): Poll {
        return PersistenceUtil.transaction {
            return@transaction it.find(Poll::class.java, id)
        }
    }

    /**
     * Returns all polls from the Database.
     */
    fun findAll(): List<Poll> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT p FROM Poll p", Poll::class.java).resultList!!
        }
    }

    /**
     * Updates a specific poll.
     * @param poll the updated poll.
     */
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

    /**
     * Removes a specific poll.
     * @param id of the poll.
     */
    fun remove(id: Long) {
        PersistenceUtil.transaction {
            val poll = it.find(Poll::class.java, id)

            it.remove(poll)
        }
    }

    /**
     * Changes the activity status of a poll.
     * @param id of the poll.
     * @param status the new activity status.
     */
    fun setActive(id: Long, status: Boolean): Poll {
        return PersistenceUtil.transaction {
            val poll = it.find(Poll::class.java, id)
            poll.active = status

            return@transaction it.merge(poll)
        }
    }
}
