/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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
