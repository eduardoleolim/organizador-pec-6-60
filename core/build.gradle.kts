plugins {
    id("org.eduardoleolim.organizadorpec660.kotlin-library-conventions")
}

dependencies {
    api(libs.ktorm.core)
    api(libs.ktorm.sqlite)
    api(libs.jdbc.sqlite)
    api(libs.slf4j.simple)
    api(libs.koin.core)
}
