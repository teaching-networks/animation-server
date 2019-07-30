/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.animgroup.model

import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
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
        @ElementCollection()
        @LazyCollection(LazyCollectionOption.FALSE)
        var animationIds: List<Long>,

        /**
         * Order of the animations.
         */
        @ElementCollection()
        @LazyCollection(LazyCollectionOption.FALSE)
        var animationIdOrder: List<Long>

)
