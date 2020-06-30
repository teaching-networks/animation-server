/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.settings.model

import javax.persistence.*

/**
 * User of the API.
 */
@Entity
@Table(name = "Settings")
data class Setting(

        /**
         * Key of the setting.
         */
        @Id
        var key: String = "",

        /**
         * Type of the setting.
         */
        @Column(nullable = false, length = 256)
        var type: String = "",

        /**
         * Value of the setting.
         */
        @Column(nullable = false, length = 1000)
        var value: String = ""

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Setting

        if (key != other.key) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
