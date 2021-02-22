repositories {
    jcenter()
    maven("https://maven.fabricmc.net/")
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    val loomVersion: String by project
    implementation("fabric-loom:fabric-loom.gradle.plugin:$loomVersion")
}

