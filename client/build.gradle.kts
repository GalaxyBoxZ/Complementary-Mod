plugins {
    id("fabric-loom") version "1.10.3"
    kotlin("jvm") version "2.1.20"
}

base {
    archivesName.set("${property("mod_id")}-client")
}

loom {
    mods {
        create(property("mod_id") as String) {
            sourceSet(sourceSets.main.get())
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")
        resources.srcDir("src/main/resources")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    include(modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${property("player_anim_version")}")!!)
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("version", property("mod_version"))
    inputs.property("minecraft_version", property("minecraft_version"))

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to property("mod_version"),
                "minecraft_version" to property("minecraft_version"),
                "mod_id" to property("mod_id"),
                "mod_name" to property("mod_name")
            )
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}
