import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.net.URL
import java.time.Year

plugins {
    id("fabric-loom")
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

base {
    archivesName = project.extra["archives_base_name"] as String
}

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

repositories {}

dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, classifier = "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.extra["loader_version"] as String)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", project.extra["fabric_version"] as String)
    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String
    )

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin("org.jetbrains.dokka", "kotlin-as-java-plugin", project.extra["dokka_version"] as String)
    }
}

loom {
    accessWidenerPath = file("src/main/resources/avm_staff.accesswidener")
}

tasks {
    val javaVersion = JavaVersion.toVersion((project.extra["java_version"] as String).toInt())

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release = javaVersion.toString().toInt()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
        }
    }

    jar {
        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.extra["mod_version"] as String,
                    "fabricloader" to project.extra["loader_version"] as String,
                    "fabric_api" to project.extra["fabric_version"] as String,
                    "fabric_language_kotlin" to project.extra["fabric_language_kotlin_version"] as String,
                    "minecraft" to project.extra["minecraft_version"] as String,
                    "java" to project.extra["java_version"] as String
                )
            )
        }
        filesMatching("*.mixins.json") {
            expand(
                mutableMapOf(
                    "java" to project.extra["java_version"] as String
                )
            )
        }
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion.toString())
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }

    dokkaHtml {
        moduleName = "Staff Mod"
        moduleVersion = version as String
        outputDirectory = layout.buildDirectory.dir(
            if (project.hasProperty("javaSyntax")) "docs/javaHtml"
            else "docs/kotlinHtml"
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "© 2023-${Year.now().value} opekope2. ${project.extra["mojank_eula_compliance_footer"]}"
            separateInheritedMembers = true
        }

        dokkaSourceSets.configureEach {
            documentedVisibilities = setOf(
                DokkaConfiguration.Visibility.PUBLIC,
                DokkaConfiguration.Visibility.PROTECTED
            )

            perPackageOption {
                matchingRegex = ".*internal.*"
                suppress = true
            }

            perPackageOption {
                matchingRegex = ".*mixin.*"
                suppress = true
            }

            sourceLink {
                localDirectory = projectDir.resolve("src/main")
                remoteUrl = URL("https://github.com/opekope2/StaffMod/tree/$version/StaffMod/src/main")
                remoteLineSuffix = "#L"
            }

            externalDocumentationLink {
                val mappingsVersion = project.extra["yarn_mappings"]
                url = URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/")
                packageListUrl = URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list")
            }
            externalDocumentationLink {
                val fabricVersion = project.extra["fabric_version"]
                url = URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/")
                packageListUrl = URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/element-list")
            }

            // Apply these last, otherwise the other options get ignored
            // You don't want to know how many hours I spent on this...
            jdkVersion = project.extra["java_version"] as Int
            languageVersion = System.getProperty("kotlin_version")
        }
    }
}
