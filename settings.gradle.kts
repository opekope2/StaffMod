pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        maven("https://maven.architectury.dev/") { name = "Architectury" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm") version System.getProperty("kotlin_version") apply false
        id("dev.architectury.loom") version settings.extra["loom_version"] as String apply false
        id("architectury-plugin") version settings.extra["architectury_plugin_version"] as String apply false
        id("com.github.johnrengelman.shadow") version settings.extra["shadow_version"] as String apply false
        id("org.jetbrains.dokka") version settings.extra["dokka_version"] as String apply false
    }
}

include(
    "StaffMod",
    "FabricMod",
    "ForgeMod"
)
