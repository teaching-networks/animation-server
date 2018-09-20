package edu.hm.cs.animation.server.user.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp
import java.util.*
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
        var id: Long?,

        /**
         * Name of the user.
         */
        @Column(nullable = false, unique = true)
        var name: String,

        /**
         * Password of the user. Should be encoded and not plain-text.
         */
        @Column(nullable = false)
        @get:JsonIgnore
        @set:JsonProperty("password")
        var password: String?,

        /**
         * Salt used to encode the password.
         * Used to verify whether a password matches the one stored in the user.
         */
        @Column(nullable = false)
        @JsonIgnore
        var passwordSalt: String?,

        /**
         * How many unsuccessful login attempts in a row the user has experienced.
         */
        @Column(nullable = false)
        @JsonIgnore
        var unsuccessfulLoginAttempts: Int?,

        /**
         * When the last unsuccessful login attempt took place.
         */
        @Column(nullable = true)
        @JsonIgnore
        var lastUnsuccessfulLogin: Timestamp?

)