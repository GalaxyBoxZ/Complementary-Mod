plugins {
    id("fabric-loom") version "1.10.3"
    kotlin("jvm") version "2.1.20"
}

val mod_id: String by project
val mod_version: String by project
val mod_name: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_api_version: String by project
val fabric_kotlin_version: String by project
val player_anim_version: String by project

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.kosmx.dev/")
}

base {
    archivesName.set("$mod_id-client")
}

loom {
    mods {
        create(mod_id) {
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
        java.setSrcDirs(listOf("src/main/kotlin"))
        resources.setSrcDirs(listOf("src/main/resources"))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

    include(modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:$player_anim_version")!!)
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("version", mod_version)
    inputs.property("minecraft_version", minecraft_version)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to mod_version,
                "minecraft_version" to minecraft_version,
                "mod_id" to mod_id,
                "mod_name" to mod_name
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
