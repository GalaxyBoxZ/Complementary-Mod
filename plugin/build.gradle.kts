plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    paperweight.paperDevBundle(providers.gradleProperty("paperVersion"))
    implementation(project(":shared"))
    implementation("io.github.retrooper:packetevents-spigot:${property("packetEventsVersion")}")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveBaseName.set("gbz-combat-plugin")
}
