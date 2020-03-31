package edu.hm.cs.animation.server.util.stomp

/**
 * Class representing a STOMP request and response.
 */
class STOMPFrame(val method: STOMPMethod, val body: String?, val header: Map<String, String>) {}
