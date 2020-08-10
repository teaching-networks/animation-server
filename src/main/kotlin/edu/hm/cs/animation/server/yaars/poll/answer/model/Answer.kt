/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.yaars.poll.answer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Representation of a Answer to an asked question in a poll.
 */
@Entity
@Table(name = "answers")
class Answer(

        answerId: Long,

        /**
         * The related Poll in which the answer is available (ManyToOne).
         */
        @ManyToOne
        @JsonBackReference
        var relatedPoll: Poll? = null,

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

        @Column(name = "times_voted")
        var timesVoted: Int = 0
) : YaarsAnswer(answerId)
