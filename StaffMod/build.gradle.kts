import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.net.URL
import java.time.Year

plugins {
    id("org.jetbrains.dokka")
}

architectury {
    common("fabric", "forge")
}

repositories {}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc", "fabric-loader", project.extra["fabric_loader_version"] as String)
    modApi("dev.architectury", "architectury", project.extra["architectury_api_version"] as String)

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin("org.jetbrains.dokka", "kotlin-as-java-plugin", project.extra["dokka_version"] as String)
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
                val fabricVersion = project.extra["fabric_api_version"]
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
