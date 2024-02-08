plugins {
    java
    kotlin("jvm")
    id("architectury-plugin")
    id("dev.architectury.loom") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("org.jetbrains.dokka")
}

architectury {
    minecraft = rootProject.extra["minecraft_version"] as String
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka", "dokka-base", project.extra["dokka_version"] as String)
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    dependencies {
        "minecraft"("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
        "mappings"("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, classifier = "v2")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "architectury-plugin")

    val javaVersion = JavaVersion.toVersion((project.extra["java_version"] as String).toInt())

    base {
        archivesName = rootProject.extra["archives_name"] as String
    }

    version = rootProject.extra["mod_version"] as String
    group = rootProject.extra["maven_group"] as String

    repositories {
        // Add repositories to retrieve artifacts from in here.
        // You should only use this when depending on other mods because
        // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
        // See https://docs.gradle.org/current/userguide/declaring_repositories.html
        // for more information about repositories.
    }

    dependencies {
        compileOnly("org.jetbrains.kotlin", "kotlin-stdlib", System.getProperty("kotlin_version") as String)
    }

    kotlin.target.compilations.all {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release = javaVersion.toString().toInt()
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion.toString())
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
