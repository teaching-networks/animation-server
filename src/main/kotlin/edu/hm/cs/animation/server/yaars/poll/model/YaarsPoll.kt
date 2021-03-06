package edu.hm.cs.animation.server.yaars.poll.model

import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import java.time.LocalDateTime
import javax.persistence.*

/**
 * This represents a general Poll. Every concrete Poll has at least those attributes, but may define more.
 */
@MappedSuperclass
open class YaarsPoll(
        /**
         * Id of the Entity.
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "poll_id")
        val id: Long,

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
         * Current state of the poll (active or inactive).
         */
        @Column(nullable = false)
        var active: Boolean = false,

        @Column(nullable = true)
        var timeStarted: LocalDateTime? = null
)
