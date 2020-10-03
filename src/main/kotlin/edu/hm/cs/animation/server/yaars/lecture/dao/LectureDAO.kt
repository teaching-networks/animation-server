/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.lecture.dao

import edu.hm.cs.animation.server.util.PersistenceUtil
import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import edu.hm.cs.animation.server.yaars.poll.model.YaarsPoll

/**
 * A DAO which manages the Lecture Entity.
 */
class LectureDAO {

    /**
     * Creates a new Lecture
     * @param lecture the new Lecture.
     */
    fun create(lecture: Lecture): Lecture {
        return PersistenceUtil.transaction {
            it.persist(lecture)

            return@transaction lecture
        }
    }

    /**
     * Finds a lecture by id.
     * @param id of the searched lecture.
     */
    fun find(id: Long): Lecture {
        return PersistenceUtil.transaction {
            return@transaction it.find(Lecture::class.java, id)
        }
    }

    /**
     * Returns all lectures.
     */
    fun findAll(): List<Lecture> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT l FROM Lecture l", Lecture::class.java).resultList!!
        }
    }

    /**
     * Returns all active polls for a certain lecture id.
     * @param id of the lecture.
     */
    fun getAllActiveForLectureId(id: Long): List<YaarsPoll> {
        val openPolls = PersistenceUtil.transaction {
            return@transaction it
                    .createQuery("SELECT p from Poll p WHERE p.active = True AND p.lecture.id = $id", YaarsPoll::class.java)
                    .resultList!!
        }
        openPolls.addAll(
                PersistenceUtil.transaction {
                    return@transaction it
                            .createQuery("SELECT p from OpenQuestionPoll p WHERE p.active = True AND p.lecture.id = $id", YaarsPoll::class.java)
                            .resultList!!
                }
        )
        return openPolls
    }

    /**
     * Updates a lecture.
     * @param lecture lecture that should be updated.
     */
    fun update(lecture: Lecture) {
        PersistenceUtil.transaction {
            val changedLecture = it.find(Lecture::class.java, lecture.id)
            changedLecture.name = lecture.name
            it.merge(changedLecture)
        }
    }

    /**
     * Removes a lecture.
     * @param id of the lecture.
     */
    fun remove(id: Long) {
        PersistenceUtil.transaction {
            val removedLecture = it.find(Lecture::class.java, id)
            it.remove(removedLecture)
        }
    }
}
