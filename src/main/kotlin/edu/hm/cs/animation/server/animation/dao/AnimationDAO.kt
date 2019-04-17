package edu.hm.cs.animation.server.animation.dao

import edu.hm.cs.animation.server.animation.model.Animation
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object processing animation objects.
 */
class AnimationDAO {

    fun findAllAnimations(): List<Animation> {
        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        val animations: List<Animation> = em.createQuery("SELECT a FROM Animation a", Animation::class.java).resultList!!

        transaction.commit()

        return animations
    }

    fun findAnimation(id: Long): Animation {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val animation: Animation = em.find(Animation::class.java, id)

        transaction.commit()

        return animation
    }

    fun createAnimation(animation: Animation): Animation {
        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        try {
            em.persist(animation)

            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }

        return animation;
    }

    fun updateAnimation(animation: Animation) {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val dbAnimation: Animation = em.find(Animation::class.java, animation.id)

        dbAnimation.url = animation.url

        try {
            em.merge(dbAnimation)
            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }
    }

    fun removeAnimation(id: Long) {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val animation: Animation = em.find(Animation::class.java, id)
        em.remove(animation)

        transaction.commit()
    }

}