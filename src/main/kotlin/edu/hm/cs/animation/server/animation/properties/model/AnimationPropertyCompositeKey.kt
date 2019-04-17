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
        var key: String? = ""

) : Serializable