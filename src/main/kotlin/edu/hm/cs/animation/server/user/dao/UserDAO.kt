package edu.hm.cs.animation.server.user.dao

import edu.hm.cs.animation.server.user.model.User
import edu.hm.cs.animation.server.util.PersistenceUtil
import javax.persistence.NoResultException

/**
 * Data access object dealing with users.
 */
class UserDAO {

    fun findAllUsers(): List<User> {
        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        val users: List<User> = em.createQuery("SELECT e FROM User e", User::class.java).resultList!!

        transaction.commit()

        return users
    }

    fun findUser(id: Long): User {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val user: User = em.find(User::class.java, id)

        transaction.commit()

        return user
    }

    fun getUserCount(): Long {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        val count: Long = (em.createQuery("SELECT COUNT(u) FROM User u").singleResult as Long?)!!

        transaction.commit()

        return count
    }

    fun findUserByName(name: String): User? {
        val em = PersistenceUtil.createEntityManager()
        val transaction = em.transaction
        transaction.begin()

        var user: User? = null;
        try {
            user = em.createQuery("SELECT u from User u WHERE u.name = :name", User::class.java).setParameter("name", name).singleResult
        } catch (e: NoResultException) {
            // Do nothing.
        }

        transaction.commit()

        return user
    }

    fun createUser(user: User): User {
        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        try {
            em.persist(user)
            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }

        return user;
    }

    fun updateUser(user: User) {
        val dbUser = findUser(user.id!!) ?: throw Exception("User to update could not be found in the database")

        dbUser.name = user.name
        dbUser.password = user.password

        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        try {
            em.merge(dbUser)
            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()

            throw e // Rethrow exception
        }
    }

    fun removeUser(id: Long) {
        val dbUser = findUser(id) ?: throw Exception("User to remove could not be found in the database")

        val em = PersistenceUtil.createEntityManager();
        val transaction = em.transaction;
        transaction.begin()

        em.remove(dbUser)

        transaction.commit()
    }

}