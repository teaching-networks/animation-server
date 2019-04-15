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
        var value: String

)
