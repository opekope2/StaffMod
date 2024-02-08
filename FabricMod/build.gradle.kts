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
    modImplementation("net.fabricmc", "fabric-loader", project.extra["fabric_loader_version"] as String)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", project.extra["fabric_api_version"] as String)
    modApi("dev.architectury", "architectury-fabric", project.extra["architectury_api_version"] as String)

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":StaffMod", configuration = "transformProductionFabric")) { isTransitive = false }

    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String
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
                    "version" to project.extra["mod_version"] as String,
                    "fabric_api" to project.extra["fabric_api_version"] as String,
                    "fabric_language_kotlin" to project.extra["fabric_language_kotlin_version"] as String,
                    "architectury" to project.extra["architectury_api_version"] as String,
                    "minecraft" to project.extra["minecraft_version"] as String,
                    "java" to project.extra["java_version"] as String
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
