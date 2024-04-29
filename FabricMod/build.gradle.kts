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
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile = shadowJar.get().archiveFile
        injectAccessWidener = true
        archiveClassifier = null

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
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
