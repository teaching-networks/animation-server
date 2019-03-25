package edu.hm.cs.animation.server.animgroup.model

import javax.persistence.*

/**
 * Representation of a animation group.
 * It is a named group of animations within the web application.
 */
@Entity
@Table(name = "AnimGroups")
class AnimGroup(

        /**
         * Id of the entity.
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,

        /**
         * Name of the animation group.
         */
        @Column(nullable = false)
        var name: String,

        /**
         * Ids of animations in this group.
         */
        @ElementCollection
        var animationIds: List<Long>

)