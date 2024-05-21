/*
 * AvM Staff Mod
 * Copyright (c) 2024 opekope2
 *
 * This mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this mod. If not, see <https://www.gnu.org/licenses/>.
 */

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

    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        //"mappings"(variantOf(rootProject.libs.yarn) { classifier("v2") })
        "mappings"(loom.layered { // FIXME
            mappings(variantOf(rootProject.libs.yarn) { classifier("v2") })
            mappings(rootProject.libs.yarn.patch.neoforge)
        })
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
