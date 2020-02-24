package edu.hm.cs.animation.server.YAARS.lecture.model

import javax.persistence.*

@Entity
@Table(name = "lectures")
data class Lecture(
        @Id
        val id: Long? = null,

        @Column(nullable = false)
        var name: String = ""
)
