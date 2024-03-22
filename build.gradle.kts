plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.architectury)
    alias(libs.plugins.architectury.loom) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.dokka)
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath(libs.dokka.base)
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(variantOf(rootProject.libs.yarn) { classifier("v2") })
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "architectury-plugin")

    val javaVersion = rootProject.libs.versions.java.get()

    base {
        archivesName = "staff-mod"
    }

    val suffix = if (extra.has("loom.platform")) "+${extra["loom.platform"]}" else ""
    version = rootProject.libs.versions.staff.mod.get() + suffix
    group = "opekope2.avm_staff"

    repositories {
        // Add repositories to retrieve artifacts from in here.
        // You should only use this when depending on other mods because
        // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
        // See https://docs.gradle.org/current/userguide/declaring_repositories.html
        // for more information about repositories.
    }

    dependencies {
        compileOnly(rootProject.libs.kotlin.stdlib)
    }

    kotlin.target.compilations.all {
        kotlinOptions.jvmTarget = javaVersion
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        options.release = javaVersion.toInt()
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion)
        }
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
        withSourcesJar()
    }
}
