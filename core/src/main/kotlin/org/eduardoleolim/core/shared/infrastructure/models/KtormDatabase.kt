package org.eduardoleolim.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.SQLiteDataSource

object KtormDatabase {
    fun init(databasePath: String, isReadOnly: Boolean = false): Database {
        SQLiteDataSource().apply {
            url = databasePath
            setEnforceForeignKeys(true)
            setReadOnly(isReadOnly)
        }.let {
            return Database.connect(it, SQLiteDialect())
        }
    }
}
