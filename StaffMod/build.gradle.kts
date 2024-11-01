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

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.time.Year

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.dokka)
}

architectury {
    platformSetupLoomIde()
    forge()
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
val developmentForge: Configuration by configurations.getting {
    extendsFrom(common)
}
// Files in this configuration will be bundled into your mod using the Shadow plugin.
// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for Forge" }
}

dependencies {
    forge(libs.forge)

    compileOnly(libs.mixinextras.common)
    annotationProcessor(libs.mixinextras.common)
    implementation(libs.mixinextras.forge)
    include(libs.mixinextras.forge)

    implementation(libs.kotlinforforge)

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin(libs.dokka.plugin.java.syntax)
    }
}

tasks {
    jar {
        archiveClassifier = "dev"
    }

    processResources {
        filesMatching("META-INF/mods.toml") {
            expand(
                mutableMapOf(
                    "version" to version as String,
                    "forge" to libs.versions.forge.get(),
                    "kotlin_for_forge" to libs.versions.kotlinforforge.get(),
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

    dokkaHtml {
        moduleName = "Staff Mod"
        moduleVersion = version as String
        outputDirectory = layout.buildDirectory.dir(
            if (project.hasProperty("javaSyntax")) "docs/javaHtml"
            else "docs/kotlinHtml"
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage =
                "© 2023-${Year.now().value} opekope2. AvM Staff Mod is not an official Minecraft product. Not associated with or endorsed by Mojang Studios."
            customAssets = listOf(projectDir.resolve("logo-icon.svg"))
            separateInheritedMembers = true
        }

        dokkaSourceSets.configureEach {
            documentedVisibilities = setOf(
                DokkaConfiguration.Visibility.PUBLIC,
                DokkaConfiguration.Visibility.PROTECTED
            )

            perPackageOption {
                matchingRegex = """opekope2\.avm_staff\.internal(\..*)?"""
                suppress = true
            }

            perPackageOption {
                matchingRegex = """opekope2\.avm_staff\.mixin"""
                suppress = true
            }

            sourceLink {
                localDirectory = projectDir.resolve("src/main")
                remoteUrl = uri("https://github.com/opekope2/StaffMod/tree/$version/StaffMod/src/main").toURL()
                remoteLineSuffix = "#L"
            }

            externalDocumentationLink {
                val mappingsVersion = libs.versions.yarn.get()
                url = uri("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/").toURL()
                packageListUrl = uri("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list").toURL()
            }
            externalDocumentationLink {
                url = uri("https://joml-ci.github.io/JOML/apidocs/").toURL()
                packageListUrl = uri("https://joml-ci.github.io/JOML/apidocs/element-list").toURL()
            }

            // Apply these last, otherwise the other options get ignored
            // You don't want to know how many hours I spent on this...
            jdkVersion = libs.versions.java.get().toInt()
            languageVersion = libs.versions.kotlin.get()
        }
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
