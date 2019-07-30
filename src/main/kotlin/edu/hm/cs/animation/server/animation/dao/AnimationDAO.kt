/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animation.dao

import edu.hm.cs.animation.server.animation.model.Animation
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object processing animation objects.
 */
class AnimationDAO {

    fun findAllAnimations(): List<Animation> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT a FROM Animation a", Animation::class.java).resultList!!
        }
    }

    fun findAnimation(id: Long): Animation {
        return PersistenceUtil.transaction {
            return@transaction it.find(Animation::class.java, id)
        }
    }

    fun createAnimation(animation: Animation): Animation {
        return PersistenceUtil.transaction {
            it.persist(animation)

            return@transaction animation
        }
    }

    fun updateAnimation(animation: Animation) {
        PersistenceUtil.transaction {
            val dbAnimation: Animation = it.find(Animation::class.java, animation.id)
            dbAnimation.url = animation.url

            it.merge(dbAnimation)
        }
    }

    fun removeAnimation(id: Long) {
        PersistenceUtil.transaction {
            val animation: Animation = it.find(Animation::class.java, id)
            it.remove(animation)
        }
    }

}
