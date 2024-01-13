// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka", "dokka-base", project.extra["dokka_version"] as String)
    }
}
