plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveBaseName.set("gbz-combat-shared")
}
