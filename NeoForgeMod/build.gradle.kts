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
    alias(libs.plugins.shadow)
}

evaluationDependsOn(":StaffMod")

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}
val compileClasspath: Configuration by configurations.getting {
    extendsFrom(common)
}
val runtimeClasspath: Configuration by configurations.getting {
    extendsFrom(common)
}
val developmentNeoForge: Configuration by configurations.getting {
    extendsFrom(common)
}
// Files in this configuration will be bundled into your mod using the Shadow plugin.
// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

repositories {
    maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
    maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for Forge" }
}

dependencies {
    neoForge(libs.neoforge)
    modApi(libs.architectury.neoforge)

    compileOnly(libs.mixinextras.common)
    annotationProcessor(libs.mixinextras.common)

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":StaffMod", configuration = "transformProductionNeoForge")) { isTransitive = false }

    implementation(libs.kotlinforforge.neoforge)
}

tasks {
    jar {
        archiveClassifier = "dev"
    }

    processResources {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                mutableMapOf(
                    "version" to version as String,
                    "neoforge" to libs.versions.neoforge.get(),
                    "kotlin_for_forge" to libs.versions.kotlinforforge.get(),
                    "architectury" to libs.versions.architectury.api.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to libs.versions.java.get()
                )
            )
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")

        configurations = listOf(shadowBundle)
        archiveClassifier = "dev-shadow"

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
        from(rootDir.resolve("README.md"))
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile = shadowJar.get().archiveFile
        injectAccessWidener = true
        archiveClassifier = null

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
        from(rootDir.resolve("README.md"))
    }

    sourcesJar {
        val commonSources = project(":StaffMod").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }
}

components {
    getByName("java") {
        this as AdhocComponentWithVariants
        this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
            skip()
        }
    }
}
