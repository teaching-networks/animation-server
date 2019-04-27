package edu.hm.cs.animation.server.animgroup.dao

import edu.hm.cs.animation.server.animgroup.model.AnimGroup
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object for animation groups.
 */
class AnimGroupDAO {

    fun create(animGroup: AnimGroup): AnimGroup {
        return PersistenceUtil.transaction {
            it.persist(animGroup)

            return@transaction animGroup
        }
    }

    fun find(id: Long): AnimGroup {
        return PersistenceUtil.transaction {
            return@transaction it.find(AnimGroup::class.java, id)
        }
    }

    fun findAll(): List<AnimGroup> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT a FROM AnimGroup a", AnimGroup::class.java).resultList!!
        }
    }

    fun update(group: AnimGroup) {
        PersistenceUtil.transaction {
            val dbAnimGroup: AnimGroup = it.find(AnimGroup::class.java, group.id)

            dbAnimGroup.name = group.name
            dbAnimGroup.animationIds = group.animationIds
            dbAnimGroup.animationIdOrder = group.animationIdOrder

            it.merge(dbAnimGroup)
        }
    }

    fun remove(id: Long) {
        PersistenceUtil.transaction {
            val group: AnimGroup = it.find(AnimGroup::class.java, id)

            it.remove(group)
        }
    }

}