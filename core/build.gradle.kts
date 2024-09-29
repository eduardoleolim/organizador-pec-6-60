plugins {
    alias(libs.plugins.jvm)
}

dependencies {
    api(libs.ktorm.core)
    api(libs.ktorm.sqlite)
    api(libs.jdbc.sqlite)
    api(libs.slf4j.simple)
    api(libs.koin.core)
    api(libs.kotlin.csv.jvm)
    api(libs.arrow.core)
    // api("com.healthmarketscience.jackcess:jackcess:4.0.7")

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
