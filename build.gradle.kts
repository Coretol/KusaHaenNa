import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.coretol"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.create<Copy>("buildPlugin") {
    from(tasks.shadowJar)
    into("server/plugins/")
}

tasks.create("writeVersionToFile") {
    File("versions.txt").apply {
        if(!exists()) {
            createNewFile()
        }
        writeText(version as String)
    }
}