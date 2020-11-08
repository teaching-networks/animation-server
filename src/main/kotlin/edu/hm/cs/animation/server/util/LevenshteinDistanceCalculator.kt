package edu.hm.cs.animation.server.util

/**
 * An util object that evaluates the similarity of two given strings by calculating the Levenshtein Distance.
 * For more information about the algorithm see https://en.wikipedia.org/wiki/Levenshtein_distance.
 */
object LevenshteinDistanceCalculator {

    /**
     * Calculates the Levenshtein Distance - which is defined by the number of operations it takes to transform one
     * String into the other. Possible operations are:
     *  - Switching letters
     *  - Inserting a letter
     *  - Removing a letter
     *  This implementation uses dynamic programming for calculating those operations.
     */
    fun calculateSimilarity(lhs : String, rhs : String): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1 until rhsLength) {
            newCost[0] = i

            for (j in 1 until lhsLength) {
                val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = costInsert.coerceAtMost(costDelete).coerceAtMost(costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }
}
