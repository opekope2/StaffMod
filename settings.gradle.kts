// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("fabric-loom") version settings.extra["loom_version"] as String apply false
        kotlin("jvm") version System.getProperty("kotlin_version") apply false
        id("org.jetbrains.dokka") version settings.extra["dokka_version"] as String apply false
    }
}

include(
    "StaffMod"
)
