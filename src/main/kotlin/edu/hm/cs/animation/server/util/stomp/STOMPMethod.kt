package edu.hm.cs.animation.server.util.stomp

/**
 * Supported Methods for STOMP.
 * Note: Not all specified Methods are used and implemented.
 */
enum class STOMPMethod {
    SEND, SUBSCRIBE, UNSUBSCRIBE, CONNECT, DISCONNECT, ERROR, RECEIPT, MESSAGE, ILLEGAL;
}
