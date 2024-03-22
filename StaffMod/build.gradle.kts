import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.time.Year

plugins {
    alias(libs.plugins.dokka)
}

architectury {
    common("fabric", "forge")
}

repositories {}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    modApi(libs.architectury)

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin(libs.dokka.plugin.java.syntax)
    }
}

loom {
    accessWidenerPath = file("src/main/resources/avm_staff.accesswidener")
}

tasks {
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
                remoteUrl = uri("https://github.com/opekope2/StaffMod/tree/$version/StaffMod/src/main").toURL()
                remoteLineSuffix = "#L"
            }

            externalDocumentationLink {
                val mappingsVersion = libs.versions.yarn.get()
                url = uri("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/").toURL()
                packageListUrl = uri("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list").toURL()
            }
            externalDocumentationLink {
                val fabricVersion = libs.versions.fabric.api.get()
                url = uri("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/").toURL()
                packageListUrl = uri("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/element-list").toURL()
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
