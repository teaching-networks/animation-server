package edu.hm.cs.animation.server.animgroup.dao

import edu.hm.cs.animation.server.animgroup.model.AnimGroup
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object for animation groups.
 */
class AnimGroupDAO {

    fun create(animGroup: AnimGroup): AnimGroup {
        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        try {
            em.persist(animGroup)

            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }

        return animGroup;
    }

    fun find(id: Long): AnimGroup {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val group: AnimGroup = em.find(AnimGroup::class.java, id)

        transaction.commit()

        return group
    }

    fun findAll(): List<AnimGroup> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val groups: List<AnimGroup> = em.createQuery("SELECT a FROM AnimGroup a", AnimGroup::class.java).resultList!!

        transaction.commit()

        return groups
    }

    fun update(group: AnimGroup) {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val dbAnimGroup: AnimGroup = em.find(AnimGroup::class.java, group.id)

        dbAnimGroup.name = group.name
        dbAnimGroup.animationIds = group.animationIds

        try {
            em.merge(dbAnimGroup)
            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }
    }

    fun remove(id: Long) {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val group: AnimGroup = em.find(AnimGroup::class.java, id)
        em.remove(group)

        transaction.commit()
    }

}