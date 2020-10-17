package edu.hm.cs.animation.server.yaars.poll.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll
import java.time.LocalDateTime


class OpenPollDAO {

    /**
     * Creates a new poll in the database.
     * @param poll that should be created.
     */
    fun create(poll: OpenQuestionPoll): OpenQuestionPoll {
        return PersistenceUtil.transaction {
            for (elem in poll.replies) {
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
    fun find(id: Long): OpenQuestionPoll {
        return PersistenceUtil.transaction {
            return@transaction it.find(OpenQuestionPoll::class.java, id)
        }
    }

    /**
     * Returns all polls from the Database.
     */
    fun findAll(): List<OpenQuestionPoll> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT p FROM OpenQuestionPoll p",
                    OpenQuestionPoll::class.java).resultList!!
        }
    }

    /**
     * Updates a specific poll.
     * @param poll the updated poll.
     */
    fun update(poll: OpenQuestionPoll) {
        PersistenceUtil.transaction {
            val updatedPoll = it.find(OpenQuestionPoll::class.java, poll.id)
            updatedPoll.active = poll.active
            updatedPoll.replies = poll.replies
            updatedPoll.lecture = poll.lecture
            updatedPoll.question = poll.question

            // fixes related poll field
            for (answer in updatedPoll.replies) {
                answer.relatedPoll = poll
            }

            it.merge(updatedPoll)
        }
    }

    /**
     * Removes a specific poll.
     * @param id of the poll.
     */
    fun remove(id: Long) {
        PersistenceUtil.transaction {
            val poll = it.find(OpenQuestionPoll::class.java, id)

            it.remove(poll)
        }
    }

    /**
     * Changes the activity status of a poll.
     * @param id of the poll.
     * @param status the new activity status.
     */
    fun setActive(id: Long, status: Boolean): OpenQuestionPoll {
        return PersistenceUtil.transaction {
            val poll = it.find(OpenQuestionPoll::class.java, id)
            poll.active = status

            if (status) {
                poll.timeStarted = LocalDateTime.now()
            } else {
                poll.timeStarted = null
            }

            return@transaction it.merge(poll)
        }
    }
}
