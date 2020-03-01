package edu.hm.cs.animation.server.yaars.poll.answer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import javax.persistence.*

@Entity
@Table(name = "answers")
data class Answer(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "answer_id")
        val answerId: Long? = null,

        @Column(nullable = false, length = 1000)
        var text: String = "",

        @Column(nullable = false)
        var correct: Boolean = false,

        @ManyToOne
        @JsonBackReference
        var relatedPoll: Poll? = null
)