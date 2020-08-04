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

        override val id: Long,

        override var question: String,

        override var lecture: Lecture,

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
) : YaarsPoll(id, question, lecture)
