package org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.eduardoleolim.organizadorpec660.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityImportData
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityImportReader
import java.io.File

class KotlinCsvFederalEntityImportInput(val file: File, val keyCodeHeader: String, val nameHeader: String) :
    CsvFederalEntityImportInput()

class CsvFederalEntityImportReader : FederalEntityImportReader<CsvFederalEntityImportInput> {
    private val csvReader = csvReader()

    override fun read(input: CsvFederalEntityImportInput): List<FederalEntityImportData> {
        when (input) {
            is KotlinCsvFederalEntityImportInput -> {
                return csvReader.open(input.file) {
                    readAllWithHeaderAsSequence().mapNotNull { row ->
                        row[input.keyCodeHeader]?.padStart(2, '0')?.let { keyCode ->
                            val name = row[input.nameHeader] ?: ""
                            FederalEntityImportData(keyCode, name)
                        }
                    }.toList()
                }
            }

            else -> error("There's no a reader for this <${input::class}> input")
        }
    }
}
