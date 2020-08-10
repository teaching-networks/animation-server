package edu.hm.cs.animation.server.yaars.poll.answer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import javax.persistence.*

@MappedSuperclass
abstract class YaarsAnswer(
        /**
         * Id of the Entity
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "answer_id")
        val answerId: Long,

        /**
         * The related Poll in which the answer is available (ManyToOne).
         */
        @ManyToOne
        @JsonBackReference
        var relatedPoll: Poll? = null
)
