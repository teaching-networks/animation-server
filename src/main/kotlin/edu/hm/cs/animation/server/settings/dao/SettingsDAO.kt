/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.settings.dao

import edu.hm.cs.animation.server.settings.model.Setting
import edu.hm.cs.animation.server.util.PersistenceUtil

/**
 * DAO for managing settings.
 */
class SettingsDAO {

    fun findAll(): List<Setting> {
        return PersistenceUtil.transaction {
            return@transaction it.createQuery("SELECT a FROM Setting a", Setting::class.java).resultList!!
        }
    }

    fun find(key: String): Setting {
        return PersistenceUtil.transaction {
            return@transaction it.find(Setting::class.java, key)
        }
    }

    fun create(entity: Setting): Setting {
        return PersistenceUtil.transaction {
            it.persist(entity)

            return@transaction entity
        }
    }

    fun update(entity: Setting) {
        PersistenceUtil.transaction {
            val dbSetting: Setting = it.find(Setting::class.java, entity.key)
            dbSetting.value = entity.value

            it.merge(dbSetting)
        }
    }

    fun remove(key: String) {
        PersistenceUtil.transaction {
            val entity: Setting = it.find(Setting::class.java, key)
            it.remove(entity)
        }
    }

}
