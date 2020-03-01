package edu.hm.cs.animation.server.yaars.poll.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import edu.hm.cs.animation.server.yaars.lecture.model.Lecture
import edu.hm.cs.animation.server.yaars.poll.answer.model.Answer
import javax.persistence.*

@Entity
@Table(name = "polls")
class Poll(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "poll_id")
        val id: Long? = null,

        @Column(nullable = false, length = 1000)
        var question: String = "",

        @ManyToOne
        var lecture: Lecture,

        // TODO: Schauen ob es besser geht als mit EAGER
        @OneToMany(cascade = arrayOf(CascadeType.ALL), mappedBy = "relatedPoll", fetch = FetchType.EAGER)
        @JsonManagedReference
        var answers: List<Answer>,

        var active: Boolean = false
) {
    fun getAnwers(): List<Answer> {
        return answers
    }
}