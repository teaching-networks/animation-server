/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animation.properties.model

import javax.persistence.*

/**
 * Property for an animation (for example a description).
 */
@Entity
@Table(name = "AnimationProperties")
@IdClass(AnimationPropertyCompositeKey::class)
data class AnimationProperty(

        /**
         * Id of the animation the property belongs to.
         */
        @Id
        var animationId: Long = -1,

        /**
         * Locale of the language the value is written in.
         */
        @Id
        var locale: String = "",

        /**
         * Key of the animation property.
         */
        @Id
        var key: String = "",

        /**
         * Value of the property.
         */
        @Column(nullable = false, length = 10000)
        var value: String = ""

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnimationProperty

        if (animationId != other.animationId) return false
        if (locale != other.locale) return false
        if (key != other.key) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animationId.hashCode()
        result = 31 * result + locale.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
