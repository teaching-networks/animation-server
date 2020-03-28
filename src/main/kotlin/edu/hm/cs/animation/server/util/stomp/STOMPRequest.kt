package edu.hm.cs.animation.server.util.stomp

/**
 * Class representing a STOMP request.
 */
class STOMPRequest(val method: STOMPMethod, val body: String?, val header: Map<String, String>) {}