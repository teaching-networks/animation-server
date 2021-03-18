package edu.hm.cs.animation.server.util

import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPOpenPollSubscriptionManager
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPPollSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import kotlin.math.abs
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object PollCleanupUtil {
    fun setOldPollsInactive(minutesTillOld: Long) {
        val now = LocalDateTime.now()

        PersistenceUtil.transaction {
            val openPolls = it
                    .createQuery("SELECT op FROM OpenQuestionPoll op",
                            OpenQuestionPoll::class.java)
                    .resultList ?: return@transaction

            for (openPoll in openPolls) {
                openPoll.timeStarted?.let { timesStarted ->

                    if (getDuration(now, timesStarted) >= minutesTillOld) {
                        openPoll.timeStarted = null
                        openPoll.active = false
                        STOMPOpenPollSubscriptionManager.notifyAboutChange(openPoll)
                        it.merge(openPoll)
                    }
                }
            }

            val polls = it
                    .createQuery("SELECT p FROM Poll p",
                            Poll::class.java)
                    .resultList ?: return@transaction

            for (poll in polls) {
                poll.timeStarted?.let { timeStarted ->
                    if (getDuration(now, timeStarted) >= minutesTillOld) {
                        poll.timeStarted = null
                        poll.active = false
                        STOMPPollSubscriptionManager.notifyAboutChange(poll)
                        it.merge(poll)
                    }
                }
            }
        }
    }

    fun getDuration(time1: LocalDateTime, time2: LocalDateTime): Long {
        return abs(time1.until(time2, ChronoUnit.MINUTES))
    }
}
