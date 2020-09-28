package edu.hm.cs.animation.server.yaars.poll.answer.model

import javax.persistence.*

@MappedSuperclass
open class YaarsAnswer(
        /**
         * Id of the Entity
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "answer_id")
        val answerId: Long
)
