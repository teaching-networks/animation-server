package edu.hm.cs.animation.server.util.stomp.subscriptions

import edu.hm.cs.animation.server.yaars.poll.model.Poll

object STOMPLectureSubscriptionManager : STOMPSubscriptionManager<Poll>() {
    override fun notifyAboutChange(changedObject: Poll) {
        for (subscriber in subscribers) {
            if (subscriber.third == changedObject.lecture.id) {
                factorAndSendResponse(subscriber, changedObject)
            }
        }
    }
}
