plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

dependencies {
    // https://github.com/JetBrains/JetBrainsRuntimeApi
    implementation(files("./libs/jbr-api-1.0.2.jar"))
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(compose.material3)
    implementation(libs.jna.core)
    implementation(libs.jna.platform)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.jSystemThemeDetector) {
        exclude("org.slf4j")
        exclude("net.java.dev.jna")
    }
}
