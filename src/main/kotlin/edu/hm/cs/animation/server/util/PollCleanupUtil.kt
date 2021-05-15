package edu.hm.cs.animation.server.util

import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPOpenPollSubscriptionManager
import edu.hm.cs.animation.server.util.stomp.subscriptions.STOMPPollSubscriptionManager
import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll
import edu.hm.cs.animation.server.yaars.poll.model.Poll
import edu.hm.cs.animation.server.yaars.poll.model.YaarsPoll
import java.lang.IllegalArgumentException
import kotlin.math.abs
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Handles situations in which Polls needs to get closed because their either
 * duplicates or already opened for too long.
 */
object PollCleanupUtil {
    /**
     * Queries the database for polls that are older that the given
     * minutes and closes them.
     */
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

    /**
     * Checks for a poll that should be opened if there is a similar poll currently active with a similar name,
     * This might be needed if the PowerPoint client crashes and does not recover the currently active poll,
     * in that case a new poll with the same name gets opened. In order to reduce confusion in such cases, the old
     * poll gets closed an the new one gets opened.
     */
    fun checkForOpenPollWithSimilarName(newPoll: YaarsPoll) {
        val threshold = 0.2 * newPoll.question.length
        PersistenceUtil.transaction {
            val table = when (newPoll) {
                is OpenQuestionPoll -> "OpenQuestionPoll"
                is Poll -> "Poll"
                else -> throw IllegalArgumentException("This type of poll is not known")
            }
            val openPolls = it
                    .createQuery("SELECT op FROM $table op",
                            newPoll.javaClass)
                    .resultList ?: return@transaction

            val pollsToClose = openPolls.filter { poll ->
                val isSimilarEnough = LevenshteinDistanceCalculator.calculateSimilarity(
                        newPoll.question.toLowerCase(),
                        poll.question.toLowerCase()
                ) <= threshold
                return@filter (poll.question.equals(newPoll.question, ignoreCase = true) || isSimilarEnough)
                        && poll.lecture.id == newPoll.lecture.id && poll.id != newPoll.id
            }

            for (poll in pollsToClose) {
                poll.active = false
                when (poll) {
                    is Poll -> STOMPPollSubscriptionManager.notifyAboutChange(poll)
                    is OpenQuestionPoll -> STOMPOpenPollSubscriptionManager.notifyAboutChange(poll)
                    else -> throw IllegalArgumentException("This type of poll is not known")
                }
                it.merge(poll)
            }
        }
    }

    fun getDuration(time1: LocalDateTime, time2: LocalDateTime): Long {
        return abs(time1.until(time2, ChronoUnit.MINUTES))
    }
}
