plugins {
    id("com.remodstudios.yarnandneedles.module-conventions")
}

val autoServiceVersion: String by project
val autoCommonVersion: String by project
val javapoetVersion: String by project

dependencies {
    implementation(project(":datagen"))

    compileOnly("com.google.auto.service:auto-service-annotations:$autoServiceVersion")
    annotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")
    implementation("com.google.auto:auto-common:$autoCommonVersion")
    implementation("com.squareup:javapoet:$javapoetVersion")
}