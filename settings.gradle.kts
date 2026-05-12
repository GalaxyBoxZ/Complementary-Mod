pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kosmx.dev/")
    }
}


rootProject.name = "gbz-combat"
include("client")
