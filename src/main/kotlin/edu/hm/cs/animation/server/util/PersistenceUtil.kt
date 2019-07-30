/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

object PersistenceUtil {

    private val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("default")

    fun createEntityManager() = entityManagerFactory.createEntityManager()!!

    fun destroyEntityManagerFactory() {
        entityManagerFactory.close()
    }

    /**
     * Execute the passed operation inside a database transaction.
     */
    fun <R> transaction(operation: (EntityManager) -> R): R {
        val entityManager = createEntityManager()
        val transaction = entityManager.transaction
        transaction.begin()

        try {
            val result = operation(entityManager)

            transaction.commit()

            return result
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        } finally {
            entityManager.close()
        }
    }

}
