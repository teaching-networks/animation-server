/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.poll.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import edu.hm.cs.animation.server.yaars.poll.answer.model.Answer
import javax.persistence.*

/**
 * Representation of a Poll.
 */
@Entity
@Table(name = "polls")
data class Poll(

        /**
         * Id of the Entity.
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "poll_id")
        val id: Long? = null,

        /**
         * Main question asked in the poll.
         */
        @Column(nullable = false, length = 1000)
        var question: String = "",

        /**
         * Related Lecture (every poll has to be related to one Lecture (ManyToOne)).
         */
        @ManyToOne
        var lecture: Lecture,

        /**
         * List of Available Answers. Every poll is able to havebetween 2 and 5 questions (OneToMany).
         */
        @OneToMany(cascade = arrayOf(CascadeType.ALL), mappedBy = "relatedPoll", fetch = FetchType.EAGER)
        @JsonManagedReference
        var answers: List<Answer>,

        /**
         * Current state of the poll (active or inactive).
         */
        @Column(nullable = false)
        var active: Boolean = false
)
