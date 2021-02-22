package com.remodstudios.yarnandneedles

plugins {
    id("com.remodstudios.yarnandneedles.java-conventions")
    id("fabric-loom")
    `maven-publish`
}

val minecraftVersion: String by rootProject
val yarnVersion: String by rootProject
val loaderVersion: String by rootProject
val fabricApiVersion: String by rootProject

group = "${rootProject.group}.${rootProject.name}"

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnVersion:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

tasks {
    val mainSourceSet = sourceSets.main.get()

    processResources {
        inputs.property("version", version)

        // expand ${version} macro
        from(mainSourceSet.resources.srcDirs) {
            include("fabric.mod.json")
            expand(mutableMapOf("version" to version))
        }

        // excludes the original fabric.mod.json
        from(mainSourceSet.resources.srcDirs) {
            exclude("fabric.mod.json")
        }
    }

    withType<JavaCompile> { options.encoding = "UTF-8" }

    register<Jar>("sourcesJar") {
        dependsOn("classes")
        archiveClassifier.set("sources")
        from(mainSourceSet.allSource)
    }

    // include license and readme files
    jar { from("LICENSE", "README.md") }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifact(tasks["jar"]) {
                builtBy(tasks["remapJar"])
            }
            artifact("${buildDir.absolutePath}/libs/${project.name}-${project.version}.jar"){
                builtBy(tasks["remapJar"])
            }
            artifact(tasks["sourcesJar"]) {
                builtBy(tasks["remapSourcesJar"])
            }
        }
    }
}