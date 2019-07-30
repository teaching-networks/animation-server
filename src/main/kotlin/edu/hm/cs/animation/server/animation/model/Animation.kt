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
        var id: Long,

        /**
         * The url part under which the animation is accessible.
         * e. g. localhost:8080/#/animation/my-animation
         * where the url part is "my-animation".
         */
        @Column(length = 100)
        var url: String

)
