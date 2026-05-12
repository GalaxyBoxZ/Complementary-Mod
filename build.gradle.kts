plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("io.papermc.paperweight.userdev") version "1.7.2" apply false
    id("fabric-loom") version "1.8-SNAPSHOT" apply false
}

subprojects {
    group = "com.gbz.combat"
    version = "1.0.0-SNAPSHOT"
}
