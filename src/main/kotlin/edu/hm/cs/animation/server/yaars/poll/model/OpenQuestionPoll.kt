package edu.hm.cs.animation.server.yaars.poll.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import edu.hm.cs.animation.server.yaars.poll.answer.model.OpenAnswer
import javax.persistence.*

/**
 * Representation of an open question poll.
 */
@Entity
@Table(name = "openquestionpolls")
class OpenQuestionPoll(
        id: Long,

        question: String,

        lecture: Lecture,

        active: Boolean,

        /**
         * List of Available Answers. Every poll is able to have between 2 and 5 questions (OneToMany).
         */
        @OneToMany(cascade = arrayOf(CascadeType.ALL), mappedBy = "relatedPoll", fetch = FetchType.EAGER)
        @JsonManagedReference
        var replies: MutableSet<OpenAnswer>
) : YaarsPoll(id, question, lecture, active)
