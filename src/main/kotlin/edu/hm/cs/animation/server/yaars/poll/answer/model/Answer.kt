/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.poll.answer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import javax.persistence.*

/**
 * Representation of a Answer to an asked question in a poll.
 */
@Entity
@Table(name = "answers")
data class Answer(

        /**
         * Id of the Entity
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "answer_id")
        val answerId: Long? = null,

        /**
         * Text of the answer.
         */
        @Column(nullable = false, length = 1000)
        var text: String = "",

        /**
         * Is the answer correct?
         */
        @Column(nullable = false)
        var correct: Boolean = false,

        /**
         * The related Poll in which the answer is available (ManyToOne).
         */
        @ManyToOne
        @JsonBackReference
        var relatedPoll: Poll? = null,

        @Column(name = "times_voted")
        var timesVoted: Int = 0
)
