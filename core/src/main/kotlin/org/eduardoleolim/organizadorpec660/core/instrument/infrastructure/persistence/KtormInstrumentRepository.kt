package org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.instrument.domain.*
import org.eduardoleolim.organizadorpec660.core.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFile
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.support.sqlite.insertOrUpdate
import java.io.File
import java.time.LocalDateTime

class KtormInstrumentRepository(
    private val database: Database,
    private val instrumentPath: String
) : InstrumentRepository {
    private val instruments = Instruments("i")
    private val instrumentFiles = InstrumentFiles("if")
    private val agencies = Agencies("ag")
    private val statisticTypes = StatisticTypes("st")
    private val municipalities = Municipalities("m")
    private val criteriaParser =
        KtormInstrumentsCriteriaParser(database, instruments, instrumentFiles, agencies, statisticTypes, municipalities)

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

    override fun searchFileById(id: String): InstrumentFile? {
        val instrumentFile = database.sequenceOf(instrumentFiles).firstOrNull { it.id eq id }

        return instrumentFile?.let { entity ->
            val byteArray = File(entity.path).readBytes()

            InstrumentFile.from(entity.id, byteArray)
        }
    }

    override fun searchFileByInstrumentId(instrumentId: String): InstrumentFile? {
        val instrumentFile = database.from(instrumentFiles)
            .innerJoin(instruments, on = instrumentFiles.id eq instruments.instrumentFileId)
            .select(instrumentFiles.columns)
            .where { instrumentFiles.id eq instrumentId }
            .map { instrumentFiles.createEntity(it) }
            .firstOrNull()

        return instrumentFile?.let { entity ->
            val byteArray = File(entity.path).readBytes()

            InstrumentFile.from(entity.id, byteArray)
        }
    }

    override fun count(criteria: Criteria): Int {
        val query = criteriaParser.countQuery(criteria)

        return query.rowSet.let {
            it.next()
            it.getInt(1)
        }
    }

    override fun save(instrument: Instrument, instrumentFile: InstrumentFile?) {
        database.useTransaction {
            val exists = count(InstrumentCriteria.idCriteria(instrument.id().toString())) > 0

            if (exists.not() && instrumentFile == null)
                throw InstrumentFileRequiredError()

            instrumentFile?.let { file ->
                val id = file.id().toString()
                val path = File(instrumentPath, "$id.pdf").absolutePath
                val byteArray = file.content()

                database.insertOrUpdate(instrumentFiles) {
                    set(it.id, id)
                    set(it.path, path)

                    onConflict(it.id) {
                        set(it.path, path)
                    }
                }

                val saveResult = File(path).runCatching {
                    mkdirs()
                    writeBytes(byteArray)
                }

                if (saveResult.isFailure)
                    throw InstrumentFileFailSaveError()
            }

            database.insertOrUpdate(instruments) {
                set(it.id, instrument.id().toString())
                set(it.statisticYear, instrument.statisticYear())
                set(it.statisticMonth, instrument.statisticMonth())
                set(it.consecutive, instrument.consecutive())
                set(it.saved, instrument.saved())
                set(it.createdAt, instrument.createdAt().toLocalDateTime())
                set(it.agencyId, instrument.agencyId().toString())
                set(it.statisticTypeId, instrument.statisticTypeId().toString())
                set(it.municipalityId, instrument.municipalityId().toString())
                set(it.instrumentFileId, instrument.instrumentFileId().toString())

                onConflict(it.id) {
                    set(it.statisticYear, instrument.statisticYear())
                    set(it.statisticMonth, instrument.statisticMonth())
                    set(it.consecutive, instrument.consecutive())
                    set(it.saved, instrument.saved())
                    set(it.updatedAt, instrument.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                    set(it.agencyId, instrument.agencyId().toString())
                    set(it.statisticTypeId, instrument.statisticTypeId().toString())
                    set(it.municipalityId, instrument.municipalityId().toString())
                }
            }
        }
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
