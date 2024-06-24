package org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.*
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map

class KtormInstrumentRepository(private val database: Database): InstrumentRepository {
    private val instruments = Instruments("i")
    private val instrumentFiles = InstrumentFiles("if")
    private val agencies = Agencies("ag")
    private val statisticTypes = StatisticTypes("st")
    private val municipalities = Municipalities("m")
    private val criteriaParser = KtormInstrumentsCriteriaParser(database, instruments, instrumentFiles, agencies, statisticTypes, municipalities)

    override fun matching(criteria: Criteria): List<Instrument> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            instruments.createEntity(rowSet).let {
                Instrument.from(
                    it.id,
                    it.statisticYear,
                    it.statisticMonth,
                    it.consecutive,
                    it.saved,
                    it.instrumentFile.id,
                    it.agency.id,
                    it.statisticType.id,
                    it.municipality.id,
                    it.createdAt.toDate(),
                    it.updatedAt?.toDate()
                )
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        val query = criteriaParser.countQuery(criteria)

        return query.rowSet.let {
            it.next()
            it.getInt(1)
        }
    }

    override fun save(instrument: Instrument) {

    }

    override fun delete(instrumentId: String) {
        database.useTransaction {
            val instrument = matching(InstrumentCriteria.idCriteria(instrumentId)).firstOrNull()
                ?: throw InstrumentNotFoundError(instrumentId)



            database.delete(instrumentFiles) {
                it.id eq instrument.instrumentFileId().toString()
            }

            database.delete(instruments) {
                it.id eq instrument.id().toString()
            }


        }
    }
}
