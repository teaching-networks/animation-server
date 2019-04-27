package edu.hm.cs.animation.server.user.dao

import edu.hm.cs.animation.server.user.model.User
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * Data access object dealing with users.
 */
class UserDAO {

    fun findAllUsers(): List<User> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT e FROM User e", User::class.java).resultList!!
        }
    }

    fun findUser(id: Long): User {
        return PersistenceUtil.transaction {
            return@transaction it.find(User::class.java, id)
        }
    }

    fun getUserCount(): Long {
        return PersistenceUtil.transaction {
            return@transaction (it.createQuery("SELECT COUNT(u) FROM User u").singleResult as Long?)!!
        }
    }

    fun findUserByName(name: String): User {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT u from User u WHERE u.name = :name", User::class.java).setParameter("name", name).singleResult!!
        }
    }

    fun createUser(user: User): User {
        return PersistenceUtil.transaction {
            user.unsuccessfulLoginAttempts = 0

            it.persist(user)

            return@transaction user
        }
    }

    fun updateUser(user: User) {
        PersistenceUtil.transaction {
            val dbUser: User = it.find(User::class.java, user.id)

            dbUser.name = user.name

            if (user.password != null) {
                dbUser.password = user.password;
                dbUser.passwordSalt = user.passwordSalt;
            }

            if (user.unsuccessfulLoginAttempts != null) {
                dbUser.unsuccessfulLoginAttempts = user.unsuccessfulLoginAttempts;
            }

            if (user.lastUnsuccessfulLogin != null) {
                dbUser.lastUnsuccessfulLogin = user.lastUnsuccessfulLogin;
            }

            it.merge(dbUser)
        }
    }

    fun removeUser(id: Long) {
        PersistenceUtil.transaction {
            val user: User = it.find(User::class.java, id)

            it.remove(user)
        }
    }

}