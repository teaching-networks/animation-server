/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.stomp

/**
 * Supported Methods for STOMP.
 * Note: Not all specified Methods are used and implemented.
 */
enum class STOMPMethod {
    SEND, SUBSCRIBE, UNSUBSCRIBE, CONNECT, DISCONNECT, ERROR, RECEIPT, MESSAGE, ILLEGAL;
}
