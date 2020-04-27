package edu.hm.cs.animation.server.util.stomp.subscriptions

import edu.hm.cs.animation.server.yaars.poll.model.Poll

object STOMPPollSubscriptionManager : STOMPSubscriptionManager<Poll>() {
    override fun notifyAboutChange(changedObject: Poll) {
        for (subscriber in subscribers) {
            if (changedObject.id == subscriber.third) {
                factorAndSendResponse(subscriber, changedObject)
            }
        }
    }
}
