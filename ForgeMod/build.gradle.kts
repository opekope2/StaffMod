plugins {
    alias(libs.plugins.shadow)
}

evaluationDependsOn(":StaffMod")

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
    modApi(libs.architectury.forge)

    compileOnly(libs.mixinextras.common)
    annotationProcessor(libs.mixinextras.common)
    implementation(libs.mixinextras.forge)
    include(libs.mixinextras.forge)

    common(project(":StaffMod", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":StaffMod", configuration = "transformProductionForge")) { isTransitive = false }

    implementation(libs.kotlinforforge)
}

loom {
    accessWidenerPath = project(":StaffMod").loom.accessWidenerPath

    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("avm_staff.mixins.json")
    }

    mods {
        val avm_staff by registering {
            sourceSet("main")
        }
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
                    "architectury" to libs.versions.architectury.api.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to libs.versions.java.get()
                )
            )
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
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
