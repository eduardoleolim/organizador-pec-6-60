/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.shared.infrastructure.bus

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.infrastructure.bus.KtormAgencyQueryHandlers
import org.eduardoleolim.organizadorpec660.auth.infrastructure.bus.KtormAuthQueryHandlers
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.bus.KtormFederalEntityQueryHandlers
import org.eduardoleolim.organizadorpec660.instrument.infrastructure.bus.KtormInstrumentQueryHandlers
import org.eduardoleolim.organizadorpec660.municipality.infrastructure.bus.KtormMunicipalityQueryHandlers
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryNotRegisteredError
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.infrastructure.bus.KtormStatisticTypeQueryHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(database: Database, instrumentsPath: String) : QueryBus {
    private val context = KtormAppKoinContext(database, instrumentsPath)

    private val queryHandlers =
        HashMap<KClass<out Query<*, *>>, QueryHandler<*, *, out Query<*, *>>>().apply {
            putAll(KtormAgencyQueryHandlers(context).handlers)
            putAll(KtormAuthQueryHandlers(context).handlers)
            putAll(KtormFederalEntityQueryHandlers(context).handlers)
            putAll(KtormInstrumentQueryHandlers(context).handlers)
            putAll(KtormMunicipalityQueryHandlers(context).handlers)
            putAll(KtormStatisticTypeQueryHandlers(context).handlers)
        }

    override fun <L, R> ask(query: Query<L, R>): Either<L, R> {
        val queryHandler = queryHandlers[query::class]

        if (queryHandler != null) {
            @Suppress("UNCHECKED_CAST")
            queryHandler as QueryHandler<L, R, Query<L, R>>

            return queryHandler.handle(query)
        } else {
            throw QueryNotRegisteredError(query::class)
        }
    }

}
