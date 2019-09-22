/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.user.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import edu.hm.cs.animation.server.security.roles.Roles
import java.sql.Timestamp
import javax.persistence.*

/**
 * User of the API.
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "Users")
data class User(

        /**
         * The users id.
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        /**
         * Name of the user.
         */
        @Column(nullable = false, unique = true)
        var name: String = "",

        /**
         * Role of the user.
         */
        @Column(columnDefinition = "varchar(255) not null default 'ADMINISTRATOR'")
        @Enumerated(EnumType.STRING)
        @JsonIgnore
        var role: Roles = Roles.ADMINISTRATOR,

        /**
         * Password of the user. Should be encoded and not plain-text.
         */
        @Column(nullable = false)
        @get:JsonIgnore
        @set:JsonProperty("password")
        var password: String? = null,

        /**
         * Salt used to encode the password.
         * Used to verify whether a password matches the one stored in the user.
         */
        @Column(nullable = false)
        @JsonIgnore
        var passwordSalt: String? = null,

        /**
         * How many unsuccessful login attempts in a row the user has experienced.
         */
        @Column(nullable = false)
        @JsonIgnore
        var unsuccessfulLoginAttempts: Int? = 0,

        /**
         * When the last unsuccessful login attempt took place.
         */
        @Column(nullable = true)
        @JsonIgnore
        var lastUnsuccessfulLogin: Timestamp? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (name != other.name) return false
        if (role != other.role) return false
        if (password != other.password) return false
        if (passwordSalt != other.passwordSalt) return false
        if (unsuccessfulLoginAttempts != other.unsuccessfulLoginAttempts) return false
        if (lastUnsuccessfulLogin != other.lastUnsuccessfulLogin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (passwordSalt?.hashCode() ?: 0)
        result = 31 * result + (unsuccessfulLoginAttempts ?: 0)
        result = 31 * result + (lastUnsuccessfulLogin?.hashCode() ?: 0)
        return result
    }
}
