/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animation.properties.model

import java.io.Serializable

/**
 * Composite primary key of an animation property.
 */
data class AnimationPropertyCompositeKey(

        /**
         * The animation the property belongs to.
         */
        var animationId: Long = -1,

        /**
         * The code of the locale the property value is written in.
         */
        var locale: String = "",

        /**
         * The key of the property.
         */
        var key: String = ""

) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnimationPropertyCompositeKey

        if (animationId != other.animationId) return false
        if (locale != other.locale) return false
        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animationId.hashCode()
        result = 31 * result + locale.hashCode()
        result = 31 * result + (key.hashCode() ?: 0)
        return result
    }
}
