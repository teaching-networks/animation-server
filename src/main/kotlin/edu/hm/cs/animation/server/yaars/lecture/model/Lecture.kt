package edu.hm.cs.animation.server.yaars.lecture.model

import javax.persistence.*

@Entity
@Table(name = "lectures")
data class Lecture(

        @GeneratedValue(strategy = GenerationType.AUTO)
        @Id
        @Column(name = "lecture_id")
        val id: Long? = null,

        @Column(nullable = false)
        var name: String = ""
)
