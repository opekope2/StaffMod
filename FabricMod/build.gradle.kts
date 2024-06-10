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
    fabric()
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
val developmentFabric: Configuration by configurations.getting {
    extendsFrom(common)
}
// Files in this configuration will be bundled into your mod using the Shadow plugin.
// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

repositories {
}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modApi(libs.architectury.fabric)

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":StaffMod", configuration = "transformProductionFabric")) { isTransitive = false }

    modImplementation(libs.fabric.language.kotlin)
}

loom {
    accessWidenerPath = project(":StaffMod").loom.accessWidenerPath
}

tasks {
    jar {
        archiveClassifier = "dev"
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version as String,
                    "fabric_api" to libs.versions.fabric.api.get(),
                    "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                    "architectury" to libs.versions.architectury.api.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to libs.versions.java.get()
                )
            )
        }
    }

    shadowJar {
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
