package org.eduardoleolim.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.SQLiteDataSource

object KtormDatabase {
    fun init(databasePath: String): Database {
        val dataSource = SQLiteDataSource()
        dataSource.url = databasePath
        dataSource.setEnforceForeignKeys(true)

        return Database.connect(dataSource, SQLiteDialect())
    }
}
