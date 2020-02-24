package edu.hm.cs.animation.server.YAARS.lecture.DAO

import edu.hm.cs.animation.server.YAARS.lecture.model.Lecture
import edu.hm.cs.animation.server.util.PersistenceUtil

class LectureDAO {

    fun create(lecture: Lecture): Lecture {
        return PersistenceUtil.transaction {
            it.persist(lecture)

            return@transaction lecture
        }
    }

    fun find(id: Long): Lecture {
        return PersistenceUtil.transaction {
            return@transaction it.find(Lecture::class.java, id)
        }
    }

    fun findAll(): List<Lecture> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT l FROM Lecture l", Lecture::class.java).resultList!!
        }
    }

    fun update(lecture: Lecture) {
        PersistenceUtil.transaction {
            val changedLecture = it.find(Lecture::class.java, lecture.id)
            changedLecture.name = lecture.name
            it.merge(changedLecture)
        }
    }

    fun remove(id: Long) {
        PersistenceUtil.transaction {
            val removedLecture = it.find(Lecture::class.java, id)
            it.remove(removedLecture)
        }
    }
}
