package org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityImportData
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityImportInput
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityImportReader
import java.io.File

class CsvFederalEntityImportInput(val path: String, val keyCodeHeader: String, val nameHeader: String) :
    FederalEntityImportInput()

class CsvFederalEntityImportReader : FederalEntityImportReader<CsvFederalEntityImportInput> {
    private val csvReader = csvReader()

    override fun read(input: CsvFederalEntityImportInput): List<FederalEntityImportData> {
        return csvReader.open(File(input.path)) {
            readAllWithHeaderAsSequence().mapNotNull { row ->
                row[input.keyCodeHeader]?.padStart(2, '0')?.let { keyCode ->
                    val name = row[input.nameHeader] ?: ""
                    FederalEntityImportData(keyCode, name)
                }
            }.toList()
        }
    }
}
