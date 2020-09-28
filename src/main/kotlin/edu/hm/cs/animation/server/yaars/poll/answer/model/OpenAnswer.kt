package edu.hm.cs.animation.server.yaars.poll.answer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Representation of am Answer to an asked open question in an open question poll.
 */
@Entity
@Table(name = "openAnswers")
class OpenAnswer(

        answerId: Long,

        /**
         * The related Poll in which the answer is available (ManyToOne).
         */
        @ManyToOne
        @JsonBackReference
        var relatedPoll: OpenQuestionPoll? = null,

        /**
         * Text of the answer.
         */
        @Column(nullable = false, length = 1000)
        var text: String = "",

        /**
         * How often the answer got mentioned.
         */
        @Column(nullable = false)
        var timesMentioned: Long
) : YaarsAnswer(answerId)
