plugins {
    id("com.github.johnrengelman.shadow")
}

evaluationDependsOn(":StaffMod")

architectury {
    platformSetupLoomIde()
    forge()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin since it *excludes* files.
val developmentForge: Configuration by configurations.getting

configurations {
    compileClasspath.configure {
        extendsFrom(common)
    }
    runtimeClasspath.configure {
        extendsFrom(common)
    }
    developmentForge.extendsFrom(common)
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for Forge" }
}

dependencies {
    forge("net.minecraftforge", "forge", project.extra["forge_version"] as String)
    modApi("dev.architectury", "architectury-forge", project.extra["architectury_api_version"] as String)

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":StaffMod", configuration = "transformProductionForge")) { isTransitive = false }

    implementation("thedarkcolour", "kotlinforforge", project.extra["kotlin_for_forge_version"] as String)
}

loom {
    accessWidenerPath = project(":StaffMod").loom.accessWidenerPath

    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("avm_staff.mixins.json")
        mixinConfig("avm_staff_forge.mixins.json")
    }
}

tasks {
    jar {
        archiveClassifier = "dev"
    }

    remapJar {
        from(projectDir.resolve("Fabric.license"))
        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
    }

    processResources {
        filesMatching("META-INF/mods.toml") {
            expand(
                mutableMapOf(
                    "version" to project.extra["mod_version"] as String,
                    "forge" to project.extra["forge_version"] as String,
                    "kotlin_for_forge" to project.extra["kotlin_for_forge_version"] as String,
                    "architectury" to project.extra["architectury_api_version"] as String,
                    "minecraft" to project.extra["minecraft_version"] as String,
                    "java" to project.extra["java_version"] as String
                )
            )
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
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

// FIXME A random code I copied from Architectury Discord, because Forge has skill issue. They said that this is a bad practice. I am no gradle expert. IDEA complains about duplicate content root. Adapted to Kotlin.
sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourceSets/${it.name}")
    it.resources.srcDir(dir)
    it.java.srcDir(dir)
    it.kotlin.srcDir(dir)
}
