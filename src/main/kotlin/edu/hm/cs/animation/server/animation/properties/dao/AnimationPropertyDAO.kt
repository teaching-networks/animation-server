package edu.hm.cs.animation.server.animation.properties.dao

import edu.hm.cs.animation.server.animation.properties.model.AnimationProperty
import edu.hm.cs.animation.server.animation.properties.model.AnimationPropertyCompositeKey
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object processing animation properties.
 */
class AnimationPropertyDAO {

    fun findValues(animationId: Long, locale: String): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery("SELECT a FROM AnimationProperty a WHERE a.animationId = :animationId AND a.locale = :locale", AnimationProperty::class.java)
                .setParameter("animationId", animationId)
                .setParameter("locale", locale)
                .resultList!!

        transaction.commit()

        return properties
    }

    fun findValue(animationId: Long, locale: String, key: String): AnimationProperty? {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val property: AnimationProperty? = em.find(AnimationProperty::class.java, AnimationPropertyCompositeKey(animationId, locale, key))

        transaction.commit()

        return property
    }

    fun setValue(animationId: Long, locale: String, key: String, value: String) {
        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        val property: AnimationProperty? = em.find(AnimationProperty::class.java, AnimationPropertyCompositeKey(animationId, locale, key))

        try {
            if (property == null) {
                // Does not exist yet -> Create
                em.persist(AnimationProperty(animationId, locale, key, value))
            } else {
                // Does already exist -> Update
                property.value = value

                em.merge(property)
            }

            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }
    }

}