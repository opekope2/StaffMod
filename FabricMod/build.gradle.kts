plugins {
    id("com.github.johnrengelman.shadow")
}

evaluationDependsOn(":StaffMod")

architectury {
    platformSetupLoomIde()
    fabric()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin since it *excludes* files.
val developmentFabric: Configuration by configurations.getting

configurations {
    compileClasspath.configure {
        extendsFrom(common)
    }
    runtimeClasspath.configure {
        extendsFrom(common)
    }
    developmentFabric.extendsFrom(common)
}

repositories {
}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc", "fabric-loader", project.gradleProperty("fabric_loader_version"))
    modImplementation("net.fabricmc.fabric-api", "fabric-api", project.gradleProperty("fabric_api_version"))
    modApi("dev.architectury", "architectury-fabric", project.gradleProperty("architectury_api_version"))

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":StaffMod", configuration = "transformProductionFabric")) { isTransitive = false }

    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.gradleProperty("fabric_language_kotlin_version")
    )
}

loom {
    accessWidenerPath = project(":StaffMod").loom.accessWidenerPath
}

tasks {
    jar {
        archiveClassifier = "dev"
    }

    remapJar {
        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "fabric_api" to project.gradleProperty("fabric_api_version"),
                    "fabric_language_kotlin" to project.gradleProperty("fabric_language_kotlin_version"),
                    "architectury" to project.gradleProperty("architectury_api_version"),
                    "minecraft" to project.gradleProperty("minecraft_version"),
                    "java" to project.gradleProperty("java_version")
                )
            )
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier = "dev-shadow"
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        injectAccessWidener = true
        archiveClassifier = null
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
