/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.stomp

/**
 * Class representing a STOMP request and response.
 */
class STOMPFrame(val method: STOMPMethod, val body: String?, val header: Map<String, String>)
