package edu.hm.cs.animation.server.animation.properties.dao

import edu.hm.cs.animation.server.animation.properties.model.AnimationProperty
import edu.hm.cs.animation.server.animation.properties.model.AnimationPropertyCompositeKey
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object processing animation properties.
 */
class AnimationPropertyDAO {

    fun findAllValues(): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery(
                "SELECT a FROM AnimationProperty a",
                AnimationProperty::class.java
        )
                .resultList!!;

        transaction.commit()

        return properties
    }

    fun findValuesByLocale(locale: String): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery(
                "SELECT a FROM AnimationProperty a WHERE a.locale = :locale",
                AnimationProperty::class.java
        )
                .setParameter("locale", locale)
                .resultList!!;

        transaction.commit()

        return properties
    }

    fun findValuesByKey(key: String): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery(
                "SELECT a FROM AnimationProperty a WHERE a.key = :k",
                AnimationProperty::class.java
        )
                .setParameter("k", key)
                .resultList!!;

        transaction.commit()

        return properties
    }

    fun findValuesByAnimationId(animationId: Long): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery(
                "SELECT a FROM AnimationProperty a WHERE a.animationId = :animId",
                AnimationProperty::class.java
        )
                .setParameter("animId", animationId)
                .resultList!!;

        transaction.commit()

        return properties
    }

    fun findValuesByAnimationIdAndKey(animationId: Long, key: String): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery(
                "SELECT a FROM AnimationProperty a WHERE a.animationId = :animId AND a.key = :k",
                AnimationProperty::class.java
        )
                .setParameter("animId", animationId)
                .setParameter("k", key)
                .resultList!!;

        transaction.commit()

        return properties
    }

    fun findValuesByKeyAndLocale(locale: String, key: String): List<AnimationProperty> {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val properties: List<AnimationProperty> = em.createQuery(
                "SELECT a FROM AnimationProperty a WHERE a.locale = :locale AND a.key = :k",
                AnimationProperty::class.java
        )
                .setParameter("locale", locale)
                .setParameter("k", key)
                .resultList!!;

        transaction.commit()

        return properties
    }

    fun findValuesByAnimationIdAndLocale(animationId: Long, locale: String): List<AnimationProperty> {
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