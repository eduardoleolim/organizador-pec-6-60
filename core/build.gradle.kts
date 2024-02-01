plugins {
    id("org.eduardoleolim.organizadorpec660.kotlin-library-conventions")
}

dependencies {
    api("org.ktorm:ktorm-core:${properties["ktormVersion"]}")
    api("org.ktorm:ktorm-support-sqlite:${properties["ktormVersion"]}")
    api("org.xerial:sqlite-jdbc:${properties["sqliteVersion"]}")
    api("org.slf4j:slf4j-simple:${properties["slf4jVersion"]}")
}
