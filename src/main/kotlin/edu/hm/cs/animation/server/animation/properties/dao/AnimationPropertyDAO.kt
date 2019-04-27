package edu.hm.cs.animation.server.animation.properties.dao

import edu.hm.cs.animation.server.animation.properties.model.AnimationProperty
import edu.hm.cs.animation.server.animation.properties.model.AnimationPropertyCompositeKey
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object processing animation properties.
 */
class AnimationPropertyDAO {

    fun findAllValues(): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery(
                    "SELECT a FROM AnimationProperty a",
                    AnimationProperty::class.java
            ).resultList!!
        }
    }

    fun findValuesByLocale(locale: String): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery(
                    "SELECT a FROM AnimationProperty a WHERE a.locale = :locale",
                    AnimationProperty::class.java
            ).setParameter("locale", locale).resultList!!
        }
    }

    fun findValuesByKey(key: String): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery(
                    "SELECT a FROM AnimationProperty a WHERE a.key = :k",
                    AnimationProperty::class.java
            ).setParameter("k", key).resultList!!
        }
    }

    fun findValuesByAnimationId(animationId: Long): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery(
                    "SELECT a FROM AnimationProperty a WHERE a.animationId = :animId",
                    AnimationProperty::class.java
            ).setParameter("animId", animationId).resultList!!
        }
    }

    fun findValuesByAnimationIdAndKey(animationId: Long, key: String): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery(
                    "SELECT a FROM AnimationProperty a WHERE a.animationId = :animId AND a.key = :k",
                    AnimationProperty::class.java
            )
                    .setParameter("animId", animationId)
                    .setParameter("k", key)
                    .resultList!!
        }
    }

    fun findValuesByKeyAndLocale(locale: String, key: String): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery(
                    "SELECT a FROM AnimationProperty a WHERE a.locale = :locale AND a.key = :k",
                    AnimationProperty::class.java
            )
                    .setParameter("locale", locale)
                    .setParameter("k", key)
                    .resultList!!
        }
    }

    fun findValuesByAnimationIdAndLocale(animationId: Long, locale: String): List<AnimationProperty> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT a FROM AnimationProperty a WHERE a.animationId = :animationId AND a.locale = :locale", AnimationProperty::class.java)
                    .setParameter("animationId", animationId)
                    .setParameter("locale", locale)
                    .resultList!!
        }
    }

    fun findValue(animationId: Long, locale: String, key: String): AnimationProperty? {
        return PersistenceUtil.transaction {
            return@transaction it.find(AnimationProperty::class.java, AnimationPropertyCompositeKey(animationId, locale, key))
        }
    }

    fun setValue(animationId: Long, locale: String, key: String, value: String) {
        PersistenceUtil.transaction {
            val property: AnimationProperty? = it.find(AnimationProperty::class.java, AnimationPropertyCompositeKey(animationId, locale, key))

            if (property == null) {
                // Does not exist yet -> Create
                it.persist(AnimationProperty(animationId, locale, key, value))
            } else {
                // Does already exist -> Update
                property.value = value

                it.merge(property)
            }
        }
    }

}