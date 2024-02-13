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
    forge("net.minecraftforge", "forge", project.gradleProperty("forge_version"))
    modApi("dev.architectury", "architectury-forge", project.gradleProperty("architectury_api_version"))

    compileOnly(
        annotationProcessor("io.github.llamalad7", "mixinextras-common", project.gradleProperty("mixin_extras_version"))
    )
    implementation(include("io.github.llamalad7", "mixinextras-forge", project.gradleProperty("mixin_extras_version")))

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":StaffMod", configuration = "transformProductionForge")) { isTransitive = false }

    implementation("thedarkcolour", "kotlinforforge", project.gradleProperty("kotlin_for_forge_version"))
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

    processResources {
        filesMatching("META-INF/mods.toml") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "forge" to project.gradleProperty("forge_version"),
                    "kotlin_for_forge" to project.gradleProperty("kotlin_for_forge_version"),
                    "architectury" to project.gradleProperty("architectury_api_version"),
                    "minecraft" to project.gradleProperty("minecraft_version"),
                    "java" to project.gradleProperty("java_version")
                )
            )
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier = "dev-shadow"

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
        from(projectDir.resolve("Fabric.license"))
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        injectAccessWidener = true
        archiveClassifier = null

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
        from(projectDir.resolve("Fabric.license"))
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
