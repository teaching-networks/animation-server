package edu.hm.cs.animation.server.util.stomp.subscriptions

import edu.hm.cs.animation.server.yaars.poll.model.OpenQuestionPoll

/**
 * A Subscription Manager for polls.
 */
object STOMPOpenPollSubscriptionManager : STOMPSubscriptionManager<OpenQuestionPoll>() {
    override fun notifyAboutChange(changedObject: OpenQuestionPoll) {
        for (subscriber in STOMPOpenPollSubscriptionManager.subscribers) {
            if (changedObject.id == subscriber.third) {
                STOMPOpenPollSubscriptionManager.factorAndSendResponse(subscriber, changedObject)
            }
        }
    }
}