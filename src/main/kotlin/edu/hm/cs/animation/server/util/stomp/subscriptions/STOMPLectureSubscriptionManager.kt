/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.stomp.subscriptions

import edu.hm.cs.animation.server.yaars.poll.model.Poll

/**
 * A Subscription Manager for lectures.
 */
object STOMPLectureSubscriptionManager : STOMPSubscriptionManager<Poll>() {
    override fun notifyAboutChange(changedObject: Poll) {
        for (subscriber in subscribers) {
            if (subscriber.third == changedObject.lecture.id) {
                factorAndSendResponse(subscriber, changedObject)
            }
        }
    }
}
