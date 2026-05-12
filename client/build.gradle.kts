plugins {
    kotlin("jvm")
    id("fabric-loom")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraftVersion")}")
    mappings("net.fabricmc:yarn:${property("yarnMappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("fabricLoaderVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabricApiVersion")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
    modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${property("playerAnimatorVersion")}")
    implementation(project(":shared"))
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveBaseName.set("gbz-combat-client")
}
