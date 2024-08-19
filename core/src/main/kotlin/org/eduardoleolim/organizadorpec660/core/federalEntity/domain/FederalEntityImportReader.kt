package org.eduardoleolim.organizadorpec660.core.federalEntity.domain

class FederalEntityImportData(keyCode: String, name: String) {
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}

abstract class FederalEntityImportInput

abstract class CsvFederalEntityImportInput : FederalEntityImportInput()

interface FederalEntityImportReader<I : FederalEntityImportInput> {
    fun read(input: I): List<FederalEntityImportData>
}
