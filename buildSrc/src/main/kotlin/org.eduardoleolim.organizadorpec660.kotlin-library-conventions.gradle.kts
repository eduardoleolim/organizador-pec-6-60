plugins {
    // Apply the common convention plugin for shared build configuration between library and application projects.
    id("org.eduardoleolim.organizadorpec660.kotlin-common-conventions")

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}
