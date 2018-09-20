package edu.hm.cs.animation.server.util

import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

object PersistenceUtil {

    private val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("default")

    fun createEntityManager() = entityManagerFactory.createEntityManager()

    fun destroyEntityManagerFactory() {
        entityManagerFactory.close()
    }

}