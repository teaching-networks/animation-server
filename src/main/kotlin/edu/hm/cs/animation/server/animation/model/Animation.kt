package edu.hm.cs.animation.server.animation.model

import javax.persistence.*

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
        var id: Long,

        /**
         * Whether the animation is visible in the web application.
         */
        @Column(nullable = false)
        var visible: Boolean

)