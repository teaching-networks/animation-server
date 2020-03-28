package edu.hm.cs.animation.server.util.stomp

/**
 * Supported Methods for STOMP.
 * Note: Not all specified Methods are used and implemented.
 */
enum class STOMPMethod {
    SEND, SUBSCRIBE, UNSUBSCRIBE, CONNECT, DISCONNECT, ERROR, RECEIPT, ILLEGAL;

    companion object {
        fun stringToMethod(method: String): STOMPMethod {
            when (method) {
                "SEND" -> return SEND
                "SUBSCRIBE" -> return SUBSCRIBE
                "UNSUBSCRIBE" -> return UNSUBSCRIBE
                "CONNECT" -> return CONNECT
                "DISCONNECT" -> return DISCONNECT
                "ERROR" -> return ERROR
                "RECEIPT" -> return RECEIPT
                else -> return ILLEGAL
            }
        }
    }

}