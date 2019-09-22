/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animation.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Animation entity.
 */
@Entity
@Table(name = "Animations")
data class Animation(

        /**
         * Id of the animation.
         */
        @Id
        var id: Long? = null,

        /**
         * The url part under which the animation is accessible.
         * e. g. localhost:8080/#/animation/my-animation
         * where the url part is "my-animation".
         */
        @Column(length = 100)
        var url: String? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Animation

        if (id != other.id) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        return result
    }
}
